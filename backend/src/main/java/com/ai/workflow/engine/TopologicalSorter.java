package com.ai.workflow.engine;

import com.ai.workflow.entity.WorkflowNode;
import com.ai.workflow.entity.WorkflowEdge;
import com.ai.workflow.exception.CyclicDependencyException;

import java.util.*;
import java.util.stream.Collectors;

public class TopologicalSorter {

    /**
     * Kahn's algorithm for topological sorting.
     * Returns nodes sorted in execution order (from input to output).
     */
    public static List<WorkflowNode> sort(List<WorkflowNode> nodes, List<WorkflowEdge> edges) {
        // Build adjacency: nodeKey -> list of downstream nodeKeys
        Map<String, List<String>> adjacency = new HashMap<>();
        Map<String, Integer> inDegree = new HashMap<>();

        // Map node IDs to nodeKeys
        Map<Long, String> idToKey = nodes.stream()
                .collect(Collectors.toMap(WorkflowNode::getId, WorkflowNode::getNodeKey));

        // Initialize
        for (WorkflowNode node : nodes) {
            String key = node.getNodeKey();
            adjacency.put(key, new ArrayList<>());
            inDegree.put(key, 0);
        }

        // Build graph from edges
        for (WorkflowEdge edge : edges) {
            String sourceKey = idToKey.get(edge.getSourceNode().getId());
            String targetKey = idToKey.get(edge.getTargetNode().getId());
            if (sourceKey != null && targetKey != null) {
                adjacency.get(sourceKey).add(targetKey);
                inDegree.put(targetKey, inDegree.get(targetKey) + 1);
            }
        }

        // Kahn's algorithm
        Queue<String> queue = new LinkedList<>();
        for (String key : inDegree.keySet()) {
            if (inDegree.get(key) == 0) {
                queue.add(key);
            }
        }

        List<String> sortedKeys = new ArrayList<>();
        while (!queue.isEmpty()) {
            String current = queue.poll();
            sortedKeys.add(current);
            for (String neighbor : adjacency.get(current)) {
                inDegree.put(neighbor, inDegree.get(neighbor) - 1);
                if (inDegree.get(neighbor) == 0) {
                    queue.add(neighbor);
                }
            }
        }

        // Check for cycle
        if (sortedKeys.size() != nodes.size()) {
            throw new CyclicDependencyException("Workflow contains cyclic dependencies");
        }

        // Map back to nodes
        Map<String, WorkflowNode> keyToNode = nodes.stream()
                .collect(Collectors.toMap(WorkflowNode::getNodeKey, n -> n));

        return sortedKeys.stream()
                .map(keyToNode::get)
                .collect(Collectors.toList());
    }
}
