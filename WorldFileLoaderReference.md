# Data Loader #

This interface allows to define an XML schema for your data and load it from an XML file.

## Introduction ##

Every instance represents a node in an XML file. So in case of this file:
```
<World>
  <Entity />
  <Entity />
</World>
```

`World` tag will be represented by a separate instance, and each of the `Entity` tags will also have an instance ready.

Btw. - XML representation is one of the implementations of the interface, and I'm using it in this example because it's the easiest to perceive in terms of how data are represented.

## Attributes ##

Each node can have a set of attributes:
```
< Entity  attr1="ala" attr2="1" attr3="0.0" />
```

You can access those attributes using the following methods:
```
/**
 * Reads a string value from the node.
 * 
 * @param id                            element id
 * @return
 */
public String getStringValue( String id );
        
/**
 * Reads an integer value from the node.
 * 
 * @param id                            element id
 * @return
 */
public int getIntValue( String id );
        
/**
 * Reads a float value from the node.
 * 
 * @param id                            element id
 * @return
 */
public float getFloatValue( String id );
```

## Child nodes ##

You can only have uniquely named attributes in a node. So if you want to define an array of some attributes, you have to use a different mechanism.

Also - an attribute is a simple type, and what if you wanted to persist complex types ( like Vectors etc. ).

Each node can also contain a number of children nodes, which don't share that constraint.

Let's see how such a thing looks like from an XML perspective - let's assume that our node has an array of integer values:

```
<Collection>
  <Int val='0' />
  <Int val='2' />
  <Int val='4' />
  <Int val='1' />
</Collection>
```

How to go about reading it? :
```
DataLoader collectionNode;  // this node represents the <Collection> node

for ( DataLoader child = collectionNode.getChild( "Int" ); child != null; child = child.getSibling() )
{
   int val = child.getIntValue( "val" );
}
```

## Implementations ##

Currently the engine supports just an implementation that reads definitions from XML files.

Here's how you can obtain an implementation based on an InputStream with an XML contents:

```
try 
{
       InputStream worldFileStream = game.getFileIO().readAsset( "khaky_birds_prototype/test_world.xml" );
      
       DataLoader loader = XMLDataLoader.parse( worldFileStream );
} 
catch ( IOException e ) 
{
       // exception handling code
}
```