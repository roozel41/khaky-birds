# World #

## Introduction ##

A world provides a means to poopulate of games with all sorts of entities - trees, ground, living creatures etc.

The basic world building block is an `Entity`. Build the stuff you want to populate the world with by extending this class.


## Entities management ##
Basic methods you can populate your world with are described here:

```
/**
 * Adds a new entity to the world.
 * 
 * @param entity
 */
public void addEntity( Entity entity );
       
/**
 * Removes the entity from the world
 * 
 * @param entity
 */
 public void removeEntity( Entity entity );

/**
 * Executes an operation on all registered entities.
 * 
 * @param operation
 */
public void executeOperation( EntityOperation operation );

/**
 * Looks for the first entity of the specified type
 * 
 * @param entityType
 * @return
 */
public Entity findEntity( Class entityType );
```

**addEntity** and **removeEntity** simply add and remove an entity to the world, populating it.

**executeOperation** allows you to define an operation that will be executed on **ALL** entities in the world. The operation interface looks like this:
```
public interface EntityOperation
{
        /**
         * This is where you put the operation that will alter entity's state.
         * 
         * @param entity
         */
        void visit( Entity entity );
}
```
You simply need to implement the **visit** method and tell it what you want to do with an entity.

**findEntity** looks for the first entity of the specified type and returns a reference to it - it may come in handy when you want to reference another entity in the world from some part of the code.

## Entity responsibilities ##
The engine features an MVC architecture, and this imposes certain way you need to treat things.

An entity is a part of the model - meaning that it should feature the state of a particular world object as well as methods to change that state - but that's it.

The world can have multiple views attached - views such as a renderer or the controllers factory. The world ensures that all attached views will learn about all the entities that populate it and will be able to create their own representations.

I.e. a renderer will create a renderable representation of an entity etc.

### Basic state of an entity ###
This is a game engine - so we know that our entities will populate worlds that have some metric set.

For that very purpose, each entity contains two state variables:
  * a position
  * a bounding shape

They specify where an entity is located an how much space it takes.

`Entity` class features several methods that allow you to change and query that state:

```
/**
 * Sets the bounding box of the entity.
 * 
 * @param bb
 */
public final void setBoundingBox( BoundingBox bb );
        
/**
 * Returns the entity's bounding shape.
 * 
 * @return
 */
public final BoundingShape getBoundingShape();

/**
 * Returns the world bounds of the entity.
 * @return
 */
public final BoundingBox getWorldBounds();
        
/**
 * Returns the position in the Z buffer. 
 * 
 * Numbers towards the negative values are closer to the screen,
 * and the numbers towards the positive values are farther from the screen.
 * 
 * @return
 */
public final Vector3 getPosition();
        
/**
 * Sets the entity's position.
 * 
 * @param x
 * @param y
 * @param z
 */
public final void setPosition( float x, float y, float z );
        
/**
 * Sets the entity's position.
 * 
 * @param rhs
 */
public final void setPosition( Vector3 rhs );
               
/**
 * Translates the entity by the specified vector.
 * 
 * @param dx
 * @param dy
 * @param dz
 */
public void translate( float dx, float dy, float dz );
        
/**
 * Translates the entity by the specified vector.
 * 
 * @param dx
 * @param dy
 * @param dz
 */
public void translate( Vector3 ds );
```

### Setting an entity up ###
The entity operates from the world context. You may want to set up certain inter-entity dependencies in your implementations, or set up some world-dependent stuff. `Entity` class features two methods that allow you to do that:
```
/**
 * Informs the entity that it's been added to the world.
 * 
 * @param hostWorld
 */
public void onAddedToWorld( World hostWorld );
        
/**
 * Informs the entity that it's been removed from the world.
 * 
 * @param hostWorld
 */
public void onRemovedFromWorld( World hostWorld );
```

Implement them with the setup/release code you require.

## Entity events ##
Events can be sent to an entity. You can attach listeners to an entity to listen to those events.

### Listening to events ###
A listener implements an interface `EntityEventListener`, which makes you implement one method:

```
/**
 * Called when the even should be processed by the listener.
 * 
 * @param event
 */
public void onEvent( EntityEvent event );
```

This method will be called whenever an entity receives an event. In order to tell what event it was you can simply check its type:

```
@Override
public void onEvent( EntityEvent event )
{
     if ( event instanceOf MyCoolEvent )
     {
          // do something
     }
}
```


### Sending an event ###
In order to **send** events to an entity, simply call 'Entity.sendEvent' method, specifying the type of event you want to send.

Let's say you have a custom event called `Shot` that tells an entity it got shot and specifies what it was shot with:

```
enum Weapon
{
     WPN_Pistol,
     WPN_Shotgun,
     WPN_Rifle,
}

class Shot implements EntityEvent
{
     public Weapon    m_weapon;
}
```

If you want to tell an entity it got shot with a Rifle, you can do this:
```
Shot shotEvent = shotEntity.sendEvent( Shot.class );
shotEvent.m_weapon = Weapon.WPN_Rifle;
```

The **sendEvent** method takes a single param which is the event type, and returns the instance of event you can initialize with some data.

An event itself is a regular class - you can define methods in it, make it do stuff once you receive it etc.

Let's take a look at the **send event** method signature:
```
/**
 * Transmits an entity event to the specific listeners.
 * 
 * CAUTION: single entity can only receive up to MAX_EVENTS_COUNT per frame.
 * Once that number is reached, no new events will be transmitted to the entity
 * and this method will return null pointers.
 * 
 * @param event
 * @throws EntityEventException		when the allowed number of sent events was exceeded 
 */
public final < T extends EntityEvent > T sendEvent( Class< T > eventClass ) throws EntityEventException;
```

First of all - it may return **null** in case the entity doesn't support the specified event type. **THAT'S IMPORTANT TO KEEP IN MIND**.

Second of all - it may throw an exception if the entity received too many events of this type this frame.
Why does it throw an exception instead of returning **null** instead - because it points to a programming error - you flood it with an event of one type every frame - and that's bad.

### Registering event types ###
An entity needs to be aware that it should process events of the specific type.

In order to do that, you need to register an event type with it useing the **registerEvent** method:
```
/**
 * Registers an event type.
 * 
 * @param eventClass
 * @param factory			factory that will create the events
 */
public final < T extends EntityEvent > void registerEvent( Class< T > eventClass, EventFactory< T > factory );
```

And here's how you use it:
```
// define events the entity responds to
registerEvent( Shot.class, new EventFactory< Shot >() { @Override public ShotcreateObject() { return new Shot(); } } );
```

## Aspects ##

An aspect defines a set of attributes for an entity, that tells it how to do some additional stuff.

You can define an aspect using the **defineAspect** method:
```
/**
 * Defines a new aspect of the entity.
 * It's protected - aspects can only be defined in the entity's implementation
 * as a part of its state definition.
 * 
 * @param newAspect
 */
protected final void defineAspect( Aspect newAspect )
```

An example of such an aspect is the `DynamicObject` aspect, which gives an entity movement capabilities.

## Views ##
The world on its own is just a large storage of entities. It doesn't feature an `update` or `draw` methods for instance, because we want to be able to define different ways our entities will be updated or drawn in.

In order to achieve that, we introduced the views that will operate on the entities in the world.

The following methods allow you to attach and detach a view from the world.

```
/**
 * Attaches a new view to the world.
 * 
 * @param newView
 */
public void attachView( WorldView view );

/**
 * Attaches a new view to the world.
 * 
 * @param newView
 */
public void detachView( WorldView view );
```

The view interface is called `WorldView` and here's how it looks:
```
public interface WorldView 
{
        /**
         * Called when the view is attached to the world.
         *  
         * @param world
         */
        void onAttached( World world );
        
        /**
         * Called when the view is detached to the world.
         *  
         * @param world
         */
        void onDetached( World world );
        
        /**
         * Called whenever a new entity is added.
         * 
         * @param entity
         */
        void onEntityAdded( Entity entity );
        
        /**
         * Called whenever a new entity is removed.
         * 
         * @param entity
         */
        void onEntityRemoved( Entity entity );
}
```

It gets informed about it being attached or detached from a world - this is the place where you might want to set up some world-dependent stuff.

The two latter methods are called when an entity is added or removed from the view.
Those allow you to register them in the view, create some representations for them etc.

There already are some views implemented, check out the [renderer](Renderer2DReference.md) and the [controllers view](ControllersViewReference.md)

## World size ##
The game world has a fixed size defined upon its instantiation.
You can query that size using the following methods:

```
/**
 * Returns the width of the world.
 * 
 * @return
 */
public float getWidth();
        
/**
 * Returns the height of the world.
 * 
 * @return
 */
public float getHeight();
```

Keep in mind that although our position representation with the `Vector3` class supports negative numbers, the `World` class and the existing views were designed not to support them.

So in other words - try putting your entities on the positive side of the axes, 'cause the world span is expressed as (0, 0)->(worldWidth, worldHeight).

Another cautious thing - even though we support 3D rendering and operations, the world is a 2D concept. What it means is that we assume that it spans into infinity on the Z axis.

## Persistance ##
You can ( and should ) define your game world in an asset.
Engine supports XML files with world configuration.

### Loading ###
To load a world from a file, simply call `World.load` method, passing it a DataLoader` instance:
```
try 
{
      InputStream worldFileStream = game.getFileIO().readAsset( "game_assets/my_world.xml" );
      m_world.load( XMLDataLoader.parse( worldFileStream ) );
} 
catch ( IOException e ) 
{
     Log.d( "Game", "Error while loading world" );
     throw new RuntimeException( e );
}
```

This command will create an input stream with the world XML file in it, and then create a loader instance using the `XMLWorldFileLoader` loader implementation and pass it to the world so that it can restore its state.

Here's the `load` method signature:
```
/**
 * Loads the world's contents.
 * 
 * CAUTION: it doesn't remove the previous world's contents!!!
 * 
 * @param loader                loader that persists the world
 */
public void load( DataLoader loader );
```

### Registering entity types ###
Your game may feature many specific entity types. You need to inform the world about them before you load it from a file, so that the world cn create proper entity instances based on what is written in the XML file.

You can do that using the `registerEntity` method:
```
/**
 * Informs the world about a factory that will be instantiating entities
 * of the specified type.
 * 
 * @param type
 * @param factory
 */
public void registerEntity( Class< ? extends Entity > type, EntityFactory factory );
```

And here's how you do it:
```
m_world.registerEntity( Bird.class, new EntityFactory() { @Override public Entity create() { return new Bird(); } } );
```

You simply pass your entity class as the firsts argument, and an `EntityFactory` interface implementation that creates the actual entity instance as the second argument.

**REMEBER** - you have to register entity types **BEFORE** you load the world from a file.

### XML file layout ###
Here's a sample XML file:

```
<?xml version="1.0" encoding="UTF-8"?>
<World width='9.6' height='4.8'>
        <Entity type="Ground" />
        <Entity type="ElectricCables" />
        
        <Entity type="Pedestrian" facing='180.0'>
                <localBounds minX='-0.5' minY='-0.5' minZ='-0.5' maxX='0.5' maxY='0.5' maxZ='0.5' />
                <position x='3' y='5' z='80' />
                <DynamicObject rotation='-45.0'>
                        <velocity  x='1' y='0' z='0' />
                </DynamicObject>
        </Entity>

        <Entity type="Pedestrian" />
</World>
```

This particular definition will set the world to be 9.6 meters wide and 4.8 meters high, and will add 4 entities - a Ground, ElectricCables, and two Pedestrians, one of which has the specific parameters set, and the other one simply has the default parameters.

### Main XML tags ###
So what tags are there:
  * **World** - it features two attributes **width** and **height** that describe its size
  * **Entity** - describes an entity, needs to be embedded inside a **World** tag. Features a **type** attribute that contains a simple name of the entity class.

Those two tags will help you populate the world with entities.

### Generic entity tags ###
Entity class features position, facing and a bounding shape parameters. You're probably wondering how to set those?

It's simple:
```
<Entity type="Pedestrian" facing='180.0'>
     <localBounds minX='-0.5' minY='-0.5' minZ='-0.5' maxX='0.5' maxY='0.5' maxZ='0.5' />
     <position x='3' y='5' z='80' />
</Entity>
```

  * **facing** - an attribute that specifies the angle which the entity 's facing
  * **localBounds** - describes a bounding box around the entity ( in local space )
  * **position** - entity's position ( in world space ) considered the middle point of the specified bounding box.

Sometimes you want to **specify the initial position** of the entity with respect to its **top-left corner** instead of the center of its bounding box. If you want to do that, just add an attribute `coordinatesMode='TopLeft'`

### Custom entity tags ###
The tags above described something every entity has. But your game features custom entities, each of which has a unique set of parameters.
How can you set those?

You need to define two methods that will handle those parameters and add them to your entity implementation. How you handle the parameters will describe how you should describe them in an XML file.

The two methods you need to override are:
```
/**
 * Called to deserialize entity's implementation-specific state.
 * 
 * @param loader
 */
public void onLoad( DataLoader loader );
        
/**
 * Called to deserialize entity's implementation-specific state.
 * 
 * @param loader
 */
public void onSave( DataSaver saver );
```

So let's analyze an example - let's assume you want to create an `Apple` entity, that is described by how sweet it is.

Here's how the class and the two methods saving those would look like.
```
class Apple extends Entity
{
        private int   m_sweetness;

        // .....

        @Override
        public void onLoad( DataLoader loader ) 
        {
                m_sweetness = loader.getIntValue( "sweetness" );
        }
        
        @Override
        public void onSave( DataSaver saver ) 
        {
                saver.setIntValue( "sweetness", m_sweetness );
        }
}
```

and the XML section that creates an entity with the 'sweetness' parameter defined would look like this:
```
<Entity type="Apple" sweetness='10' />
```


Check out the dedicated references on the [Data Loader](WorldFileLoaderReference.md) and [Data Saver](WorldFileSaverReference.md) for more information.

## Loading large resources ##

A level will typically employ many large texture atlases, sounds, music clips etc.

Some of this stuff will be requested for the first time some time after the level starts, and will need to be loaded then - that may cause some stutter when such a resource is loaded.

To prevent that from happening, you can specify a list of 'large' assets that need to be loaded when the level is being loaded and initialized.

You specify them in a special XML node in a `World` XML file:

```
<ResourcesCache>
	<Resource type="Texture" path="gameplay/atlas.png"/>
	<Resource type="Texture" path="gameplay/atlas2.png"/>
	<Resource type="Texture" path="backgrounds/park_alleyway/atlas.png"/> 
</ResourcesCache>
```