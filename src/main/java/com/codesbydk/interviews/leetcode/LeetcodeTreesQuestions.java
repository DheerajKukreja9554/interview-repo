package com.codesbydk.interviews.leetcode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;
import java.util.concurrent.LinkedBlockingQueue;

public class LeetcodeTreesQuestions {

    public List<Integer> dfs(TreeNode root) {

        Stack<TreeNode> stack = new Stack<>();
        List<Integer> result = new ArrayList<>();

        if (root == null) {
            return result;
        }

        stack.push(root);
        while (!stack.isEmpty()) {
            TreeNode current = stack.pop();
            result.add(current.val);

            if (current.right != null)
                stack.push(current.right);
            if (current.left != null)
                stack.push(current.left);
        }
        return result;

    }

    public List<Integer> dfsRecursive(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        if (root == null) {
            return result;
        }

        result.add(root.val);
        result.addAll(dfsRecursive(root.left));
        result.addAll(dfsRecursive(root.right));

        return result;

    }

    public List<Integer> bfs(TreeNode root) {
        Queue<TreeNode> stack = new LinkedBlockingQueue<>();
        List<Integer> result = new ArrayList<>();

        if (root == null) {
            return result;
        }

        stack.add(root);
        while (!stack.isEmpty()) {
            TreeNode current = stack.poll();
            result.add(current.val);
            if (current.left != null)
                stack.add(current.left);
            if (current.right != null)
                stack.add(current.right);

        }
        return result;

    }

    public List<Integer> dfsPathRecursive(TreeNode node, int target) {
        List<Integer> path = new LinkedList<>();
        if (node == null)
            return null;

        path.add(node.val); // add current node
        if (node.val == target)
            return path; // target found

        // search left or right
        List<Integer> nodeSearch = dfsPathRecursive(node.left, target);
        if (nodeSearch == null || nodeSearch.isEmpty()) {
            nodeSearch = dfsPathRecursive(node.right, target);
        }
        System.out.println(nodeSearch);
        if (nodeSearch == null || nodeSearch.isEmpty())
            return null;
        path.addAll(nodeSearch);
        return path;
    }

    public TreeNode bftToTree(Integer[] tree) {
        TreeNode[] nodes = new TreeNode[tree.length];
        for (int i = 0; i < tree.length; i++) {
            if (tree[i] != null) {
                TreeNode node = new TreeNode(tree[i]);
                nodes[i] = node;
                boolean even = i % 2 == 0;
                int parentIndex = (even ? i - 2 : i - 1) / 2;
                if (parentIndex >= 0 && nodes[parentIndex] != null) {
                    if (even) {
                        nodes[parentIndex].right = node;
                    } else
                        nodes[parentIndex].left = node;
                }
            } else
                nodes[i] = (null);
        }
        return nodes[0];
    }

    public List<Integer> inorder(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        if (root.left != null)
            result.addAll(inorder(root.left));
        result.add(root.val);
        if (root.right != null)
            result.addAll(inorder(root.right));
        return result;
    }

    public List<Integer> preorder(TreeNode root) {
        List<Integer> result = new LinkedList<>();
        List<TreeNode> stack = new LinkedList<>();
        stack.add(root);
        while (!stack.isEmpty()) {
            TreeNode node = stack.removeLast();
            result.add(node.val);

            if (node.right != null)
                stack.addLast(node.right);
            if (node.left != null)
                stack.addLast(node.left);
        }
        return result;
    }

    public List<Integer> preorderRecurse(TreeNode root) {
        List<Integer> result = new LinkedList<>();
        if (root == null)
            return result;

        result.add(root.val);
        result.addAll(preorderRecurse(root.left));
        result.addAll(preorderRecurse(root.right));
        return result;
    }

    public boolean isValidBST(TreeNode root) {
        return isValid(root, Long.MIN_VALUE, Long.MAX_VALUE);
    }

    private boolean isValid(TreeNode node, long min, long max) {
        if (node == null)
            return true;
        if (node.val <= min || node.val >= max)
            return false;
        return isValid(node.left, min, node.val) && isValid(node.right, node.val, max);
    }

    public boolean isSameTree(TreeNode p, TreeNode q) {
        if (p == null && q == null) {
            return true;
        }
        if (p == null || q == null || p.val != q.val)
            return false;

        return isSameTree(p.left, q.left) && isSameTree(p.right, q.right);
    }

    public List<List<Integer>> levelOrder(TreeNode root) {
        List<List<Integer>> result = new ArrayList<>();
        if (root == null) {
            return null;
        }
        List<TreeNode> currentQueue = new ArrayList<>();
        List<TreeNode> nextQueue = new ArrayList<>();
        List<Integer> temp = new ArrayList<>();
        currentQueue.add(root);
        while (!currentQueue.isEmpty() || !nextQueue.isEmpty()) {

            TreeNode current = currentQueue.removeFirst();
            temp.add(current.val);
            if (current.left != null)
                nextQueue.add(current.left);
            if (current.right != null)
                nextQueue.add(current.right);
            // System.out.println("Current queue: %s; next queue:
            // %s".formatted(currentQueue, nextQueue));
            if (currentQueue.isEmpty()) {
                result.add(temp);
                temp = new ArrayList<>();
                currentQueue.addAll(nextQueue);
                nextQueue.clear();
            }
        }
        // result.add(temp);

        return result;
    }

    public List<List<Integer>> zigzagLevelOrder(TreeNode root) {
        List<List<Integer>> result = new ArrayList<>();
        if (root == null) {
            return null;
        }
        List<TreeNode> currentQueue = new ArrayList<>();
        List<TreeNode> nextQueue = new ArrayList<>();
        List<Integer> temp = new ArrayList<>();
        currentQueue.add(root);
        boolean reverse = false;
        while (!currentQueue.isEmpty() || !nextQueue.isEmpty()) {

            TreeNode current = currentQueue.removeFirst();
            temp.add(current.val);
            if (current.left != null)
                nextQueue.add(current.left);
            if (current.right != null)
                nextQueue.add(current.right);
            // System.out.println("Current queue: %s; next queue:
            // %s".formatted(currentQueue, nextQueue));
            if (currentQueue.isEmpty()) {
                if (reverse) {
                    result.add(temp.reversed());
                } else
                    result.add(temp);

                reverse = !reverse;
                temp = new ArrayList<>();
                currentQueue.addAll(nextQueue);
                nextQueue.clear();
            }
        }
        // result.add(temp);

        return result;
    }

    public Integer maxDepth(TreeNode root) {
        if (root == null) {
            return 0;
        }

        return 1 + Math.max(maxDepth(root.left), maxDepth(root.right));
    }

    public TreeNode sortedArrayToBST(int[] nums) {

        int low = 0;
        int high = nums.length - 1;
        if (low <=high) {
            return sortedArrayToBST(nums, low, high);
        }

        return new TreeNode();

    }

    public TreeNode sortedArrayToBST(int[] nums, int low, int high) {
        if (low > high) {
            return null;
        }
        TreeNode node = new TreeNode();
        int mid = (low + high) / 2;
        node.val = nums[mid];
        node.left = sortedArrayToBST(nums, low, mid - 1);
        node.right = sortedArrayToBST(nums, mid + 1, high);
        return node;

    }

    public boolean hasPathSum(TreeNode root, int targetSum) {
        if (root == null) {
            return false;
        }

        if (root.left == null && root.right == null) {
            return targetSum == root.val;
        }

        boolean leftSum = hasPathSum(root.left, targetSum - root.val);
        boolean rightSum = hasPathSum(root.right, targetSum - root.val);

        return leftSum || rightSum;
    }
    

    public List<List<Integer>> pathSum(TreeNode root, int targetSum) {
    
        if (root == null) {
            return null;
        }
        List<List<Integer>> path = new ArrayList<>();
        if (root.left == null && root.right == null && root.val == targetSum) {
            
            return path;
        }
        List<List<Integer>> pathLeft = pathSum(root.left, targetSum - root.val);
        List<List<Integer>> pathRight = pathSum(root.right, targetSum - root.val);
        if (pathLeft != null) {
            pathLeft.forEach(p -> p.addFirst(root.val));
            path.addAll(pathLeft);
        }
        if (pathRight != null) {
            pathRight.forEach(p -> p.addFirst(root.val));
            path.addAll(pathRight);
        }
        // pathRight.forEach(p-> p.addFirst(root.val));
        
        return path;
    }

    public static void main(String[] args) {
        LeetcodeTreesQuestions sa = new LeetcodeTreesQuestions();

        TreeNode root = new TreeNode(1);
        root.left = new TreeNode(2);
        root.right = new TreeNode(3);

        root.left.left = new TreeNode(4);
        root.left.right = new TreeNode(5);

        root.right.left = new TreeNode(6);
        root.right.right = new TreeNode(7);

        root.left.left.left = new TreeNode(8);
        root.left.right.right = new TreeNode(9);

        root.right.left.left = new TreeNode(10);
        root.right.right.left = new TreeNode(11);

        // List<Integer> result = new ArrayList<>();

        // System.out.println(sa.dfsPathRecursive(root, 10));
        // System.out.println(result);
        // System.out.println(sa.dfs(root));

        // Integer[] bfsNull = new Integer[] { 1, 2, 3, 4, 5, 6, 7, 8, null, null, 9,
        // 10, null, null, 11 };
        System.out.println(sa.bfs(root) );
        // System.out.println(sa.preorder(root));
        // System.out.println(sa.zigzagLevelOrder(root));
        // System.out.println(sa.maxDepth(root));

        // System.out.println(sa.bfs(sa.sortedArrayToBST(new int[] {1,2,3,4,5,6,7})));
        System.out.println(sa.hasPathSum(root, 20));
        System.out.println(sa.pathSum(root, 20));

    }

}

// 1
// 2 3
// 4 5 6 7
// 8 9 10 11
//
//
//
//
