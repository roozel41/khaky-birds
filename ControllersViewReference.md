# Controllers view #

## Introduction ##

This view allows you to instantiate objects for controlling your entities.

That's right - this is where you create your AI.

You define your AI controller by extending `EntityController` class.
The extension requires you to implement an `update` method, which will be updated every frame.

Since the constructor of such a controller receives an entity instance it should control, you can now write the cone that will change the entity's state.

A sample implementation:

```

class MyController extends EntityController
{
     private Entity           m_controledEntity;

     public MyController( Entity controledEntity )
     {
          super( controledEntity );
          m_controledEntity = controledEntity;
     }

     @Override
     public void update( float deltaTime )
     {
          m_controledEntity.translate( 1.0f * timeDelta, 0, 0 );
     }
```

It will move the entity left with the speed of 1 m/s.

## Controllers factory ##
You need to set up the `ControllerView` instance so that it knows what controllers it should create for the specific entities.

You achieve that by registering a factory.

Here's a sample registration:

```
m_controllersView.register( Bird.class , new EntityControllerFactory() { @Override public EntityController instantiate( Entity parentEntity ) { return new BirdController( m_game.getInput(), parentEntity ); } } );
```

The **register** method takes a class of an entity as its first argument, and the factory that should be used to create the controllers as the second one.

The factory itself is an interface with a single method **instantiate**:
```
public interface EntityControllerFactory extends ObjectFactory< Entity, EntityController >
{
        /**
         * The factory method.
         * 
         * @param parentEntity
         * @return
         */
        EntityController instantiate( Entity parentEntity );
}
```

Implement it as you see above in order to create a controller you desire.

The factory concept this view uses is described in section [Generic factory](GenericFactory.md).