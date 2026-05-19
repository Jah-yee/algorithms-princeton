package ch3_searching.hash_tables;

import edu.princeton.cs.algs4.SequentialSearchST;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdRandom;

public class SeparateChainingHashST<Key, Value> {
  private int N, M, lgM;
  private static int defaultSize = 997;
  private double probeTolerance;
  private static double defaultProbeTolerance = 5.0;
  private SequentialSearchST<Key, Value>[] chains;

  private static int[] primes = new int[] {
      0, 0, 0, 0, 0,
      31, 61, 127, 251, 509, 1021, 2039, 4093, 8191, 16381, 32749, 65521, 131071, 262139, 524287, 1048573, 2097143,
      4194301, 8388593, 16777213, 33554393, 67108859, 134217689, 268435399, 536870909, 1073741789, 2147483647
  };

  public SeparateChainingHashST() {
    this(defaultSize, defaultProbeTolerance);
  }

  public SeparateChainingHashST(double probeTolerance) {
    this(defaultSize, probeTolerance);
  }

  public SeparateChainingHashST(int M) {
    this(M, defaultProbeTolerance);
  }

  @SuppressWarnings("unchecked")
  public SeparateChainingHashST(int M, double probeTolerance) {
    this.M = M;
    this.probeTolerance = probeTolerance;
    chains = (SequentialSearchST<Key, Value>[]) new SequentialSearchST[M];
    for (int i = 0; i < M; i++) {
      chains[i] = new SequentialSearchST<>();
    }
    setLgM();
  }

  private int hash(Key key) {
    // mask off sign bit to prevent negative remainders
    int t = key.hashCode() & 0x7fffffff;
    // Note: Math.abs() does not work for -2^31. Math.abs(-2147483648) ==
    // -2147483648

    if (lgM < 26)
      t = t % primes[lgM + 5];

    return t % M;
  }

  private void setLgM() {
    lgM = (int) (Math.log(M) / Math.log(2));
  }

  private void resize(int newSize) {
    SeparateChainingHashST<Key, Value> t = new SeparateChainingHashST<>(newSize, probeTolerance);

    for (int i = 0; i < chains.length; i++) {
      SequentialSearchST<Key, Value> currentChain = chains[i];

      for (Key key : currentChain.keys()) {
        t.put(key, currentChain.get(key));
      }
    }

    chains = t.chains;
    M = t.M;
    setLgM();
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

    if (N >= M / 2)
      resize(2 * M);

    chains[hash(key)].put(key, val);
    N++;
  }

  public void delete(Key key) {
    if (isEmpty())
      return;

    chains[hash(key)].delete(key);
    N--;

    if (N > 0 && N <= M / 8)
      resize(M / 2);
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
