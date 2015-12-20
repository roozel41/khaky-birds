# How to make entities move #

You can achieve that in two ways:

  * each entity has a **translate** and **sePosition** methods - you can simply call them from your controller code, and that's it

  * you can use **steering behaviors** and physics to make your moving agents move in a cool and sophisticated fashion.

Whichever approach you take however, be sure to mark the entity as **movable**. It's extremally important - due to optimization reasons, all entities are considered static by default, and as such will be subject to certain optimizations, which if they move, will cause bugs in them not being displayed etc.

**How to make your entity movable?** In its constructor, add a `DynamicObject` aspect to it.
This aspect defines the movement capabilities of an entity ( how fast can it travel, how quickly can it rotate etc. ).

```
// add movement capabilities
final float maxLinearSpeed = 1.0f;
final float maxRotationSpeed = 180.0f;
defineAspect( new DynamicObject( maxLinearSpeed, maxRotationSpeed ) );
```


Ok - now that you have it, you can use [Steering behaviors](SteeringBehaviorsReference.md) on the entity.