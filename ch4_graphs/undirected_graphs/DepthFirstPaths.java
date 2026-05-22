package ch4_graphs.undirected_graphs;

import edu.princeton.cs.algs4.Stack;

public class DepthFirstPaths {
  private boolean[] marked;
  private int[] edgeTo;
  private final int s;

  public DepthFirstPaths(Graph G, int s) {
    marked = new boolean[G.V()];
    edgeTo = new int[G.V()];
    this.s = s;
    dfs(G, s);
  }

  private void dfs(Graph G, int v) {
    marked[v] = true;

    for (int w : G.adj(v)) {
      if (!marked[w]) {
        // on first visiting any node, store the previous node
        edgeTo[w] = v;
        // when moving from v to w, edgeTo[w] will be set to v

        // then continue DFS
        dfs(G, w);
      }
    }
  }

  public boolean hasPathTo(int v) {
    return marked[v];
  }

  public Iterable<Integer> pathTo(int v) {
    if (!hasPathTo(v))
      return null;

    Stack<Integer> path = new Stack<>();

    // since there must be a path, step back through edgeTo[] from given node
    for (int x = v; x != s; x = edgeTo[x]) {
      path.push(x);
    }

    // include final node in the path
    path.push(s);

    // Note: using a stack means the nodes are in order s -> v
    // (a Queue would yield v -> s)
    return path;
  }
}
