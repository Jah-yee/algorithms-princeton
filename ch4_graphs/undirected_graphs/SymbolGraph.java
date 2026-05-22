package ch4_graphs.undirected_graphs;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.ST;

public class SymbolGraph {
  private ST<String, Integer> st;
  private String[] keys;
  private Graph G;

  public SymbolGraph(String filename, String delim) {
    st = new ST<>();
    In in = new In(filename);

    /* Create symbol table */

    while (in.hasNextLine()) {
      // read each line and split by given delimiter
      String[] a = in.readLine().split(delim);

      // process each substring
      for (int i = 0; i < a.length; i++) {
        // if substring isn't in symbol table,
        if (!st.contains(a[i]))
          // add it, using current symbol table size as value/ID
          st.put(a[i], st.size());
      }
    }

    /* Create keys array */

    // initialize keys using final symbol table size
    keys = new String[st.size()];

    // put each name in the index of its ID
    for (String name : st.keys()) {
      keys[st.get(name)] = name;
    }

    /* Create graph of associations */

    // initialize graph using final symbol table size
    G = new Graph(st.size());

    // re-read file to make associations
    in = new In(filename);

    while (in.hasNextLine()) {
      // read each line and split by given delimiter
      String[] a = in.readLine().split(delim);

      // get vertex associated with *first* element
      int v = st.get(a[0]);

      // connect all later elements in the line with first element
      for (int i = 1; i < a.length; i++) {
        G.addEdge(v, st.get(a[i]));
      }
    }
  }

  public boolean contains(String key) {
    return st.contains(key);
  }

  public int index(String key) {
    return st.get(key);
  }

  public String name(int v) {
    return keys[v];
  }

  public Graph G() {
    return G;
  }
}
