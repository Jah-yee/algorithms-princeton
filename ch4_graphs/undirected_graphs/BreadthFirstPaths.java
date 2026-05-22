package ch4_graphs.undirected_graphs;

import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.Stack;

public class BreadthFirstPaths {
  private boolean[] marked;
  private int[] edgeTo;
  private final int s;

  public BreadthFirstPaths(Graph G, int s) {
    marked = new boolean[G.V()];
    edgeTo = new int[G.V()];
    this.s = s;
    bfs(G, s);
  }

  private void bfs(Graph G, int s) {
    // use queue instead of stack (call stack in DFS)
    Queue<Integer> queue = new Queue<>();

    // mark starting location
    marked[s] = true;
    queue.enqueue(s);

    while (!queue.isEmpty()) {
      // get least-recently visited node
      int v = queue.dequeue();

      // process and enqueue all unmarked adjacent nodes
      for (int w : G.adj(v)) {
        if (!marked[w]) {
          edgeTo[w] = v;
          marked[w] = true;
          queue.enqueue(w);
        }
      }
    }
  }

  public boolean hasPathTo(int v) {
    // same as DFS hasPathTo
    return marked[v];
  }

  public Iterable<Integer> pathTo(int v) {
    // same as DFS

    if (!hasPathTo(v))
      return null;

    Stack<Integer> path = new Stack<>();
    for (int x = v; x != s; x = edgeTo[x]) {
      path.push(x);
    }
    path.push(s);

    return path;
  }
}
