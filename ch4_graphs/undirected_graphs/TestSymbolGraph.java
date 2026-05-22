package ch4_graphs.undirected_graphs;

import edu.princeton.cs.algs4.StdIn;

public class TestSymbolGraph {
  public static void main(String[] args) {
    String filename = args[0];
    String delim = args[1];
    SymbolGraph sg = new SymbolGraph(filename, delim);

    Graph G = sg.G();

    System.out.println("Symbol graph constructed. Enter a symbol to find its adjacent vertices. Ctrl+C to exit.");

    while (StdIn.hasNextLine()) {
      String source = StdIn.readLine();

      if (sg.contains(source)) {
        for (int w : G.adj(sg.index(source))) {
          System.out.println("\t" + sg.name(w));
        }
      } else {
        System.out.println("Not in database.");
      }
    }
  }
}
