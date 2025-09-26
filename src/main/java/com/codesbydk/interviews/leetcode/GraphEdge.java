package com.codesbydk.interviews.leetcode;

public class GraphEdge {
    WeightedGraphNode from;
    WeightedGraphNode to;
    int weight;

    GraphEdge(WeightedGraphNode from, WeightedGraphNode to, int weight) {
        this.from = from;
        this.to = to;
        this.weight = weight;
    }

    @Override
    public String toString() {
        return "from: %s;to : %s; weight: %s".formatted(from, to, weight);
    }
}
