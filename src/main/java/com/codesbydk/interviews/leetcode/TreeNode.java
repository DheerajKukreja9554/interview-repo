package com.codesbydk.interviews.leetcode;

import lombok.Data;

@Data
public class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;

    TreeNode() {
    }

    TreeNode(int val) {
        this.val = val;
    }

    TreeNode(int val, TreeNode left, TreeNode right) {
        this.val = val;
        this.left = left;
        this.right = right;
    }

    @Override
    public String toString() {
        return "%s".formatted(val);
        // return "TreeNode [val=" + val + ", left=" + left + ", right=" + right + "]";
    }

}