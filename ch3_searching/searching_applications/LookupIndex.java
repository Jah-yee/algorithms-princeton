package ch3_searching.searching_applications;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.ST;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

public class LookupIndex {
  public static void main(String[] args) {
    In dbIn = new In(args[0]);
    String separator = args[1];

    ST<String, Queue<String>> keyValueSt = new ST<>();
    ST<String, Queue<String>> valueKeySt = new ST<>();

    while (dbIn.hasNextLine()) {
      String[] a = dbIn.readLine().split(separator);
      String key = a[0];

      for (int i = 1; i < a.length; i++) {
        String val = a[i];

        if (!keyValueSt.contains(key)) keyValueSt.put(key, new Queue<>());
        if (!valueKeySt.contains(val)) valueKeySt.put(val, new Queue<>());

        keyValueSt.get(key).enqueue(val);
        valueKeySt.get(val).enqueue(key);
      }
    }

    System.out.println("Indexing finished. Enter a search term to see all associated keys/values. Ctrl+C to exit.");

    // input loop
    while (!StdIn.isEmpty()) {
      String query = StdIn.readLine();

      if (keyValueSt.contains(query)) {
        for (String val : keyValueSt.get(query)) {
          StdOut.println("  " + val);
        }
      }

      if (valueKeySt.contains(query)) {
        for (String key : valueKeySt.get(query)) {
          StdOut.println("  " + key);
        }
      }
    }
  }
}
