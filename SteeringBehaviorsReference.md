# Steering behaviors #

Steering behaviors allow you to refine your control over an agent by making it move in a semi-physical way and giving an impresion of a fairly advanced movement scheme.


## Introduction ##

You can use them by creating an instance of `SteeringBehavior` class, and passing it a **movable** entity.

A movable entity is an entity with a `DynamicObject` aspect set ( take a look [here](MakeEntitiesMove.md) to see how it's done ).

If the entity doesn't have that aspect, it will stay imprevious to any movement commands.

**CAUTION** :The behavior instance needs to be **updated** each tick.

## Behaviors list ##

  * seek

```
/**
 * Makes the entity rush towards the specified goal position.
 * 
 * @param goal
 * @return instance to self, allowing to chain commands
 */
public SteeringBehaviors seek( Vector3 goal )
```

  * arrive

```
/**
 * Makes the entity rush towards the specified goal position, and arrive at that position
 * gently by breaking at the very last moment
 * 
 * @param goal
 * @param breakDistanceFactor		factor which tells how far before the goal the breaking 
 * process should begin ( the larger the number, the earlier it will start )
 * 
 * @return instance to self, allowing to chain commands
 */
public SteeringBehaviors arrive( Vector3 goal, float breakDistanceFactor )
```

  * flee

```
/**
 * Makes the entity flee from the specified point.
 * 
 * @param from
 * 
 * @return instance to self, allowing to chain commands
 */
public SteeringBehaviors flee( Vector3 from )
```

  * wander ( there are 2 versions of this behavior )

```
/**
 * Makes the entity wander around aimlessly.
 * 
 * @return instance to self, allowing to chain commands
 */
public SteeringBehaviors wander();

/**
 * Makes the entity wander around aimlessly.
 * 
 * @param initialDir		initial movement direction
 * @return instance to self, allowing to chain commands
 */
public SteeringBehaviors wander( Vector3 initialDir );
```

  * pursuit

```
/**
 * Makes the entity chase another entity.
 * 
 * @param goal
 * 
 * @return instance to self, allowing to chain commands
 */
public SteeringBehaviors pursuit( Entity goal )
```

  * evade

```
/**
 * Makes the entity run away from another entity.
 * 
 * @param pursuer
 * 
 * @return instance to self, allowing to chain commands
 */
public SteeringBehaviors evade( Entity pursuer )
```

  * circle

```
/**
 * Makes the entity circle around a point.
 * 
 * @param anchorPt
 * @oaram radius
 * 
 * @return instance to self, allowing to chain commands
 */
public SteeringBehaviors circle( Vector3 anchorPt, float radius )
```

  * faceMovementDirection

```
/**
 * Makes the entity face its movement direction ( direction of the velocity vector )
 * 
 * @return instance to self, allowing to chain commands
 */
public SteeringBehaviors faceMovementDirection();
```

  * lookAt
```
/**
 * Makes the entity rotate so that it's looking down the specified vector.
 * 
 * @param lookAtVec
 * @return instance to self, allowing to chain commands
 */
public SteeringBehaviors lookAt( Vector3 lookAtVec );
```

## Language ##
The class was constructed in a way that allows you to define movement behaviors using a **dot-language** syntax:

```
// behavior that will make an agent wander aimlessly, always trying to face its movement direction
m_sb.begin()
   .wander()
   .faceMovementDirection();

// behavior that will make an agent run towards the specified position, always looking in the specified direction
m_sb.begin()
   .seek( m_goToPos )
   .lookAt( lookingDirection );
```