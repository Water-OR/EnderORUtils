package io.github.enderor.utils;

public class Pair<K, V> implements Cloneable {
  public K getKey() { return key; }
  
  public Pair<K, V> setKey(K key) {
    this.key = key;
    return this;
  }
  
  public V getValue() { return value; }
  
  public Pair<K, V> setValue(V value) {
    this.value = value;
    return this;
  }
  
  private K key;
  private V value;
  
  public Pair()               { this(null, null); }
  
  public Pair(K key, V value) { setKey(key).setValue(value); }
  
  @Override
  public Pair<K, V> clone() throws CloneNotSupportedException {
    Pair<K, V> pair = (Pair<K, V>) super.clone();
    if (!(key instanceof Cloneable)) { pair.setKey(key); }
    if (!(value instanceof Cloneable)) { pair.setValue(value); }
    return pair;
  }
}
