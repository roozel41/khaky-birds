# How to implement interactions with the environment? #


What you typically would like to achive when asking this question is:
"So I have my agent, and I want him to run when a grenade explodes nearby. How can tell him abou the granade blast?".

And the answer is - you use [EntityEvents](WorldReference#Entity_events.md) for those.

## Event definition ##
A grenade explosion is an event, same as getting hit with a rotten tomato, being crapped on etc.

So what you need to do first is to define your event:

```
class GrenadeExplosion implements EntityEvent 
{
     private float m_blastStrength;
}
```

An event should contain data relevant to the information it wants to convey.
If we wanted to inform about the type of grenade that exploded or its  strength, we could add a field in the event and fill it with the data.


The most important thing is to tell the entity that it should process the events of this type.

In order to do that, you need to invoke the `registerEvent` method on an entity **BEFORE** you send any events of that type to it - otherwise it won't receive them.

```
entity.registerEvent( GrenadeExplosion.class, new EventFactory< GrenadeExplosion >() { @Override public GrenadeExplosion createObject() { return new GrenadeExplosion (); } } );
```

Those requirements give you some hints as to which place is the best to do that:
  * if the events apply to the entity type ALWAYS, no matter what representations represent it, simply put the code in its constructor.
  * if the event is specific to some representation type ( i.e. `CollisionEvent` ), register it **ON THE ENTITY** in the representation's constructor.

## Event transmission ##

Now - how do we send it to an entity? That's simple - use `Entity.sendEvent` method. It will return an instance of the event, so that you can fill it with data.

```
GrenadeExplosion event = affectedEntity.sendEvent( GrenadeExplosion.class );
event.m_blastStrength( 10.0f );
```

The method accepts a single parameter - the event type. You need to fill the relevant data on the returned instance.

And that's it - the entity will receive the event in the next tick.

## Event reception ##
It's obvious that the entity itslef is not interested in the event. After all - it's just a data bundle.

What might be interested in the explosion are its representations:
  * you may want to change entity's looks
  * you may want to change entity's behavior
etc.

That means you want to customize your representation classes so that they are able to receive the events.
You can do that by implementing the `EntityEventListener` interface

```
class MyEntityAI extends EntityController implements EntityEventListener
{
     // ....
     public MyEntityAI( Entity entity )
     {
          super( entity );
          entity.attachEventListener( this );

          // remaining init code
     }

     // ....

     @Override
     public void onEvent( EntityEvent event )
     {
          if ( event instanceof GrenadeExplosion )
          {
               // implement the explosion results here
          }
     }

     // ....
}
```