package com.codesbydk.interviews.leetcode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.LinkedBlockingQueue;

public class LeetcodeQuestions {

    public int reverse(int x) {

        int num = Math.abs(x);
        int rev = 0;
        while (num > 0) {
            int ld = num % 10;
            if (rev > (Integer.MAX_VALUE - ld) / 10) {
                return 0;
            }
            rev = rev * 10 + num % 10;
            num /= 10;

        }
        if (rev > Math.pow(2, 31) - 1)
            return 0;
        return x < 0 ? rev * -1 : rev;
    }

    public int myAtoi(String s) {
        s = s.trim();
        boolean neg = false;

        if (s.charAt(0) == '-') {
            neg = true;
            s = s.substring(1);
        }
        int ans = 0;

        for (int i = 0; i < s.length(); i++) {
            int c = s.charAt(i);
            if (c < 48 || c > 57) {
                return neg ? ans * -1 : ans;
            }
            ans = ans * 10 + c - 48;
        }
        return neg ? ans * -1 : ans;
    }

    public int maxArea(int[] height) {
        int left = 0, right = height.length - 1, area = 0;

        while (left < right) {
            int temp = Math.min(height[left], height[right]) * (right - left);
            area = Math.max(area, temp);
            if (height[left] < height[right])
                left++;
            else
                right--;
        }

        return area;
    }

    public List<String> letterCombinations(String digits) {
        LinkedList<String> ans = new LinkedList<String>();

        if (digits.isEmpty())
            return ans;
        String[] mapping = new String[] { "0", "1", "abc", "def", "ghi", "jkl", "mno", "pqrs", "tuv", "wxyz" };
        ans.add("");
        for (int i = 0; i < digits.length(); i++) {
            int x = Character.getNumericValue(digits.charAt(i));
            while (ans.peek().length() == i) {
                String t = ans.remove();
                for (char s : mapping[x].toCharArray())
                    ans.add(t + s);
            }
        }
        return ans;
    }

    public List<List<Integer>> threeSum(int[] nums) {
        Set<List<Integer>> ans = new HashSet<>();
        for (int i = 0; i < nums.length - 1; i++) {
            for (int j = 0; j < nums.length - 1 - i; j++) {
                if (nums[j] > nums[j + 1]) {
                    int temp = nums[j];
                    nums[j] = nums[j + 1];
                    nums[j + 1] = temp;
                }
            }

        }
        System.out.println(Arrays.toString(nums));
        for (int i = 0; i < nums.length; i++) {

            int left = i + 1, right = nums.length - 1, fix = i;
            while (left < right) {
                int sum = nums[left] + nums[fix] + nums[right];
                if (sum == 0) {
                    ans.add(List.of(nums[left], nums[fix], nums[right]));
                    left++;
                } else if (sum < 0) {
                    left++;
                } else
                    right--;
            }
        }

        return ans.stream().toList();
    }

    public ListNode removeNthFromEnd(ListNode head, int n) {
        ListNode res = new ListNode(0, head);
        ListNode dummy = res;
        for (int i = 0; i < n; i++) {
            head = head.next;
        }
        while (head != null) {
            head = head.next;
            dummy = dummy.next;
        }
        dummy = dummy.next;
        return res;
    }

    public List<String> generateParenthesis(int n) {
        List<String> ans = new ArrayList<>();
        backtrackString(ans, new StringBuilder(""), 0, 0, n);
        return ans;
    }

    private static void backtrackString(List<String> result, StringBuilder current, int open, int close, int max) {
        System.out.println("list: " + result + " " + current + " " + open + " " + close + " " + max);

        if (current.length() == max * 2) {
            result.add(current.toString());
            return;
        }

        if (open < max) {
            current.append('(');
            backtrackString(result, current, open + 1, close, max);
            current.deleteCharAt(current.length() - 1); // backtrack
        }
        if (close < open) {
            current.append(')');
            backtrackString(result, current, open, close + 1, max);
            current.deleteCharAt(current.length() - 1); // backtrack
        }
    }

    public ListNode mergeKLists(ListNode[] lists) {
        ListNode start = null;
        ListNode head = null;
        while (true) {
            int smallest = Integer.MAX_VALUE;
            int smallestIndex = -1;
            for (int i = 0; i < lists.length; i++) {
                ListNode node = lists[i];
                if (node != null && node.val < smallest) {
                    smallest = node.val;
                    smallestIndex = i;
                }
            }
            if (smallestIndex == -1)
                break;

            if (head == null) {
                start = lists[smallestIndex];
                head = lists[smallestIndex];

            } else {
                head.next = lists[smallestIndex];
                head = head.next;
            }
            lists[smallestIndex] = lists[smallestIndex].next;

        }
        return start;

    }

    public int removeDuplicates(int[] nums) {
        int counts = 0;
        for (int i = 1; i < nums.length; i++) {
            if (nums[i] == 101) {
                if (i < nums.length - counts) {
                    nums[i] = nums[i + counts];
                    nums[i + counts] = 101;
                    i--;
                }
                continue;
            }
            if (!(nums[i] > nums[i - 1])) {
                nums[i] = nums[i + counts];
                nums[i + counts] = 101;
                i--;
                counts++;
            }

            System.out.println(Arrays.toString(nums));
        }
        System.out.println(Arrays.toString(nums));
        return nums.length - counts;

    }

    public int removeElements(int[] nums, int val) {
        int counts = 0;
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] == -1) {
                if (i < nums.length - counts) {
                    nums[i] = nums[i + counts];
                    nums[i + counts] = -1;
                    i--;
                }
                continue;
            }
            if (nums[i] == val) {
                nums[i] = nums[i + counts];
                nums[i + counts] = -1;
                i--;
                counts++;
            }

        }
        return nums.length - counts;

    }

    public int divide(int dividend, int divisor) {
        int coeff = 0;

        int dividendSign = dividend < 0 ? -1 : 1;
        int divisorSign = divisor < 0 ? -1 : 1;
        int absDividend = Math.abs(dividend);
        int absDivisor = Math.abs(divisor);
        int sum = 0;
        int quotient = 0;
        while (sum + absDivisor < absDividend + coeff) {
            sum += absDivisor;
            quotient++;
            // System.out.println(quotient);
        }
        return quotient * dividendSign * divisorSign;
    }

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
        System.out.println(Arrays.toString(nodes));
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

    public static void main(String[] args) {
        LeetcodeQuestions sa = new LeetcodeQuestions();

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
        System.out.println(sa.dfs(root));

        // Integer[] bfsNull = new Integer[] { 1, 2, 3, 4, 5, 6, 7, 8, null, null, 9,
        // 10, null, null, 11 };
        Integer[] bfsNull = new Integer[] { 1, 2, 3, 4, 5, null, 8, null, null, 6, 7, null, null, 9, null };

        // System.out.println(sa.dfs(sa.bftToTree(bfsNull)));
        System.out.println("=".repeat(10));
        System.out.println(sa.inorder(sa.bftToTree(bfsNull)));
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
