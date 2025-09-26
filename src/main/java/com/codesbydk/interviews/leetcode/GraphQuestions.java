package com.codesbydk.interviews.leetcode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

public class GraphQuestions {

    public int minimumTotal(List<List<Integer>> triangle) {
        int ans = 0;
        for (List<Integer> list : triangle) {
            ans += Collections.min(list);
        }
        return ans;
    }

    public List<Integer> bft(GraphNode root) {

        List<Integer> traversal = new ArrayList<>();

        Queue<GraphNode> queue = new LinkedList<>();
        Set<GraphNode> visited = new HashSet<>();

        queue.add(root);
        while (!queue.isEmpty()) {
            var node = queue.poll();

            if (!visited.contains(node)) {
                traversal.add(node.val);
                queue.addAll(node.neighbors);
                visited.add(node);
            }
        }

        return traversal;
    }

    public List<Integer> dft(GraphNode root) {

        List<Integer> traversal = new ArrayList<>();

        Stack<GraphNode> queue = new Stack<>();
        Set<GraphNode> visited = new HashSet<>();

        queue.add(root);
        while (!queue.isEmpty()) {
            var node = queue.pop();

            if (!visited.contains(node)) {
                traversal.add(node.val);
                queue.addAll(node.neighbors.reversed());
                visited.add(node);
            }
        }

        return traversal;
    }

    public List<Integer> dftRecursive(GraphNode root) {
        List<Integer> traversal = new LinkedList<>();
        Set<GraphNode> visited = new HashSet<>();
        dftRecursiveHelper(root, traversal, visited);
        return traversal;
    }

    public void dftRecursiveHelper(GraphNode root, List<Integer> traversal, Set<GraphNode> visited) {
        if (visited.contains(root)) {
            return;
        }

        traversal.add(root.val);
        visited.add(root);
        for (int i = 0; i < root.neighbors.size(); i++) {
            dftRecursiveHelper(root.neighbors.get(i), traversal, visited);
        }

    }

    /*
     * start from start,0
     * visit nodes
     * - add distance from start if and only if distanceTillNow > start
     * - add adjacent nodes to the queue
     * 
     */
    public Map<WeightedGraphNode, Integer> dijkstra(WeightedGraphNode start) {
        Map<WeightedGraphNode, Integer> distances = new HashMap<>();
        PriorityQueue<Edge> queue = new PriorityQueue<>(Comparator.comparingInt(e -> e.weight));
        distances.put(start, 0);
        queue.add(new Edge(start, 0));

        while (!queue.isEmpty()) {
            Edge edge = queue.poll();
            Integer distanceTillNow = distances.getOrDefault(edge.to, Integer.MAX_VALUE);

            if (distanceTillNow < edge.weight) {
                continue;
            }
            for (Edge e : edge.to.neighbors) {
                int newDistance = distances.get(edge.to) + e.weight;
                if (!distances.containsKey(e.to) || newDistance < distances.get(e.to)) {
                    distances.put(e.to, newDistance);

                }
                queue.add(e);

            }
        }

        // while()
        // System.out.println();

        return distances;
    }

    public List<WeightedGraphNode> dijkstraPath(WeightedGraphNode start, WeightedGraphNode end) {
        Map<WeightedGraphNode, Integer> distances = new HashMap<>();
        Map<WeightedGraphNode, WeightedGraphNode> parents = new HashMap<>();
        PriorityQueue<Edge> queue = new PriorityQueue<>(Comparator.comparingInt(e -> e.weight));
        Set<Edge> visited = new HashSet<>();
        distances.put(start, 0);
        queue.add(new Edge(start, 0));

        while (!queue.isEmpty()) {

            Edge edge = queue.poll();
            Integer distanceTillNow = distances.getOrDefault(edge.to, Integer.MAX_VALUE);
            if (distanceTillNow < edge.weight) {
                continue;
            }
            for (Edge e : edge.to.neighbors) {
                if (!visited.contains(e)) {
                    int newDistance = distances.get(edge.to) + e.weight;
                    if (!distances.containsKey(e.to) || newDistance < distances.get(e.to)) {
                        distances.put(e.to, newDistance);
                        parents.put(e.to, edge.to);
                    }
                    queue.add(e);
                    visited.add(e);
                }

            }
        }
        System.out.println(parents);
        var current = end;
        List<WeightedGraphNode> path = new ArrayList<>();
        path.add(0, current);
        while (current != null && current != start) {
            var parent = parents.getOrDefault(current, null);
            if (parent != null) {
                path.add(0, parent);
            }
            current = parent;
        }
        return path;
    }

    // public void kruskal(WeightedGraphNode node) {
    //     Stack<WeightedGraphNode> stack = new Stack<>();
    //     Set<WeightedGraphNode> visited = new HashSet<>();
    //     List<GraphEdge> edges = new ArrayList<>();
    //     stack.push(node);

    //     while (!stack.isEmpty()) {
    //         var current = stack.pop();
    //         visited.add(current);
    //         current.neighbors.forEach(n -> {
    //             if (!visited.contains(n.to)) {
    //                 stack.push(n.to);
    //                 edges.add(new GraphEdge(current, n.to, n.weight));
    //             }
    //         });

    //     }
    //     // System.out.println(edges + ": " + edges.size());
    //     // System.out.println(visited + ": " + v.size());
    // }

    public static void main(String[] args) {

        GraphQuestions ga = new GraphQuestions();
        // Create nodes
        GraphNode node0 = new GraphNode(0);
        GraphNode node1 = new GraphNode(1);
        GraphNode node2 = new GraphNode(2);
        GraphNode node3 = new GraphNode(3);
        GraphNode node4 = new GraphNode(4);
        GraphNode node5 = new GraphNode(5);

        // Create undirected edges
        node0.neighbors.add(node1);
        node0.neighbors.add(node2);

        node1.neighbors.add(node0);
        node1.neighbors.add(node3);
        node1.neighbors.add(node4);

        node2.neighbors.add(node0);
        node2.neighbors.add(node4);

        node3.neighbors.add(node1);
        node3.neighbors.add(node5);

        node4.neighbors.add(node1);
        node4.neighbors.add(node2);
        node4.neighbors.add(node5);

        node5.neighbors.add(node3);
        node5.neighbors.add(node4);

        // Run traversals
        System.out.println(ga.bft(node0));
        System.out.println(ga.dft(node0));
        System.out.println(ga.dftRecursive(node0));
        // dfs(node0);
        // dfsIterative(node0);

        WeightedGraphNode n0 = new WeightedGraphNode(0);
        WeightedGraphNode n1 = new WeightedGraphNode(1);
        WeightedGraphNode n2 = new WeightedGraphNode(2);
        WeightedGraphNode n3 = new WeightedGraphNode(3);

        n0.neighbors.add(new Edge(n1, 2));
        n0.neighbors.add(new Edge(n2, 4));
        n1.neighbors.add(new Edge(n2, 1));
        n1.neighbors.add(new Edge(n3, 7));
        n2.neighbors.add(new Edge(n3, 3));

        Map<WeightedGraphNode, Integer> shortestPaths = ga.dijkstra(n0);
        for (var entry : shortestPaths.entrySet()) {
            System.out.println("Distance from 0 to " + entry.getKey().val + " = " + entry.getValue());
        }

        System.out.println("=".repeat(10));

        WeightedGraphNode a0 = new WeightedGraphNode(0);
        WeightedGraphNode a1 = new WeightedGraphNode(1);
        WeightedGraphNode a2 = new WeightedGraphNode(2);
        WeightedGraphNode a3 = new WeightedGraphNode(3);
        WeightedGraphNode a4 = new WeightedGraphNode(4);

        a0.neighbors.add(new Edge(a1, 4));
        a0.neighbors.add(new Edge(a2, 2));
        a1.neighbors.add(new Edge(a0, 4));
        a2.neighbors.add(new Edge(a0, 2));
        a1.neighbors.add(new Edge(a2, 1));
        a2.neighbors.add(new Edge(a1, 1));
        a1.neighbors.add(new Edge(a3, 5));
        a3.neighbors.add(new Edge(a1, 5));
        a1.neighbors.add(new Edge(a4, 7));
        a4.neighbors.add(new Edge(a1, 7));
        a2.neighbors.add(new Edge(a3, 3));
        a3.neighbors.add(new Edge(a2, 3));

        // Map<WeightedGraphNode, Integer> shortestPathsForA = ga.dijkstraPath(a0, a4);
        // for (var entry : shortestPathsForA.entrySet()) {
        // System.out.println("Distance from 0 to " + entry.getKey().val + " = " +
        // entry.getValue());
        // }
        System.out.println(ga.dijkstraPath(a0, a4));

        ga.kruskal(a0);
        // ga.kruskal(a4);
        // ga.kruskal(n0);
    }

}

// 0 -> (1,2), (2,4)
// 1 -> (2,1), (3,7)
// 2 -> (3,3)
// 3
//
//

/*
 *          (0)
 *          / \
 *        4/   \2
 *        /     \
 *      (1)----->(2)
 *      |  \7
 *      |   \
 *      5    \
 *      (3) (4)
 * 
 */
