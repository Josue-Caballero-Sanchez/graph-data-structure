import org.example.GraphDataStructure;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.*;

public class GraphTest {
    // Test for Feature 1: Parsing DOT file to create a graph
    @Test
    public void testParseGraph() throws IOException {
        GraphDataStructure graphParser = new GraphDataStructure();

        graphParser.parseGraph("testGraph.dot");

        String expectedOutput =
                "Number of nodes: 3\n" +
                        "Nodes: [A, B, C]\n" +
                        "Number of edges: 2\n" +
                        "Edges:\n" +
                        "A -> B\n" +
                        "B -> C";

        assertEquals(expectedOutput.trim(), graphParser.toString().trim());
    }

    // Test for Feature 2: Adding a node and preventing duplicates
    @Test
    public void testAddNode() {
        GraphDataStructure graphParser = new GraphDataStructure();

        graphParser.addNodes(new String[]{"A", "B", "C"});
        graphParser.addNode("A");

        assertEquals(3, graphParser.graph.vertexSet().size());
        assertTrue(graphParser.graph.containsVertex("A"));
    }

    // Test for Feature 3: Adding an edge and preventing duplicates
    @Test
    public void testAddEdge() {
        GraphDataStructure graphParser = new GraphDataStructure();

        graphParser.addNodes(new String[]{"A", "B"});
        graphParser.addEdge("A", "B");
        graphParser.addEdge("A", "B"); // Duplicate edge

        assertEquals(1, graphParser.graph.edgeSet().size());
        assertTrue(graphParser.graph.containsEdge("A", "B"));
    }

    // Test for Feature 4: Exporting to DOT file
    @Test
    public void testOutputDOTGraph() throws IOException {
        GraphDataStructure graphParser = new GraphDataStructure();

        graphParser.addNodes(new String[]{"A", "B"});
        graphParser.addEdge("A", "B");

        graphParser.outputDOTGraph("testOutput.dot");
        String expectedDOT =
                "digraph G {\n" +
                        "A;\n" +
                        "B;\n" +
                        "A -> B;\n" +
                        "}";

        String actualDOT = Files.readString(Paths.get("testOutput.dot"));
        assertEquals(expectedDOT.trim(), actualDOT.trim());
    }

    // Test for Feature 4: Exporting to PNG (graph visualization)
    @Test
    public void testOutputGraphics() {
        GraphDataStructure graphParser = new GraphDataStructure();

        assertDoesNotThrow(() -> {
            graphParser.outputGraphics("testGraph.png");

            assertTrue(Files.exists(Paths.get("testGraph.png")));
        });
    }

    // Test for scenario 1, some edges and nodes are correctly removed
    @Test
    public void  testRemoveNodesAndEdges(){
        GraphDataStructure graphParser = new GraphDataStructure();
        graphParser.addNodes(new String[]{"A", "B", "C", "D"});
        graphParser.addEdge("A", "B");
        graphParser.addEdge("B", "C");

        graphParser.removeNodes(new String[]{"A", "C"});
        graphParser.removeEdge("B", "C");

        assertFalse(graphParser.graph.containsVertex("A"));
        assertFalse(graphParser.graph.containsVertex("C"));
        assertFalse(graphParser.graph.containsEdge("B", "C"));

        assertEquals(2, graphParser.graph.vertexSet().size());
        assertEquals(0, graphParser.graph.edgeSet().size());
    }

    // Test for scenario 2, removing nodes that do not exist prints an error message
    public void testRemoveNonExistentNodes(){
        GraphDataStructure graphParser = new GraphDataStructure();
        graphParser.addNodes(new String[]{"A", "B", "C", "D"});
        graphParser.addEdge("A", "B");
        graphParser.addEdge("B", "C");
        final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        final PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        graphParser.removeNodes(new String[]{"X", "Y"});
        String expectedOutput = "Node X does not exist in the graph.\nNode Y does not exist in the graph.\n";

        assertEquals(expectedOutput, outContent.toString());
    }

    @Test
    public void testRemoveNonExistentEdges(){
        GraphDataStructure graphParser = new GraphDataStructure();
        graphParser.addNodes(new String[]{"A", "B", "C", "D"});
        graphParser.addEdge("A", "B");
        graphParser.addEdge("B", "C");
        final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        final PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        graphParser.removeEdge("A", "D");
        String expectedOutput = "Edge A -> D does not exist in the graph.\n";

        assertEquals(expectedOutput, outContent.toString());
    }
}
