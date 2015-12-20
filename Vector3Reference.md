# Vector3 #

This class represents a mathematical vector and provides you with ability to perform mathematical calculations on vectors.

You can:
  * add vectors
  * subtract them
  * multiply by a value ( the **scale** method )
  * calculate their length ( **mag** method )
  * normalize

Most of the operations are the so-called **in-place** operations, meaning that they don't return a new instance of a vector, instead they change the value of the vector the method is invoked on.

An example of such methods are the **add** and **sub** methods. They return a reference to the vector the operation was invoked on in order to allow the method call chaining.

The following operation:
```
Vector3 v1, v2, v3;
v1.add( v2 ).sub( v3 );
```

equals to:
v1 = v1 + v2;
v1 = v1 - v3.

## Memory saving mode ##
The class is designed to minimize the memory consumption - that's why it encourages a model where you keep a few `Vector3` instances as the fields in your class and you perform operations using them.

This approach decreases the memory footprint required if you were forced to allocate a new `Vector3` instance each frame i.e.

In order to allow a simple vector value change, the class has two **set** methods, accepting a canonic vector version ( 3 coefficients ), or another `Vector3` instance the coefficients of which we want to copy.

