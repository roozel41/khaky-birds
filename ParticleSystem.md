# Particle system #

A particle system can be used to generate a large amounts of moving objects.

## Introduction ##

A particle system manages particles. A particle is something that can be displayed on the screen and moved around.
They only serve the decorative purpose.

We generally want to reuse the existing engine resources to be displayed as particles, that's why we allow to plug in sprites and animations as particles.

Particles are emitted by **emitters**. Those provide them with initial position and velocity.
You can position the emitters relative to the global position of the particle system.

Apart from that, their velocity and orientation can be dynamically changed by various **affectors**. Those allow you to create an impression of wind blowing the particles in a particular direction, or a black hole sucking them in etc.

## Playback ##
A particle system is just a resource and can't be displayed on its own.
You need a `ParticleSystemPlayer` instance to be able to display the particles.

You can define it like so:

```
ParticleSystem someParticleSystem;
boolean playLooped = true;
m_psPlayer = new ParticleSystemPlayer( someParticleSystem, playLooped );
```

The second parameter, **playLooped** tells if the particle system should continue reemitting the particles when they die, or should it emit them just once and then stop.

If you want to play the particles, simply call this method on the player:
```
/**
 * Renders the particle system.
 * 
 * @param x				position in the world
 * @param y				position in the world
 * @param batcher
 * @param deltaTime
 */
public void draw( float x, float y, SpriteBatcher batcher, float deltaTime ) 
```

## XML definition ##

As with every other resource, you can define a particle system in an XML file.

The skeleton is pretty straightforward:
```
<?xml version="1.0" encoding="UTF-8"?>
<ParticleSystem >
	<Emitter type="RadialParticleEmitter" >
		<pos x='0' y='0' z='0' />
	</Emitter>
	<Emitter type="RadialParticleEmitter" >
		<pos x='1' y='0' z='0' />
	</Emitter>
	<Affector type="LinearMovementAffector"/>
	<Affector type="LinearMovementAffector"/>
</ParticleSystem>
```

First there's the `ParticleSystem` tag which defines the system itself.

This tag can contain a number of children tags that come in two falvors:
  * Emitter  - definitions of emitters
  * Affector - definitions of affectors

### Emitters definition ###

At the generic level, an emitter requires you to specify a number of parameters so that it knows how to emit the particles, and - what's even more important - what kind of particles to emit.

A default emitter definition would look like so:

```
<Emitter type="SomeEmitterType" frequency='0.1' timeToLive='7' amountEmittedEachTick='3' totalAmount='3' particleType="TexturedParticle" texturePath="khaky_birds_prototype/textures/crapHitParticle.xml" emitterSpecificParam='0'>
</Emitter>
```

So let's go through them one by one:
  * **type**  - specifies the emitter class. At this moment we support two emmiter types:
    * `RadialParticleEmitter` - emits the particles from a single point concentrically outside to form a circular pattern
    * `DirectionalParticleEmitter` - emits the particles from a single point in a single specified direction

  * **frequency** - how often should the next batch of particles be emitted
  * **timeToLive** - how long (in seconds ) should one particle live before it can be reemited
  * **amountEmittedEachTick** - size of a particle emission batch
  * **totalAmount** - how many particles can be emitted in total
  * **width**, **height** - size of a particle
  * **particleType** - what resource should be used as a particle. Currently you have two options here:
    * `TexturedParticle` - use a sprite
    * `AnimatedParticle` - use an animation

As for the data specific to particular emitter implementations:

  1. `RadialParticleEmitter`
```
<Emitter type="RadialParticleEmitter" speed='1' >
	<pos x='0' y='0' z='0' />
</Emitter>
```
    * `speed` - how fast should the particles travel initially
    * `pos` - offset position relative to the particle system's position from which the particles should be emitted

> 2. `DirectionalParticleEmitter`
```
<Emitter type="DirectionalParticleEmitter" force='3.5' dispersionAngle='30' >
	<pos x='0' y='0' z='0' />
	<direction x='1' y='0' z='0' />
</Emitter>
```
    * `pos` - offset position relative to the particle system's position  from which the particles should be emitted
    * `direction` - direction with which the particles should be emitted
    * `force` - mltiplied by the direction gives you the particle velocity
    * `dispersionAngle` - defines an angle in which the particles will be emitted. Specify 0 if you want all particles to be emitted in the same direction, or >0 if you want to create a fountain-like effects

> 3. `LineParticleEmitter`
```
<Emitter type="LineParticleEmitter" >
	<lineStart x='0' y='-1' z='0' />
	<lineEnd x='0' y='1' z='0' />
	<velocity x='1' y='0' z='0' />
</Emitter>
```
    * `lineStart` - offset position relative to the particle system's position where the emission line begins
    * `lineEnd` - offset position relative to the particle system's position where the emission line ends
    * `velocity` - velocity ( speed & direction ) with which the particles should be emitted

### Particle types ###

The particle types the engine supports are:

  1. `AnimatedParticle`
```
<Emitter particleType="AnimatedParticle" animPath="animations/myAnimation.xml" width='0.5' height='0.5' >
</Emitter>
```
    * `animPath` - specifies the location of an animation definition asset

> 2. `TexturedParticle`
```
<Emitter particleType="TexturedParticle" texturePath="textures/myTexture.xml" width='0.5' height='0.5' >
</Emitter>
```

  * `texturePath` - specifies the location of a texture definition asset

> 3. `RandomlyTexturedParticle` - sometimes you want the emitter to use random textures for the particles.

```
<Emitter particleType="RandomlyTexturedParticle" >
	<Textures atlasName='backgrounds/meadow/meadow.png' alphaOp='test' >
		<Texture x='859' y='107' w='18' h='20' />
		<Texture x='878' y='107' w='18' h='20' />
		<Texture x='896' y='107' w='18' h='20' />
	</Textures> 
</Emitter>
```
> You need to specify the texture regions you want the emiter to choose from.
> _CAUTION_: I went for an embedded texture region definition here, as opposed to the referenced texture resource used by `TexturedParticle` - seems more convenient. If it turns out to be, change the `TexturedParticle` to work in the same way.

> 4. `TracerParticle` - for emitting procedurally drawn 'spark'-like particles.

```
<Emitter particleType="TracerParticle" particleWidth='2.0' fade="true" >
	<ParticleColors >
		<Color r='255' g='235' b='135'/>
		<Color r='255' g='178' b='112' />
		<Color r='255' g='142' b='132' />
	</ParticleColors> 
</Emitter>
```

  * `particleWidth` - width ( in pixels ) of a particle line
  * `fade` - should the particle color fade as its lifetime expires?

### Affectors definition ###

The supported affectors:

  1. `LinearMovementAffector` - changes the speed of particles by _blowing_ them in the specified direction

```
<Affector type="LinearMovementAffector">
	<velocity x='1' y='0' z='0'/>
</Affector>
```

> 2. `BlowMovementAffector` - changes the speed of particles by _blowing_ them away from the blow axis. You need to specify the `range` from the specified axis in which the affector will work, the blowing `force`, the `origin` in praticle system space and the orientation of the `blowAxis`

```
<Affector type="BlowMovementAffector" range='2.0' force='0.5' >
	<origin x='-0.5' y='0' z='0' />
	<blowAxis x='1' y='0' z='0' />
</Affector>
```

> 3. `RotationAffector` - rotates the particles around their mass center with the specified angular speed.

```
<Affector type="RotationAffector" angularSpeed='6.0'/>
```

> 4. `AccelerationAffector` - accelerates particles. A perfect solution for creating effect that include external forces, such as **gravity**

```
<Affector type="AccelerationAffector" >
	<acceleration x='0' y='-3' z='0' />
</Affector>
```