# graph-data-structure

This project implements a graph data structure using JGraphT and Graphviz for visualization. It includes functionalities to create, modify, and query graphs as well as export them to DOT format and PNG images. The program can also search through the graph using breadth first search or depth first search.

### **[Download Project](https://github.com/Josue-Caballero-Sanchez/graph-data-structure/archive/refs/heads/master.zip)**

![ouput1](java-output.png)
![ouput2](JUnit-tests.png)

## Built with

- Java
- JUnit

## Features
1. Graph Representation: Uses DirectedMultigraph from JGraphT to represent directed graphs.
2. Graph Parsing: Parses a DOT file to create a graph structure.
3. Node and Edge Management: Supports adding, removing, and listing nodes and edges.
4. Graph Export: Exports the graph to DOT format and PNG images.
5. Graph Search: Provides path search using BFS and DFS algorithms.

## Running JUnit Tests
To run the JUnit tests, use the following Maven command: mvn test