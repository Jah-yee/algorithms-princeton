package ch3_searching.hash_tables;

import edu.princeton.cs.algs4.SequentialSearchST;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdRandom;

public class SeparateChainingHashST<Key, Value> {
  private int N, M;
  private SequentialSearchST<Key, Value>[] chains;

  public SeparateChainingHashST() {
    this(997);
  }

  @SuppressWarnings("unchecked")
  public SeparateChainingHashST(int M) {
    this.M = M;
    chains = (SequentialSearchST<Key, Value>[]) new SequentialSearchST[M];
    for (int i = 0; i < M; i++) {
      chains[i] = new SequentialSearchST<>();
    }
  }

  private int hash(Key key) {
    // mask off sign bit to prevent negative remainders
    return (key.hashCode() & 0x7fffffff) % M;
    // Note: Math.abs() does not work for -2^31. Math.abs(-2147483648) == -2147483648
  }

  public int size() {
    return N;
  }

  public boolean isEmpty() {
    return N == 0;
  }

  public Value get(Key key) {
    return (Value) chains[hash(key)].get(key);
  }

  public void put(Key key, Value val) {
    if (val == null)
      delete(key);

    chains[hash(key)].put(key, val);
    N++;
  }

  public void delete(Key key) {
    if (isEmpty())
      return;

    chains[hash(key)].delete(key);
    N--;
  }

  public Iterable<Key> keys() {
    Stack<Key> stack = new Stack<>();

    for (int i = 0; i < N; i++) {
      for (Key key : chains[i].keys()) {
        stack.push(key);
      }
    }

    return stack;
  }

  public static void main(String[] args) {
    SeparateChainingHashST<Integer, String> hashST = new SeparateChainingHashST<>();

    assert hashST.size() == 0;
    assert hashST.isEmpty();

    hashST.put(1, "1");

    assert hashST.size() == 1;
    assert !hashST.isEmpty();
    assert hashST.get(1) == "1";

    hashST.delete(1);

    assert hashST.size() == 0;
    assert hashST.isEmpty();

    int T = 100_000;

    int[] keys = new int[T];
    for (int i = 0; i < T; i++) {
      keys[i] = i;
    }
    StdRandom.shuffle(keys);

    // System.out.println("Testing put()...");
    for (int i = 0; i < T; i++) {
      hashST.put(keys[i], "" + keys[i]);
      assert hashST.size() == i + 1;
    }

    StdRandom.shuffle(keys);

    // System.out.println("Testing delete()...");
    for (int i = T - 1; i >= 0; i--) {
      hashST.delete(keys[i]);
      assert hashST.size() == i : "expected size: " + i + ", actual size: " + hashST.size();
    }

    System.out.println("All tests pass");
  }
}
