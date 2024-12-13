package org.example;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedMultigraph;

import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.parse.Parser;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.GraphvizException;

import java.nio.file.Paths;
import java.io.*;
import java.util.*;

public class GraphDataStructure {
    public Graph<String, DefaultEdge> graph = new DirectedMultigraph<>(DefaultEdge.class);

    // Constructor
    public GraphDataStructure() {
        this.graph = new DirectedMultigraph<>(DefaultEdge.class);
    }

    // function to parse DOT file, Feature 1
    public void parseGraph(String filepath) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("digraph")) {
                    continue; // Skip the digraph declaration
                } else if (line.contains("->")) {
                    // Parse edges
                    String[] parts = line.split("->");
                    String src = parts[0].trim().replaceAll("[;]", "");
                    String dst = parts[1].trim().replaceAll("[;]", "");

                    // Add nodes and edges to the graph
                    addNode(src);
                    addNode(dst);
                    graph.addEdge(src, dst);
                } else if (line.contains(";")) {
                    // Parse nodes (isolated nodes or attributes)
                    String node = line.replaceAll("[;]", "").trim();
                    if (!node.isEmpty()) {
                        addNode(node);
                    }
                }
            }
        }
    }

    // Function to add new node, Feature 2
    public void addNode(String label) {
        if (!graph.containsVertex(label)) {
            graph.addVertex(label);
        }
    }

    // Function to add a list of nodes, Feature 2
    public void addNodes(String[] labels) {
        for (String label : labels) {
            addNode(label);
        }
    }

    // Function to add an edge, Feature 3
    public void addEdge(String srcLabel, String dstLabel) {
        if (!graph.containsEdge(srcLabel, dstLabel)) {
            graph.addEdge(srcLabel, dstLabel);
        }
    }

    // Feature 4: Export to DOT format
    public void outputDOTGraph(String path) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            writer.write("digraph G {\n");
            for (String vertex : graph.vertexSet()) {
                writer.write(vertex + ";\n");
            }
            for (DefaultEdge edge : graph.edgeSet()) {
                writer.write(graph.getEdgeSource(edge) + " -> " + graph.getEdgeTarget(edge) + ";\n");
            }
            writer.write("}");
        }
    }

    // Feature 4: Export to PNG graphic using Graphviz
    public void outputGraphics(String path) throws IOException {
        try {
            MutableGraph g = new Parser().read(Paths.get("outputDOTGraph.dot").toFile());
            Graphviz.fromGraph(g).render(Format.PNG).toFile(new File(path));
        } catch (GraphvizException e) {
            System.err.println("Error: Failed to generate PNG using Graphviz.");
            e.printStackTrace();
        }
    }

    // Function to output graph information, Feature 1
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Number of nodes: ").append(graph.vertexSet().size()).append("\n");
        sb.append("Nodes: ").append(graph.vertexSet()).append("\n");
        sb.append("Number of edges: ").append(graph.edgeSet().size()).append("\n");
        sb.append("Edges:\n");
        for (DefaultEdge edge : graph.edgeSet()) {
            sb.append(graph.getEdgeSource(edge)).append(" -> ").append(graph.getEdgeTarget(edge)).append("\n");
        }
        return sb.toString();
    }

    // Function to output graph details to a text file, Feature 1
    public void outputGraph(String filePath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(this.toString());
        }
    }

    // Function to remove a node by its label
    public void removeNode(String label) {
        if (graph.containsVertex(label)) {
            graph.removeVertex(label);
            System.out.println("Node " + label + " removed successfully.");
        } else {
            System.out.println("Node " + label + " does not exist in the graph.");
        }
    }

    // Function to remove a list of nodes
    public void removeNodes(String[] labels) {
        for (String label : labels) {
            removeNode(label);
        }
    }

    // Function to remove and edge from the graph
    public void removeEdge(String srcLabel, String dstLabel) {
        if (graph.containsEdge(srcLabel, dstLabel)) {
            graph.removeEdge(srcLabel, dstLabel);
        }
        else{
            System.out.println("Edge " + srcLabel + " -> " + dstLabel + " does not exist in the graph.");
        }
    }

    // Path class used in dfs and bfs
    class Path {
        private List<String> nodes;

        public Path() {
            this.nodes = new ArrayList<>();
        }

        public void addNode(String node) {
            nodes.add(node);
        }

        public List<String> getNodes() {
            return nodes;
        }

        @Override
        public String toString() {
            return String.join(" -> ", nodes);
        }
    }

    // enum used in GraphSearch
    public enum Algorithm {
        BFS,
        DFS
    }

    // Function to search for a path using bfs or dfs
    public Path GraphSearch(String src, String dst, Algorithm algo) {
        if(algo == Algorithm.BFS) {
            return bfsGraphSearch(src, dst);
        }
        else if(algo == Algorithm.DFS) {
            return dfsGraphSearch(src, dst);
        }

        return null;
    }

    // Function to search for a path using dfs
    public Path dfsGraphSearch(String src, String dst) {
        if (!graph.containsVertex(src) || !graph.containsVertex(dst)) {
            return null;
        }

        Set<String> visited = new HashSet<>();
        Path path = new Path();
        if (dfsHelper(src, dst, visited, path)) {
            return path;
        }
        return null;
    }

    // helper method for dfs
    private boolean dfsHelper(String current, String destination, Set<String> visited, Path path) {
        visited.add(current);
        path.addNode(current);

        if (current.equals(destination)) {
            return true;
        }

        for (DefaultEdge edge : graph.outgoingEdgesOf(current)) {
            String neighbor = graph.getEdgeTarget(edge);
            if (!visited.contains(neighbor)) {
                if (dfsHelper(neighbor, destination, visited, path)) {
                    return true;
                }
            }
        }

        path.nodes.remove(path.nodes.size() - 1); // Backtrack
        return false;
    }

    // Function to search for a path using bfs
    public Path bfsGraphSearch(String src, String dst) {
        Queue<String> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();
        Map<String, String> predecessors = new HashMap<>();

        queue.add(src);
        visited.add(src);

        while (!queue.isEmpty()) {
            String current = queue.poll();

            if (current.equals(dst)) {
                Path path = new Path();
                String node = dst;
                while (node != null) {
                    path.addNode(node);
                    node = predecessors.get(node);
                }
                Collections.reverse(path.getNodes());
                return path;
            }

            for (DefaultEdge neighbor : graph.edgesOf(current)) {
                String neighborNode = graph.getEdgeTarget(neighbor);
                if (!visited.contains(neighborNode)) {
                    visited.add(neighborNode);
                    queue.add(neighborNode);
                    predecessors.put(neighborNode, current);
                }
            }
        }

        return null; // Return null if no path is found
    }

    // Main method to test functions
    public static void main(String[] args) {
        try {
            // Create a graph instance
            GraphDataStructure graph = new GraphDataStructure();

            // Test adding nodes
            System.out.println("Testing addNode:");
            graph.addNode("A");
            graph.addNode("B");
            graph.addNode("C");
            System.out.println(graph);

            // Test adding edges
            System.out.println("Testing addEdge:");
            graph.addEdge("A", "B");
            graph.addEdge("B", "C");
            graph.addEdge("A", "C");
            System.out.println(graph);

            // Test adding multiple nodes
            System.out.println("Testing addNodes:");
            graph.addNodes(new String[]{"D", "E", "F"});
            System.out.println(graph);

            // Test removing a node
            System.out.println("Testing removeNode:");
            graph.removeNode("E");
            System.out.println(graph);

            // Test removing multiple nodes
            System.out.println("Testing removeNodes:");
            graph.removeNodes(new String[]{"A", "D"});
            System.out.println(graph);

            // Test removing an edge
            System.out.println("Testing removeEdge:");
            graph.removeEdge("B", "C");
            System.out.println(graph);

            // Filling graph for furhter testing
            graph.addNodes(new String[]{"A", "D", "E"});
            graph.addEdge("A", "B");
            graph.addEdge("A", "C");
            graph.addEdge("B", "D");
            graph.addEdge("C", "E");
            graph.addEdge("D", "E");
            System.out.println("Current graph structure:");
            System.out.println(graph);

            // Test exporting to DOT format
            System.out.println("Testing outputDOTGraph:");
            graph.outputDOTGraph("outputDOTGraph.dot");
            System.out.println("Graph exported to outputDOTGraph.dot");

            // Test exporting to PNG (requires Graphviz)
            System.out.println("Testing outputGraphics:");
            graph.outputGraphics("testGraph.png");
            System.out.println("Graph exported to testGraph.png");

            // Test BFS path search
            System.out.println("Testing BFS Graph Search from A to E:");
            GraphDataStructure.Path bfsPath = graph.GraphSearch("A", "E", GraphDataStructure.Algorithm.BFS);
            System.out.println("BFS Path: " + (bfsPath != null ? bfsPath : "No path found"));

            // Test DFS path search
            System.out.println("Testing DFS Graph Search from A to E:");
            GraphDataStructure.Path dfsPath = graph.GraphSearch("A", "E", GraphDataStructure.Algorithm.DFS);
            System.out.println("DFS Path: " + (dfsPath != null ? dfsPath : "No path found"));

            // Test outputting graph details to a text file
            System.out.println("Testing outputGraph:");
            graph.outputGraph("graphDetails.txt");
            System.out.println("Graph details exported to graphDetails.txt");

        } catch (IOException e) {
            System.err.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }

    }
}
