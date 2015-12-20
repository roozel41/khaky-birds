# Renderer 2D #

The renderer gives you ability to draw 2D sprites on the screen.

The drawn screen can be a lot larger than the screen. The visible portion of the screen is defined using a camera.


## Sprites ##
Sprites are 2D bitmaps.

Due to an optimization issues, all sprites we want to render are located in an **atlas** texture.

Sprites are drawn using the texture regions, which specify the portion of the atlas bitmap ( in pixels ) the sprite is located in.


## Atlas ##
An atlas is a large texture where an artist can place a lot of smaller sprite bitmaps.

**CAUTION** an atlas texture has to have a ^2 ( power of two ) dimensions, and the largest supported dimensions are 1024x1024.

### XML texture definition ###

Here's a sample texture region XML file:
```
<?xml version="1.0" encoding="UTF-8"?>
<TextureRegion atlasName='khaky_birds_prototype/atlas.png' x='0' y='0' w='320' h='480' alphaOp='none'/>
```

It specifies a texture that's contained in atlas `khaky_birds_prototype/atlas.png` and is located in the region (0, 0)->(320, 480), and should be drawn without any alpha test.

### XML render state attrbutes ###

  1. `atlasName` - name of the atlas from which the texture should be aquired
  1. `x`, `y` - upper left corner coordinates ( in pixels )
  1. `w`, `h` - texture width and height ( in pixels )
  1. `alphaOp` - alpha operation to use:
    * `none` - don't perform any alpha operation
    * `test` - perform a simple alpha test
    * `blend` - dop the alpha blending. When this param is specified, you should also specify the `srcAlphaFunc` and `destAlphaFunc` attributes
  1. `srcAlphaFunc`, `destAlphaFunc` - alpha blending functions for the source and destination textures
    * `AF_Zero` - don't use the texture
    * `AF_One` - use texture color and alpha
    * `AF_Src_Alpha` - use source texture alpha
    * `AF_Dest_Alpha` - use destination texture alpha
    * `AF_Src_Color` - use source texture color
    * `AF_Dest_Color` - use destination texture color
    * `AF_One_Minus_Src_Alpha` - use invertex source texture alpha
    * `AF_One_Minus_Dest_Alpha` - use invertex destination texture alpha
    * `AF_One_Minus_Src_Color` - use invertex source texture color
    * `AF_One_Minus_Dest_Color` - use invertex destination texture color
  1. `stencilOp` - stencil buffer operation
    * `SO_None` - don't use stencil buffer
    * `SO_Write_Or` - draw to the stencil buffer, masking the bits that weren't set yet, and leaving the ones already set untouched event if current operation didn't set them
    * `SO_Test` - test against the stencil buffer contents and draw to the back buffer only where the stencil buffer bits are set.

## Animation ##
An animation is a resource. You can create them manually, or by using a definition stored in an XML file.

### XML animation resource ###
To instantiate an animation resource, do this:
```
Animation myAnimation = resourcesManager.getResource( Animation.class, "path_to_my_anim/my_anim.xml" );
```

The XML file with it could look like so:
```
<?xml version="1.0" encoding="UTF-8"?>
<Animation frameDuration='0.1' looped='true' atlasName='khaky_birds_prototype/atlas.png' alphaOp='test'>
        <TextureRegion x='0', y='0' w='100' h='100'/>
</Animation>
```

The file must contain a single `<Animation>` tag with an attribute `frameDuration` specifying the duration of a single animation frame. It may also contain regular **render state tags** ( such as `atlasName` or `alphaOp` ) which are described in more detail [here](#XML_render_state_attrbutes.md).

You can also specify whether the animation should be played in a looped mode or not by adding a **looped** attribute to the `Animation` node.

That tag has children - `<Frame>` tags, each of which describes a region in a texture where an animation frame is located.
The order of the tags specifies the order in which the key frames will be played.

There are various types of frames:

  * `StaticRegion` - corresponds to a single 'texture' frame. With it you just define a static region in an atlas that will be used as an animation frame:

```
<Frame type="StaticRegion" x='396' y='120' w='65' h='119' />
```

  * `LinearInterpolation` - this type of frame lets you define two static regions in an atlas, and make the animation create a number of frames that move you from one region to another. You specify the start frame in a 'From' child node that contains regular texture region coordinates, and the end frame in a 'To' child node. There's one thing left you need to specify - how long should it take (in seconds) for the animation to move from the source region to the target one. You can specify that time in 'duration' parameter

```
<Frame type="LinearInterpolation" duration='0.1'>
   <From x='0' y='0' w='50' h='50'/>
   <To x='100' y='0' w='50' h='50'/>
</Frame>
```

  * `FramesChain` - You will often place multiple animations frames next to each other in a single atlas. You could describe that sequence using multiple `StaticRegion` frames, however the longer an XML file, the longer it takes to load it. That's where this frame type comes in  handy. It assumes each animation frame has the same size ( you need to specify  using `w` and `h` attributes ) and that particular frames lie adjacent to each other, organized in multiple rows. You need to specify the number of frames in each row ( `numPerRow` attribute ) as well as how many frames are there in total ( `numFrames` attribute ). The last pair of attributes specifies the top left coordinate of the first frame ( `x`, `y` attributes ) as well as the horizontal and vertical spacing between subsequent frames (`vSpacing`, `hSpacing` attributes)

```
<Frame type="FramesChain" x='0' y='0' vSpacing='1' hSpacing='1' 
       numPerRow='10' numFrames='20' w='65' h='119' />
```

## Animation events ##

Each `TextureRegion` tag can contain a number of `<Event>` tags. Those allow you to define **animation events**.

You can define an event like so:
```
<Frame type='StaticRegion' x='761' y='235' w='31' h='31'>
	<Event type="Fire"/>
</Frame>
```

This particular frame of animation will emit an `EntityEvent` of type `Fire`.

In order for an entity to be able to receive those events, you need to register them with it - and here's a little surprise - you only need to register event `AnimEvent` like so:
```
m_entity.registerEvent( AnimEvent.class, new EventFactory< AnimEvent >() { @Override public AnimEvent createObject() { return new AnimEvent (); } } );
```

and from that point on you can receive any event you wish.

However - you also need to tell the deserializer that if it encounters this type of event, it should use a specific class for it.
You do it like so:
```
Animation.registerAnimEvent( Fire.class, new AnimEventFactory() { @Override public EntityEvent create() { return new Fire(); } } );
```

The method is static and tells the `Animation` resource that if it encounters a type matching the event name, it should create an instance like that.

Here's the method's signature:
```
/**
 * Registers a new type of event the animations can send to entities.
 * 
 * @param type
 * @param factory
 */
public static void registerAnimEvent( Class< ? extends EntityEvent > type, AnimEventFactory factory )
```

### Playing animation ###
A resource can't be played by itself. In order to be played, we need to memorize the time of the animation somewhere, and since an animation is a resource, shared between many interested visuals that want to display it, it doesn't fit to keep such a runtime info in a static by nature instance.

That's why the engine offers another tool - `AnimationPlayer`, which allows you to play and mix multiple animations.

First of all, you need to define which animations you want to be able to play. You can do that using this method:
```
/**
 * Adds a new animation definition to the player.
 * 
 * @param animation
 * @return              animation index
 */
public int addAnimation( Animation animation );
```

and here's how you can go about it:
```
// load animations
Animation regularAnimation = resMgr.getResource( Animation.class, "khaky_birds_prototype/pedestrianWalking.xml");
Animation wipeShitOffAnimation = resMgr.getResource( Animation.class, "khaky_birds_prototype/pedestrianWipeShitOff.xml");
                
// create an animation player
m_animationPlayer = new AnimationPlayer();
int ANIM_WALK = m_animationPlayer.addAnimation( regularAnimation );
int ANIM_WIPE_SHIT_OFF = m_animationPlayer.addAnimation( wipeShitOffAnimation );
```

So first you load the animations themselves, then you add them to the player. The player returns an ID for each of the added animations, which you'll need in order to switch to playing the specified animation.

You can select an animation you want to play using this ID and the following method of the `AnimationPlayer`:
```
/**
 * Selects an active animation.
 * 
 * @param idx   animation index
 */
public void select( int idx );
```

An animation player returns a `TextureRegion` you should draw your sprite with. So here's how you animate your sprite:
```
batcher.drawSprite( pos.m_x, pos.m_y, bs.getWidth(), bs.getHeight(), m_animationPlayer.getTextureRegion( deltaTime ) ); 
```

where `deltaTime` is the time that's elapsed since the last time you called that method.

The `getTextureRegion` method's signature is:
```
/**
 * Returns a texture region you should draw your geometry with in this animation frame. 
 * 
 * @param deltaTime
 * @return
 */
public TextureRegion getTextureRegion( float deltaTime );
```

## Sprite batcher ##
A device used to draw sprites on the screen.
It uses two methods:

```
/**
 * Draws a sprite with the texture defined for the batch.
 * 
 * @param x				sprite center X coordinate
 * @param y				sprite center Y coordinate
 * @param width			desired sprite width
 * @param height		desired sprite height
 * @param region		texture region to draw the sprite with
 */
public void drawSprite( float x, float y, float width, float height, TextureRegion region );

/**
 * Draws a rotated sprite with the texture defined for the batch.
 * 
 * @param x				sprite center X coordinate
 * @param y				sprite center Y coordinate
 * @param width			desired sprite width
 * @param height		desired sprite height
 * @param angle			rotation angle in degrees
 * @param region		texture region to draw the sprite with
 */
public void drawSprite( float x, float y, float width, float height, float angle, TextureRegion region );

```

The latter allows to draw rotated sprites.
Both allow to specify the size of the sprite, so we can scale the images to our hearts contents.

## Entity visual ##

Entity's visual aspects are represented using an `EntityVisual` abstract class.
The class contains a method:

```
/**
 * Draw self.
 * 
 * @param batcher
 */
public abstract void draw( SpriteBatcher batcher );
```

Override this method in your custom implementation to get access to the `SpriteBatcher` instance which you can use to render your objects.

The class should contain applicable `TextureRegion` or `Animation` fields that describe the graphics features you want to draw.


**CAUTION**: The renderer currently uses just a single atlas texture, so you can't specify which texture to select.

## Visuals factory ##
You need to set up the `Renderer2DView` instance so that it knows what visual representations it should create for the specific entities.

You achieve that by registering a factory.

Here's a sample registration:

```
m_rendererView.register( Bird.class , new EntityVisualFactory() { @Override public EntityVisual instantiate( Entity parentEntity ) { return new BirdVisual( parentEntity ); } } );
```

The **register** method takes a class of an entity as its first argument, and the factory that should be used to create the visual representation as the second one.

The factory itself is an interface with a single method **instantiate**:
```
public interface EntityVisualFactory extends ObjectFactory< Entity, EntityVisual >
{
        /**
         * The factory method.
         * 
         * @param parentEntity
         * @return
         */
        EntityVisual instantiate( Entity parentEntity );
}
```

Implement it as you see above in order to create a representation you desire.

The factory concept this view uses is described in section [Generic factory](GenericFactory.md).