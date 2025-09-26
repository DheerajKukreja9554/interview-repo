package com.codesbydk.interviews.leetcode;

import java.util.ArrayList;
import java.util.List;

public class WeightedGraphNode {
    int val;
    List<Edge> neighbors; // store edges instead of just nodes

    WeightedGraphNode(int val) {
        this.val = val;
        neighbors = new ArrayList<>();
    }

    @Override
    public String toString() {
        return val + "";//+ ", neighbors=" + neighbors + ")";
    }

}

class Edge {
    WeightedGraphNode to;
    int weight;

    Edge(WeightedGraphNode to, int weight) {
        this.to = to;
        this.weight = weight;
    }

    @Override
    public String toString() {
        return "(to=" + to + ", weight=" + weight + ")";
    }

    
}
