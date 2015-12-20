# Physics #

The physics view is responsible for running a physical simulation of the game world as well as for performing the collision detection.


## Physical Body ##
Entities are represented using `PhysicalBody` abstract class.


## Bodies factory ##
You need to set up the `PhysicsView` instance so that it knows what physical representations it should create for the specific entities.

You achieve that by registering a factory.

Here's a sample registration:

```
m_physicsView.register( Bird.class , new PhysicalBodyFactory() { @Override public PhysicalBody instantiate( Entity parentEntity ) { return new StaticObject( parentEntity ); } } );
```

The **register** method takes a class of an entity as its first argument, and the factory that should be used to create the physical body as the second one.

The factory itself is an interface with a single method **instantiate**:public interface PhysicalBodyFactory extends ObjectFactory< Entity, PhysicalBody > 
{
        /**
         * The factory method.
         * 
         * @param parentEntity
         * @return
         */
        PhysicalBody instantiate( Entity parentEntity );
}}}}

Implement it as you see above in order to create a body you desire.

The factory concept this view uses is described in section [GenericFactory Generic factory].```