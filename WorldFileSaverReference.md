# Data Saver #

This interface complements the `DataLoader` in that it allows you to save data to a schema the loader will read.

As its counterpart, each instance represents a single node in the document hierarchy.

## Attributes ##

You can save the attributes using the following methods:
```
/**
 * Saves a string value.
 * 
 * @param id                            element id
 * @param value
 */
void setStringValue( String id, String value );
        
/**
 * Saves an integer value.
 * 
 * @param id                            element id
 * @param value
 */
void setIntValue( String id, int value );
        
/**
 * Saves an float value.
 * 
 * @param id                            element id
 * @param value
 */
void setFloatValue( String id, float value );
```

## Child nodes ##

You can add child nodes using the following method:
```
/**
 * Adds a new child node with the specified tag.
 * 
 * @param id
 * @return
 */
DataSaver addChild( String id );
```

## Saving the data ##

You explicitly need to specify when you want to persist the data. The interface has the `flush` method that does that:

```
/**
 * Flushes the saver contents to an output stream.
 * 
 * @param stream
 */
void flush( OutputStream stream );
```

Simply call it, passing it an instance to an `OutputStream` you want the data to be written to.

## Implementations ##

At this moment, the engine contains just one implementation that can persist the data to an XML format.

You can obtain its instance by calling:
```
DataSaver saver = XMLDataSaver.create();
```