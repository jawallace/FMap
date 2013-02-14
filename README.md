FMap
====

This data structure is a Function Map, parameterized over Keys and Values. It utilizes the Abstract Factory Pattern to
deliver a different implementation to the user depending on whether the structure is constructed with a comparator
over the keys. If no Comparator is given, the Map is implemented as a simple Association List. If the Comparator is
given, the Map is implemented as a RedBlack Tree, a self balancing tree structure that ensures worst case performance
of O(log n) for lookup operations. In addition, the Visitor pattern is used for performing operations on all elements of
the Map.

Developed for an Object Oriented Design class, this structure demonstrates knowledge of the Abstract Factory Pattern,
the Visitor pattern, parametric polymorphism, and ad hoc polymorphism.

The file was benchmarked against Java's Tree Map, which also is implemented using RedBlack Trees, and it performed
roughly the same. The test suite was written by the Professor, I am planning on creating a similar test suite to add
here soon.
