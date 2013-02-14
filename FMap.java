/*
 *   Jeff Wallace
 *   wallace.je@husky.neu.edu
 *   Assignment 9
 */

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Vector;
import java.util.NoSuchElementException;

// A List of Key/Value Pairs with 
// Keys of type K and Values of type V
public abstract class FMap<K,V> implements Iterable<K> {

  // Add the given key and value pair to this FMap
  public abstract FMap<K,V> add(K key, V val);
   
  // Is this FMap empty?
  public abstract boolean isEmpty();
   
  // Return the size of this FMap
  public abstract int size();

  // Does this FMap contain the given key?
  public abstract boolean containsKey(K toFind);

  // Return the value associated with the given key
  public abstract V get(K toFind);

  // Accept a Visitor<K,V> to this FMap, creating a new FMap
  // with values computed by the Visitor<K,V> for each K,V pair
  public abstract FMap<K,V> accept(Visitor<K,V> v);

  // Accept a Visitor to this FMap, updataing the given FMap
  // with values computed by the Visitor for each K,V pair
  abstract FMap<K,V> accept(Visitor<K,V> v, FMap<K,V> newMap);

  // Is this FMap the same as that FMap?
  abstract boolean sameAs(FMap<K,V> that);

  // Return an iterator for this FMap
  public abstract Iterator<K> iterator();

  // Return an iterator for this FMap 
  public abstract Iterator<K> iterator(Comparator<? super K> c);

  // Populate an Vector with the keys of this FMap
  abstract void popKeyList(Vector<K> keys);

  // Create a new EmptyMap
  @SuppressWarnings(value="unchecked")
  public static <K,V> FMap<K,V> emptyMap() {
    return AssocList.emptyMap();
  }

  // Create a new EmptyMap
  @SuppressWarnings(value="unchecked")
  public static <K,V> FMap<K,V> emptyMap(Comparator<? super K> c) {
    return RedBlack.Leaf(c);
  }
}

////////////////////////////////////////////////////////////////
/// Traditional Implementation
////////////////////////////////////////////////////////////////

// An Association List implementation of FMap, for use with 
// FMaps without a specified comparator.
abstract class AssocList<K,V> extends FMap<K,V> {
  // Create a new Empty FMap with no comparator
  @SuppressWarnings(value="unchecked")
  public static <K,V> FMap<K,V> emptyMap() {
    return new EmptyMap<K,V>();
  }
}

// An Empty FMap with Keys of type K and Values of Type V
class EmptyMap<K,V> extends AssocList<K,V> {
  EmptyMap() { }

  public FMap<K,V> add(K key, V val) {
    return new Add<K,V>(key, val, this);
  }

  public boolean isEmpty() {
    return true;
  }

  public int size() {
    return 0;
  }

  public boolean containsKey(K toFind) {
    return false;
  }

  public V get(K toFind) {
    String msg = "Invalid key in get method: " + toFind.toString();
    throw new RuntimeException(msg);
  }

  public String toString() {
    String str = "{...(" + this.size() + " entries)...}";
    return str;
  }

  // Does this EmptyMap equal the given object?
  public boolean equals(Object o) {
    if( o instanceof FMap ) {
      @SuppressWarnings(value="unchecked")
      FMap<K,V> m = (FMap<K,V>) o;
      return m.sameAs(this);
    } else {
      return false;
    }
  }

  boolean sameAs(FMap<K,V> that) {
    return that.isEmpty();
  }

  public int hashCode() {
    return 0;
  }

  public Iterator<K> iterator() {
    return new FMapIterator<K>(new Vector<K>());
  }

  public Iterator<K> iterator(Comparator<? super K> c) {
    return this.iterator();
  }

  void popKeyList(Vector<K> keys) {
    // Do nothing, no keys
  }

  public FMap<K,V> accept(Visitor<K,V> v) {
    return FMap.emptyMap();
  }

  FMap<K,V> accept(Visitor<K,V> v, FMap<K,V> newMap) {
    return newMap;
  }
}

// A Non-Empty FMap with Keys of type K and Values of type V
class Add<K,V> extends AssocList<K,V> {
  K k0;           // The Key for v0
  V v0;           // The Value that corresponds to k0
  FMap<K,V> m0;   // The rest of this FMap
  private int size;

  Add(K key, V val, FMap<K,V> map) {
    this.k0 = key;
    this.v0 = val;
    this.m0 = map;
    if( map.containsKey(key) )  {
      this.size = map.size();
    } else {
      this.size = map.size() + 1;
    }
  }

  public FMap<K,V> add(K key, V val) {
    if( this.containsKey(key) ) {
      return this.setVal(key, val);
    } else {
      return new Add<K,V>(key, val, this);
    }
  }

  public boolean isEmpty() {
    return false;
  }

  public int size() {
    return this.size;
  }

  public boolean containsKey(K toFind) {
    if( k0.equals(toFind) ) {
      return true;
    } else {
      return m0.containsKey(toFind);
    }
  }

  public V get(K toFind) {
    if( k0.equals(toFind) ) {
      return v0;
    } else {
      return m0.get(toFind);
    }
  }

  // Return a string representation of this Add
  public String toString() {
    String str = "{...(" + this.size() + " entries)...}";
    return str;
  }

  public boolean equals(Object o) {
    if( o instanceof FMap ) {
      @SuppressWarnings(value="unchecked")
      FMap<K,V> m = (FMap<K,V>) o;
      return m.sameAs(this);
    } else {
      return false;
    }
  }

  boolean sameAs(FMap<K,V> that) {
    for(K k : this) {
      if( ! that.containsKey(k) ) {
        return false;
      } else if( ! that.get(k).equals(this.get(k)) ) {
        return false;
      }
    }
    return this.size() == that.size();
  }

  public int hashCode() {
    int hash = 71;
    hash = hash * 31 + k0.hashCode();
    hash = hash * 31 + v0.hashCode();
    return hash + m0.hashCode();
  }

  public Iterator<K> iterator() {
    Vector<K> keys = new Vector<K>();
    popKeyList(keys);
    return new FMapIterator<K>(keys);
  }

  public Iterator<K> iterator(Comparator<? super K> c) {
    Vector<K> keys = new Vector<K>();
    popKeyList(keys);
    return new FMapIterator<K>(keys, c);
  }

  void popKeyList(Vector<K> keys) {
    keys.add(this.k0);
    this.m0.popKeyList(keys);
  }

  // Change the value of the given Key in this FMap to the
  // given new Value
  private FMap<K,V> setVal(K key, V newVal) {
    FMap<K,V> newMap = FMap.emptyMap();
    FMap<K,V> oldMap = this;
    while( ! oldMap.isEmpty() ) {
      @SuppressWarnings(value="unchecked")
      Add<K,V> map = (Add<K,V>) oldMap;
      if( map.k0.equals(key) ) {
        newMap = new Add<K,V>(map.k0, newVal, newMap);
        break;
      } else {
        newMap = new Add<K,V>(map.k0, map.v0, newMap);
        oldMap = map.m0;
      }
    }
    return newMap;
  }

  public FMap<K,V> accept(Visitor<K,V> v) {
    FMap<K,V> newMap = FMap.emptyMap();
    V newVal = v.visit(k0, v0);
    newMap = newMap.add(k0, newVal);
    return m0.accept(v, newMap);
  }

  FMap<K,V> accept(Visitor<K,V> v, FMap<K,V> newMap) {
    V newVal = v.visit(k0,v0);
    newMap = newMap.add(k0, newVal);
    return m0.accept(v, newMap);
  }
}

////////////////////////////////////////////////////////////////
///  Red-Black Tree Optimized Implementation
////////////////////////////////////////////////////////////////

// A Red-Black Tree implementation of the FMap ADT, for use when
// a comparator is given to the constructror of an FMap. 
// INVARIANT: No red node has a red parent.
// INVARIANT: Every path from root to a leaf (empty node) contains
// the same number of black nodes.
abstract class RedBlack<K,V> extends FMap<K,V> {
  // A comparator over the keys in this FMap, used to insert
  // key/val pairs in the correct sorted order
  Comparator<? super K> c;

  // The token to represent the Nodes color
  Color color;

  // Create a new Leaf with the given comparator
  @SuppressWarnings(value="unchecked")
  public static <K,V> FMap<K,V> Leaf(Comparator<? super K> c) {
    return new Leaf<K,V>(c);
  }

  // Insert the given key value pair into the RedBlack Tree and
  // ensure the tree is balanced. 
  abstract RedBlack<K,V> insert(K newKey, V newVal);

  // Change the color of the root node of the RedBlack Tree to be 
  // black
  abstract RedBlack<K,V> makeBlack();

  // Return the left child of the RedBlack Tree
  abstract RedBlack<K,V> getLeft();

  // Return the right child of the RedBlack Tree
  abstract RedBlack<K,V> getRight();

  // Is the RedBlack Tree a Red Node?
  abstract boolean isRed();

  // Representation of the Token to distinguish Red and Black nodes
  enum Color {
    RED,
    BLACK;
  }
}

// An Empty BST of Keys of Type K and Values of Type V
class Leaf<K,V> extends RedBlack<K,V> {
  Leaf(Comparator <? super K> c) {
    this.c = c;
    this.color = Color.BLACK;
  }

  public FMap<K,V> add(K key, V val) {
    return insert(key, val).makeBlack();
  }

  public boolean isEmpty() {
    return true;
  }

  public int size() {
    return 0;
  }

  public boolean containsKey(K toFind) {
    return false;
  }

  public V get(K toFind) {
    String msg = "Invalid key in get method: " + toFind.toString();
    throw new RuntimeException(msg);
  }

  public Iterator<K> iterator() {
    return new FMapIterator<K>(new Vector<K>());
  }

  public Iterator<K> iterator(Comparator<? super K> c) {
    return this.iterator();
  }

  void popKeyList(Vector<K> keys) {
    // Do Nothing
  }

  public String toString() {
    String str = "{...(" + this.size() + " entries)...}";
    return str;
  }

  public boolean equals(Object o) {
    if( o instanceof FMap ) {
      @SuppressWarnings(value="unchecked")
       FMap<K,V> m = (FMap<K,V>) o;
      return m.sameAs(this);
    } else {
      return false;
    }
  }

  boolean sameAs(FMap<K,V> that) {
    return that.isEmpty();
  }

  public int hashCode() {
    return 0;
  }

  public FMap<K,V> accept(Visitor<K,V> v) {
    return FMap.emptyMap(c);
  }

  FMap<K,V> accept(Visitor<K,V> v, FMap<K,V> newMap) {
    return newMap;
  }

  RedBlack<K,V> insert(K newKey, V newVal) {
    return new Node<K,V>(this, newKey, newVal, this, c, Color.RED);
  }

  RedBlack<K,V> makeBlack() {
    return new Leaf<K,V>(c);
  }

  RedBlack<K,V> getLeft() {
    throw new RuntimeException("Get Left Error");
  }

  RedBlack<K,V> getRight() {
    throw new RuntimeException("Get Right error.");
  }

  boolean isRed() {
    return false;
  }
}

// A RedBlack with Keys of Type K and Values of Type V
class Node<K,V> extends RedBlack<K,V> {
  // The left subtree of this RedBlack of Key/Val pairs
  RedBlack<K,V> left;   
  // The key for v0
  K k0;
  // The value that corresponds to v0
  V v0;  
  // The right subtree of this RedBlack of Key/Val pairs
  RedBlack<K,V> right; 
  // The size of this FMap 
  private int size; 

  Node(RedBlack<K,V> left, 
       K k0, 
       V v0, 
       RedBlack<K,V> right, 
       Comparator<? super K> c, 
       Color color) {
    this.left = left;
    this.k0 = k0;
    this.v0 = v0;
    this.right = right;
    this.c = c;
    this.color = color;
    this.size = 1 + left.size() + right.size();
  }

  public FMap<K,V> add(K newKey, V newVal) {
    return insert(newKey, newVal).makeBlack();
  }

  public boolean isEmpty() {
    return false;
  }

  public int size() {
    return this.size;
  }

  public boolean containsKey(K toFind) {
    int compare = c.compare(toFind, k0);
    if( compare < 0 ) {
      return left.containsKey(toFind);
    } else if( compare == 0 ) {
      return true;
    } else {
      return right.containsKey(toFind);
    }
  }

  public V get(K toFind) {
    int compare = c.compare(toFind, k0);
    if( compare == 0 ) {
      return v0;
    } else if ( compare < 0 ) {
      return left.get(toFind);
    } else {
      return right.get(toFind);
    }
  }

  public Iterator<K> iterator() {
    Vector<K> keys = new Vector<K>();
    popKeyList(keys);
    return new FMapIterator<K>(keys);
  }

  public Iterator<K> iterator(Comparator<? super K> c) {
    Vector<K> keys = new Vector<K>();
    popKeyList(keys);
    return new FMapIterator<K>(keys, c);
  }

  void popKeyList(Vector<K> keys) {
    getLeft().popKeyList(keys);
    keys.add(k0);
    getRight().popKeyList(keys);
  }

  public String toString() {
    String str = "{...(" + this.size() + " entries)...}";
    return str;
  }

  public boolean equals(Object o) {
    if( o instanceof FMap ) {
      @SuppressWarnings(value="unchecked")
       FMap<K,V> m = (FMap<K,V>) o;
      return m.sameAs(this);
    } else {
      return false;
    }
  }

  boolean sameAs(FMap<K,V> that) {
    for(K k : this) {
      if( ! that.containsKey(k) ) {
        return false;
      } else if( ! that.get(k).equals(this.get(k)) ) {
        return false;
      }
    }
    return this.size() == that.size();
  }

  public int hashCode() {
    int hash = 71;
    hash = hash * 31 + k0.hashCode();
    hash = hash * 31 + v0.hashCode();
    return left.hashCode() + hash + right.hashCode();
  }

  public FMap<K,V> accept(Visitor<K,V> v) {
    FMap<K,V> newMap = FMap.emptyMap(c);
    V newVal = v.visit(k0, v0);
    newMap = left.accept(v, newMap);
    newMap = newMap.add(k0, newVal);
    newMap = right.accept(v, newMap);
    return newMap;
  }

  FMap<K,V> accept(Visitor<K,V> v, FMap<K,V> newMap) {
    V newVal = v.visit(k0, v0);
    newMap = newMap.add(k0, newVal);
    newMap = left.accept(v, newMap);
    newMap = right.accept(v, newMap);
    return newMap;
  }

  RedBlack<K,V> insert(K newKey, V newVal) {
    int compare = c.compare(newKey, k0);
    Node<K,V> t;
    if( compare < 0 ) {
      t = new Node<K,V>(left.insert(newKey, newVal), 
                        k0, v0, 
                        right, 
                        c, 
                        color);
      return t.balance();
    } else if( compare == 0 ) {
      return new Node<K,V>(left, k0, newVal, right, c, color);
    } else {
      t = new Node<K,V>(left, 
                        k0, v0, 
                        right.insert(newKey, newVal), 
                        c, 
                        color);
      return t.balance();
    }
  }

  ///////////////////////////////////////////////////
  ///    Insert Auxilliary Methods
  ///////////////////////////////////////////////////

  // Rebalance the RedBlack Tree. Checks if the current node is in 
  // a valid balancing location. If so, rotates nodes to balance
  // the tree. Returns the rotated tree
  private RedBlack<K,V> balance() {
    switch ( checkBalanceCondition() ) {
      case 1:
        return rotateRight();
      case 2:
        return rotateLeftRight();
      case 3:
        return rotateRightLeft();
      case 4:
        return rotateLeft();
      default:
        return this;
    }
  }

  // Returns an int value, representing the current state of 
  // the tree. Values represent the following:
  // 0 : no red children, no double red children -> do nothing
  // 1 : left, left red children -> rotateRight()
  // 2 : left, right red children -> rotateLeftRight()
  // 3 : right, left red children -> rotateRightLeft()
  // 4 : right, right red children -> rotateLeft()
  private int checkBalanceCondition() {
    Color RED = Color.RED;
    Color BLACK = Color.BLACK;
    if( color.equals(RED) || ( left.isEmpty() && right.isEmpty() ) ) {
      return -1;
    } else if( left.isRed() ) {
      if( left.getLeft().isRed() ) {
        return 1;
      } else if( left.getRight().isRed() ) {
        return 2;
      }
    } else if( right.isRed() ) {
      if( right.getRight().isRed() ) {
        return 4;
      } else if( right.getLeft().isRed() ) {
        return 3;
      }
    }
    return -1;
  }

  // Rotate the tree left about this node.
  // Results in a new tree, where this node is the left child, 
  // the right child is the new root, and the right-right grandchild
  // is the new right node
  private RedBlack<K,V> rotateLeft() {
    if( right.isEmpty() ) {
      throw new RuntimeException("Invalid Rotation");
    } else {
      @SuppressWarnings(value="unchecked")
      Node<K,V> r = (Node<K,V>) right;
      Node<K,V> newLeft; 
      newLeft = new Node<K,V>(left, 
                              k0, v0, 
                              r.getLeft(), 
                              c, 
                              Color.BLACK);
      return new Node<K,V>(newLeft, 
                           r.k0, r.v0, 
                           r.getRight().makeBlack(), 
                           c, 
                           Color.RED);
    }
  }

  // Rotate the tree left about the left child, then rotate
  // the tree right about the root node
  private RedBlack<K,V> rotateLeftRight() {
    if( left.isEmpty() || left.getRight().isEmpty() ) {
      throw new RuntimeException("Invalid Rotation");
    } else {
      @SuppressWarnings(value="unchecked")
      Node<K,V> l = (Node<K,V>) left;
      @SuppressWarnings(value="unchecked")
      Node<K,V> lr = (Node<K,V>) l.getRight();
      Node<K,V> newLeft, newRight;
      newLeft = new Node<K,V>(l.getLeft(), 
                              l.k0, l.v0, 
                              lr.getLeft(), 
                              c, 
                              Color.BLACK);
      newRight = new Node<K,V>(lr.getRight(), 
                               k0, v0, 
                               right, 
                               c, 
                               Color.BLACK);
      return new Node<K,V>(newLeft, 
                           lr.k0, lr.v0, 
                           newRight, 
                           c, 
                           Color.RED);
    }
  }

  // Rotate the tree right about the right child, then rotate
  // the tree left about the root node
  private RedBlack<K,V> rotateRightLeft() {
    if( right.isEmpty() || right.getLeft().isEmpty() ) {
      throw new RuntimeException("Invalid Rotation");
    } else {
      @SuppressWarnings(value="unchecked")
      Node<K,V> r = (Node<K,V>) right;
      @SuppressWarnings(value="unchecked")
      Node<K,V> rl = (Node<K,V>) r.getLeft();
      Node<K,V> newLeft, newRight;
      newLeft = new Node<K,V>(left, 
                              k0, v0, 
                              rl.getRight(), 
                              c, 
                              Color.BLACK);
      newRight = new Node<K,V>(rl.getRight(), 
                               r.k0, r.v0, 
                               r.getRight(), 
                               c, 
                               Color.BLACK);
      return new Node<K,V>(newLeft, 
                           rl.k0, rl.v0, 
                           newRight, 
                           c, 
                           Color.RED);
    }
  }

  // Rotate the tree right about this node.
  // Results in a new tree, where this node is the right child, 
  // the left child is the new root, and the left-left grandchild
  // is the new left node
  private RedBlack<K,V> rotateRight() {
    if( left.isEmpty() ) {
      throw new RuntimeException("Invalid Rotation");
    } else {
      @SuppressWarnings(value="unchecked")
      Node<K,V> l = (Node<K,V>) left;
      Node<K,V> newRight;
      newRight = new Node<K,V>(l.getRight(), 
                               k0, v0, 
                               right, 
                               c, 
                               Color.BLACK);
      return new Node<K,V>(l.getLeft().makeBlack(), 
                           l.k0, l.v0, 
                           newRight, 
                           c, 
                           Color.RED);
    }
  }

  ///////////////////////////////////////////////////////////
  ///   END Insert Auxilliary Methods
  ///////////////////////////////////////////////////////////

  RedBlack<K,V> makeBlack() {
    return new Node<K,V>(left, k0, v0, right, c, Color.BLACK);
  }

  RedBlack<K,V> getLeft() {
    return left;
  }

  RedBlack<K,V> getRight() {
    return right;
  }

  boolean isRed() {
    return color.equals(Color.RED);
  }
}


////////////////////////////////////////////////////////////////
/// Iterators
////////////////////////////////////////////////////////////////

// An Iterator over the Keys of an FMap
class FMapIterator<K> implements Iterator<K> {
  Vector<K> keys;  // The keys to be iterated over
  boolean hasNext;    // Does the Iterator have a next element?

  FMapIterator(Vector<K> keys) {
    this.keys = keys;
    hasNext = ! keys.isEmpty();
  }

  FMapIterator(Vector<K> keys, Comparator<? super K> c) {
    Collections.sort(keys, c);
    this.keys = keys;
    hasNext = ! keys.isEmpty();
  }

  public K next() {
    if( ! hasNext() ) {
      throw new NoSuchElementException();
    } else {
      K result = keys.get(0);
      while( keys.contains(result) ) {
        keys.remove(result);
      }
      hasNext = ! keys.isEmpty();
      return result;
    }
  }

  public boolean hasNext() {
    return hasNext;
  }

  // Not supported
  public void remove() {
    throw new UnsupportedOperationException();
  }
}   