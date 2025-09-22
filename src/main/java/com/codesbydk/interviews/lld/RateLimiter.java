/*
 * Low-Level Design (LLD) for Token Bucket and Leaky Bucket rate limiters in Java
 * Package: com.example.ratelimiter
 *
 * Now includes:
 *  - Timeline sequence diagrams (ASCII style) for TokenBucket and LeakyBucket
 *  - Explanation of Sliding Window Rate Limiter
 *
 * Contents:
 *  - RateLimiter (interface)
 *  - TokenBucketRateLimiter (token bucket implementation, lazy refill)
 *  - LeakyBucketRateLimiter (leaky bucket implementation, scheduled draining)
 *  - SlidingWindowRateLimiter (time-based counters)
 *  - Usage examples and ASCII flow diagrams
 */

package com.codesbydk.interviews.lld;

import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Common RateLimiter interface that all implementations implement.
 */
public interface RateLimiter {
    boolean tryAcquire(long permits);
    boolean tryAcquire(long permits, long timeout, TimeUnit unit) throws InterruptedException;
    void acquire(long permits) throws InterruptedException;
    long getCapacity();
    double getRatePerSecond();
}

/* ========================= Token Bucket ========================= */
/**
 * Token Bucket Rate Limiter.
 *
 * Timeline Example:
 * Capacity = 10, Refill = 5/sec
 *
 * t=0s  [##########] (10 tokens)
 * t=1s  [#####     ] (5 used, 5 left)
 * t=2s  [########  ] (refill 5, capped at 10)
 * t=3s  Request 8 → succeeds if >=8 available, otherwise rejected
 */
class TokenBucketRateLimiter implements RateLimiter {
    private final long capacity;
    private final double refillTokensPerSec;
    private double availableTokens;
    private long lastRefillTimeNanos;

    private final Lock lock = new ReentrantLock();
    private final Condition tokensAvailable = lock.newCondition();
    private final AtomicLong granted = new AtomicLong();
    private final AtomicLong rejected = new AtomicLong();

    public TokenBucketRateLimiter(long capacity, double refillTokensPerSec) {
        if (capacity <= 0 || refillTokensPerSec <= 0) throw new IllegalArgumentException("capacity and rate must be > 0");
        this.capacity = capacity;
        this.refillTokensPerSec = refillTokensPerSec;
        this.availableTokens = capacity;
        this.lastRefillTimeNanos = System.nanoTime();
    }

    private void refill() {
        long now = System.nanoTime();
        long elapsedNanos = now - lastRefillTimeNanos;
        if (elapsedNanos <= 0) return;
        double tokensToAdd = (elapsedNanos / 1_000_000_000.0) * refillTokensPerSec;
        if (tokensToAdd > 0) {
            availableTokens = Math.min(capacity, availableTokens + tokensToAdd);
            lastRefillTimeNanos = now;
        }
    }

    @Override
    public boolean tryAcquire(long permits) {
        if (permits <= 0) throw new IllegalArgumentException("permits must be > 0");
        lock.lock();
        try {
            refill();
            if (availableTokens >= permits) {
                availableTokens -= permits;
                granted.incrementAndGet();
                return true;
            } else {
                rejected.incrementAndGet();
                return false;
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean tryAcquire(long permits, long timeout, TimeUnit unit) throws InterruptedException {
        long nanos = unit.toNanos(timeout);
        long deadline = System.nanoTime() + nanos;
        lock.lockInterruptibly();
        try {
            while (true) {
                refill();
                if (availableTokens >= permits) {
                    availableTokens -= permits;
                    granted.incrementAndGet();
                    return true;
                }
                long now = System.nanoTime();
                long remaining = deadline - now;
                if (remaining <= 0) {
                    rejected.incrementAndGet();
                    return false;
                }
                double tokensNeeded = permits - availableTokens;
                long waitNanos = (long) Math.ceil((tokensNeeded / refillTokensPerSec) * 1_000_000_000L);
                long toWait = Math.min(waitNanos, remaining);
                if (toWait > 0) tokensAvailable.awaitNanos(toWait);
                else Thread.yield();
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void acquire(long permits) throws InterruptedException {
        if (!tryAcquire(permits, Long.MAX_VALUE, TimeUnit.NANOSECONDS)) {
            throw new InterruptedException("Interrupted while waiting for tokens");
        }
    }

    @Override
    public long getCapacity() { return capacity; }
    @Override
    public double getRatePerSecond() { return refillTokensPerSec; }

    public void signalAllWaiters() {
        lock.lock();
        try { tokensAvailable.signalAll(); } finally { lock.unlock(); }
    }
    public long getGrantedCount() { return granted.get(); }
    public long getRejectedCount() { return rejected.get(); }
}

/* ========================= Leaky Bucket ========================= */
/**
 * Leaky Bucket Rate Limiter.
 *
 * Timeline Example:
 * Capacity = 10, Leak = 5/sec
 *
 * t=0s  Queue size=0
 * t=1s  Incoming 8 → queue=8
 * t=2s  Leak 5 → queue=3
 * t=3s  Incoming 6 → queue=9 (<=10 so accepted)
 * t=4s  Leak 5 → queue=4
 */
class LeakyBucketRateLimiter implements RateLimiter, AutoCloseable {
    private final long capacity;
    private final double leakRatePerSecond;
    private final Queue<Long> queue = new ConcurrentLinkedQueue<>();
    private final AtomicLong queuedPermits = new AtomicLong(0);
    private final ScheduledExecutorService scheduler;
    private final long drainIntervalMillis = 100;

    private final Lock lock = new ReentrantLock();
    private final Condition spaceAvailable = lock.newCondition();
    private final AtomicLong accepted = new AtomicLong();
    private final AtomicLong dropped = new AtomicLong();

    public LeakyBucketRateLimiter(long capacity, double leakRatePerSecond) {
        if (capacity <= 0 || leakRatePerSecond <= 0) throw new IllegalArgumentException("capacity and rate must be > 0");
        this.capacity = capacity;
        this.leakRatePerSecond = leakRatePerSecond;
        this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "leaky-bucket-drainer");
            t.setDaemon(true);
            return t;
        });
        scheduler.scheduleAtFixedRate(this::drain, drainIntervalMillis, drainIntervalMillis, TimeUnit.MILLISECONDS);
    }

    private void drain() {
        try {
            double permitsPerInterval = (leakRatePerSecond * drainIntervalMillis) / 1000.0;
            long toDrain = (long) Math.floor(permitsPerInterval);
            for (long i = 0; i < toDrain; i++) {
                Long p = queue.poll();
                if (p == null) break;
                queuedPermits.decrementAndGet();
            }
            if (toDrain > 0) {
                lock.lock();
                try { spaceAvailable.signalAll(); } finally { lock.unlock(); }
            }
        } catch (Throwable t) { t.printStackTrace(); }
    }

    @Override
    public boolean tryAcquire(long permits) {
        if (permits <= 0) throw new IllegalArgumentException("permits must be > 0");
        while (true) {
            long current = queuedPermits.get();
            long desired = current + permits;
            if (desired > capacity) { dropped.incrementAndGet(); return false; }
            if (queuedPermits.compareAndSet(current, desired)) {
                for (int i = 0; i < permits; i++) queue.offer(1L);
                accepted.incrementAndGet();
                return true;
            }
        }
    }

    @Override
    public boolean tryAcquire(long permits, long timeout, TimeUnit unit) throws InterruptedException {
        long deadline = System.nanoTime() + unit.toNanos(timeout);
        lock.lockInterruptibly();
        try {
            while (true) {
                if (tryAcquire(permits)) return true;
                long now = System.nanoTime();
                long remaining = deadline - now;
                if (remaining <= 0) return false;
                spaceAvailable.awaitNanos(remaining);
            }
        } finally { lock.unlock(); }
    }

    @Override
    public void acquire(long permits) throws InterruptedException {
        if (!tryAcquire(permits, Long.MAX_VALUE, TimeUnit.NANOSECONDS)) {
            throw new InterruptedException("Interrupted while waiting to enqueue permits");
        }
    }

    @Override
    public long getCapacity() { return capacity; }
    @Override
    public double getRatePerSecond() { return leakRatePerSecond; }
    public long getQueuedPermits() { return queuedPermits.get(); }
    public long getAcceptedCount() { return accepted.get(); }
    public long getDroppedCount() { return dropped.get(); }
    @Override
    public void close() { scheduler.shutdownNow(); }
}

/* ========================= Sliding Window ========================= */
/**
 * Sliding Window Rate Limiter:
 * Instead of buckets or queues, it divides time into windows (e.g., 1 sec) and maintains counts.
 * Each request checks how many requests were served in the last N milliseconds.
 *
 * Timeline Example:
 * Limit = 5/sec
 *
 * t=0.1s → req1 (allowed)
 * t=0.2s → req2 (allowed)
 * t=0.3s → req3 (allowed)
 * t=0.4s → req4 (allowed)
 * t=0.5s → req5 (allowed)
 * t=0.6s → req6 (rejected, 5 already in [t=-0.4,0.6])
 */
class SlidingWindowRateLimiter implements RateLimiter {
    private final long capacity;
    private final long windowMillis;
    private final ConcurrentLinkedQueue<Long> timestamps = new ConcurrentLinkedQueue<>();

    public SlidingWindowRateLimiter(long capacity, long windowMillis) {
        if (capacity <= 0 || windowMillis <= 0) throw new IllegalArgumentException();
        this.capacity = capacity;
        this.windowMillis = windowMillis;
    }

    @Override
    public boolean tryAcquire(long permits) {
        long now = System.currentTimeMillis();
        long windowStart = now - windowMillis;
        while (!timestamps.isEmpty() && timestamps.peek() < windowStart) {
            timestamps.poll();
        }
        if (timestamps.size() + permits > capacity) return false;
        for (int i = 0; i < permits; i++) timestamps.add(now);
        return true;
    }

    @Override
    public boolean tryAcquire(long permits, long timeout, TimeUnit unit) { return tryAcquire(permits); }
    @Override
    public void acquire(long permits) throws InterruptedException { while (!tryAcquire(permits)) Thread.sleep(10); }
    @Override
    public long getCapacity() { return capacity; }
    @Override
    public double getRatePerSecond() { return 1000.0 / windowMillis * capacity; }
}

/* ========================= Usage / Demo ========================= */
class Demo {
    public static void main(String[] args) throws Exception {
        System.out.println("=== Token Bucket Demo ===");
        TokenBucketRateLimiter tokenBucket = new TokenBucketRateLimiter(10, 5.0);
        System.out.println("Burst tryAcquire(8): " + tokenBucket.tryAcquire(8));
        System.out.println("tryAcquire(8) immediate: " + tokenBucket.tryAcquire(8));

        System.out.println("=== Leaky Bucket Demo ===");
        LeakyBucketRateLimiter leaky = new LeakyBucketRateLimiter(10, 5.0);
        System.out.println("tryAcquire 12: " + leaky.tryAcquire(12));
        System.out.println("tryAcquire 8: " + leaky.tryAcquire(8));
        Thread.sleep(3000);
        System.out.println("Queued permits after drain: " + leaky.getQueuedPermits());
        leaky.close();

        System.out.println("=== Sliding Window Demo ===");
        SlidingWindowRateLimiter sliding = new SlidingWindowRateLimiter(5, 1000);
        for (int i = 0; i < 7; i++) {
            System.out.println("Request " + i + ": " + sliding.tryAcquire(1));
            Thread.sleep(150);
        }
    }
}
