# Generic factory #

## Introduction ##
It's an implementation of a design pattern widely known as Factory.

The pattern allows you to instantiate classes from class hierarchy B that correspond to classes in the class hierarchy A.

If you're more interested in the theory, take a look at the [formal description](http://www.oodesign.com/factory-pattern.html)


What we apply the pattern to in the engine is to create representations if the entities in particular views.

Let's say I have a family of entities, and I want to create dedicated visual representations.
What I need is to simply create a GenericFactory object, tell it what entity classes I want to associate with which representation classes, and then I can call the instantiate method, which will create an instance for me.

## Example ##
Let's take a look at an example. First - stubs of the classes:
```
class Entity
{
   // ...
}

class Dog extends Entity
{
  // ...
}

class Cat extends Entity
{
  // ...
}

class Visual
{
  // ...
}

class DogVisual
{
   DogVisual( Dog dog ) {}

   // ... 
}

class CatVisual
{
   CatVisual( Dog dog ) {}

   // ... 
}
```

So we have two hierarchies:
![http://khaky-birds.eclipselabs.org.codespot.com/files/factory_hierarchy.jpg](http://khaky-birds.eclipselabs.org.codespot.com/files/factory_hierarchy.jpg)

Now - the thing here is that **if we knew the type** of an object we're dealing with, we could create the applicable representation ourselves.

But what if we don't know that - that's exactly what's going on in our `World` and `View` classes - they only operate on `Entities` and their respective representations a particular view uses - but they have no idea what sort of entities or visuals you'll create for your game. So they have to be flexible and generic.

And that's where the `GenericFactory` steps in:

```
// factory setup
GenericFactory< Entity, Visual > factory;

factory.register( Dog.class, new VisualFactory() { @Override public Visual instantiate( Entity entity ) { return new DogVisual( (Dog)entity ); } );
factory.register( Cat.class, new VisualFactory() { @Override public Visual instantiate( Entity entity ) { return new CatVisual( (Cat)entity ); } );

// .... a completely diffferent place in the code 
Entity someEntity;
Visual someVisual = factory.create( someEntity );
```

And it's as simple as that.

## Setup ##

The setup might be a bit intimidating, but it's no biggie actually.
The **register** method takes two parameters:

```
/**
 * Registers the class of a visual that should be created
 * when an entity of the specified type is added.
 * 
 * @param type
 * @param factory
 */
public void register( Class type, ObjectFactory< Template, Representation > factory );
```

The first one is the type of a solid object from the A hierarchy ( in our example the A hierarchy was the hierarchy of `Entity` classes, and the B hierarchy was the hierarchy of the `Visual` classes ).

The second one is an instance of an `ObjectFactory` interface, that knows how to create an instance of the representation.
The interface is pretty straightforward - it has only one method **instantiate** that is responsible for creating the representation object.

```
public interface ObjectFactory< Template, Representation > 
{
        /**
         * The factory method.
         * 
         * @param parentTemplate
         * @return
         */
        Representation instantiate( Template parentTemplate );
}
```

Because it's generic, you might want to extend it into your own custom interface to facilitate the process. In our example that would be the `VisualFactory` interface, which looks like this:
```
public interface VisualFactory extends ObjectFactory< Entity, Visual > 
{
        /**
         * The factory method.
         * 
         * @param parentTemplate
         * @return
         */
        Visual instantiate( Entity parentTemplate );
}
```