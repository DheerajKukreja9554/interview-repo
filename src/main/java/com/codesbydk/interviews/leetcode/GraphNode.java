package com.codesbydk.interviews.leetcode;

import java.util.*;
class GraphNode {
    int val;  // node id or value
    List<GraphNode> neighbors;

    GraphNode(int val) {
        this.val = val;
        this.neighbors = new ArrayList<>();
    }
}