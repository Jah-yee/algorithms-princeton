package ch4_graphs.undirected_graphs;

// stands for "connected components"
public class CC {
  private boolean[] marked;
  private int[] id;
  private int count;

  public CC(Graph G) {
    marked = new boolean[G.V()];
    id = new int[G.V()]; // represents component IDs for each vertex

    count = 0;
    for (int s = 0; s < G.V(); s++) {
      // process all unmarked vertices connected to s using DFS
      if (!marked[s]) {
        dfs(G, s); // mark and ID all connected components
        count++; // increment count for new component ID
      }
    }
  }

  private void dfs(Graph G, int v) {
    // mark vertex
    marked[v] = true;

    // set vertex component ID to current count of components
    id[v] = count;

    // recursively mark and ID all adjacent vertices
    for (int w : G.adj(v)) {
      if (!marked[w]) {
        dfs(G, w);
      }
    }
  }

  public boolean connected(int v, int w) {
    return id[v] == id[w];
  }

  public int count() {
    return count;
  }

  int id(int v) {
    return id[v];
  }
}
