package ch3_searching.hash_tables;

import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdRandom;

public class LinearProbingHashST<Key, Value> {
  private int N;
  private int M;
  private Key[] keys;
  private Value[] vals;

  public LinearProbingHashST() {
    this(16);
  }

  @SuppressWarnings("unchecked")
  public LinearProbingHashST(int capacity) {
    M = capacity;
    keys = (Key[]) new Object[M];
    vals = (Value[]) new Object[M];
  }

  public int size() {
    return N;
  }

  public boolean isEmpty() {
    return N == 0;
  }

  private int hash(Key key) {
    return (key.hashCode() & 0x7fffffff) % M;
  }

  private void resize(int newSize) {
    LinearProbingHashST<Key, Value> t = new LinearProbingHashST<>(newSize);

    for (int i = 0; i < M; i++) {
      if (keys[i] != null)
        t.put(keys[i], vals[i]);
    }

    keys = t.keys;
    vals = t.vals;
    M = t.M;
  }

  public void put(Key key, Value val) {
    if (val == null)
      delete(key);

    if (N >= M / 2)
      resize(2 * M);

    // probe keys until a search hit or miss
    int i;
    for (i = hash(key); keys[i] != null; i = (i + 1) % M) {
      // if search hit, update key with new value
      if (keys[i].equals(key)) {
        vals[i] = val;
        return;
      }
    }

    // search miss -- add new key/value pair
    keys[i] = key;
    vals[i] = val;
    N++;
  }

  public Value get(Key key) {
    // probe keys until a search hit or miss
    for (int i = hash(key); keys[i] != null; i = (i + 1) % M) {
      // if search hit, return corresponding value
      if (keys[i].equals(key))
        return vals[i];
    }

    // search miss -- return null
    return null;
  }

  public boolean contains(Key key) {
    for (int i = hash(key); keys[i] != null; i = (i + 1) % M) {
      if (keys[i].equals(key))
        return true;
    }

    return false;
  }

  public void delete(Key key) {
    if (!contains(key))
      return;

    // find and delete key and value
    int i = hash(key);
    while (!key.equals(keys[i])) {
      i = (i + 1) % M;
    }

    keys[i] = null;
    vals[i] = null;
    N--;

    // begin reinsertion on next entry
    i = (i + 1) % M;

    // delete + reinsert key/value pairs to maintain linear probing structure
    while (keys[i] != null) {
      // store temp key/value pair
      Key keyToRedo = keys[i];
      Value valToRedo = vals[i];

      // remove key/value pair
      keys[i] = null;
      vals[i] = null;
      N--;

      // reinsert key/value pair in new correct place (N gets re-incremented)
      put(keyToRedo, valToRedo);

      // move to next entry
      i = (i + 1) % M;
    }

    if (N > 0 && N <= M / 8)
      resize(M / 2);
  }

  public Iterable<Key> keys() {
    Stack<Key> stack = new Stack<>();

    for (int i = 0; i < M; i++) {
      if (keys[i] != null)
        stack.push(keys[i]);
    }

    return stack;
  }

  public static void main(String[] args) {
    LinearProbingHashST<Integer, String> hashST = new LinearProbingHashST<>();

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
