# HUD #

## Introduction ##
A HUD allows you to define a set of controls visible controls and display widgets ( such as buttons, animations, images ) in order to establish a simple user interface.

HUD is divided into a **LAYOUT** and **WIDGETS DEFINITION**.

A layout describes what widgets create a HUD and how are they located in the viewport.

Widgets definition describes how particular widgets look like.

At this moment we have the following widgets at your disposal:
  * `AnimationWidget` - for displaying animation
  * `ButtonWidget` - a control that can be clicked
  * `CounterWidget` - allows to specify and change a number
  * `FrameWidget` - a storage place for other widgets
  * `ImageWidget` - displays a static image

## Widgets Definition ##

The definition is specified in an XML file.
You need to embrace it in the `<HUD>` tag, and then place
the definition of the visual templates.

Each template allows you to define a `class`, the name of which you'll later on be using in the layout to specify how particular widget should look like.

Here are the available template definitions:

  * `DefaultFrameVisualTemplate`  - draws a frame which consists of 9 elements - 8 for the border, and 1 for the interior. The edge elements and the interior ( `center` ) element are scaled when the frame is resized, so mind that when you create graphics for it.

Here's the definition itself:
```
<Template type="DefaultFrameVisualTemplate" class="MyFrame" elemSize='5' alphaOp='test' atlasName='hud/hudAtlas.png'>
	<TopLeftCorner x='0' y='0' w='5' h='5'/>
	<TopEdge x='5' y='0' w='5' h='5'/>
	<TopRightCorner x='10' y='0' w='5' h='5'/>
	<LeftEdge x='0' y='5' w='5' h='5'/>
	<Center x='5' y='5' w='5' h='5'/>
	<RightEdge x='10' y='5' w='5' h='5'/>
	<BottomLeftCorner x='0' y='10' w='5' h='5'/>
	<BottomEdge x='5' y='10' w='5' h='5'/>
	<BottomRightCorner x='10' y='10' w='5' h='5'/>
</Template>
```

As you can see, it specifies 9 **texture regions** that share common render settings, the attributes of which are embedded in the `<Template>` tag itself.

This type requires you to specify `type="DefaultFrameVisualTemplate"` attribute.

Mind to specify the `class` attribute and name this visual.

  * `CustomFrameVisualTemplate` - same as above, only this time the entire frame is drawn using a single bitmap. Here's how you can define it:

```
<Template type="CustomFrameVisualTemplate" class="PrettyFrame" alphaOp='test' atlasName='hud/hudAtlas.png' x='30' y='0' w='218' h='50'/>
```

As simple as that - both the `render state` and the `texture region` coordinates are specified as attributes of the `<Template>` tag.

  * `DefaultButtonVisualTemplate` - a template that draws a frame with a text in it. Since it's mainly ineded for buttons, it has to provide for three states a button can be in: `Pressed`, `Highlighted` and `Released`.

The definition is an extended version of the `DefaultFrameVisualTemplate`, and includes the font specification and the region specifications for the three states:

```
<Template type="DefaultButtonVisualTemplate" class="MyButton" fontPath="font.xml" elemSize='5' alphaOp='test' atlasName='hud/hudAtlas.png'>
	<TopLeftCorner x='15' y='0' w='5' h='5'/>
	<TopEdge x='20' y='0' w='5' h='5'/>
	<TopRightCorner x='25' y='0' w='5' h='5'/>
	<LeftEdge x='15' y='5' w='5' h='5'/>
	<Center x='20' y='5' w='5' h='5'/>
	<RightEdge x='25' y='5' w='5' h='5'/>
	<BottomLeftCorner x='15' y='10' w='5' h='5'/>
	<BottomEdge x='20' y='10' w='5' h='5'/>
	<BottomRightCorner x='25' y='10' w='5' h='5'/>
</Template>
```

  * `ImageButtonVisualTemplate` - customized version of the `DefaultButtonVisualTemplate`, one that uses a prepared bitmap instead of frame elements. `path` attribute contains the path to a texture asset.

```
<Template type="ImageButtonVisualTemplate" class="PrettyButton" fontPath="font.xml" borderSize='5' path="hud/menu/gameSelectionButton.xml" />
```

  * `AnimatedButtonVisualTemplate` - customized version of the `DefaultButtonVisualTemplate`, one that uses an animation to display the button.

```
<Template type="AnimatedButtonVisualTemplate" class="MyAnimatedButton" path="hud/hudAtlas.xml" />
```

  * `CounterVisualTemplate` - a visual of a counter widget - a widget composed of a frame in which a number is displayed, and two control buttons for incrementing and decrementing the value display by the frame's side.

```
<Template type="CounterVisualTemplate" class="MyCounter" fontPath="font.xml" buttonWidth='5' buttonHeight='10'>
	<Frame elemSize='5' alphaOp='test' atlasName='hud/hudAtlas.png'>
		<TopLeftCorner x='0' y='0' w='5' h='5'/>
		<TopEdge x='5' y='0' w='5' h='5'/>
		<TopRightCorner x='10' y='0' w='5' h='5'/>
		<LeftEdge x='0' y='5' w='5' h='5'/>
		<Center x='5' y='5' w='5' h='5'/>
		<RightEdge x='10' y='5' w='5' h='5'/>
		<BottomLeftCorner x='0' y='10' w='5' h='5'/>
		<BottomEdge x='5' y='10' w='5' h='5'/>
		<BottomRightCorner x='10' y='10' w='5' h='5'/>
	</Frame>
		
	<IncreaseButton fontPath="font.xml" borderSize='5' path="hud/increaseButton.xml" />
	<DecreaseButton fontPath="font.xml" borderSize='5' path="hud/decreaseButton.xml" />
</Template>
```

It requires to specify the looks of a frame and the buttons. The visual types of these widgets are hardcoded at the time, so you're bound to use a `DefaultFrameVisualTemplate` and two `ImageButtonVisualTemplate` ( at least for now ) - but how you define them, that's up to you.

  * `CustomCheckboxVisualTemplate` - a template for defining the looks of checkboxes. Its definition is very similar to that of an image-based button, the difference is that you need to define two such button looks here - one for the 'checked', and the other for the 'unchecked' state:

```
<Template type="CustomCheckboxVisualTemplate" class="SoundCheckbox" alphaOp='test' atlasName='hud/menu/mainMenuAtlas.png' >
		<Checked x='799' y='233' w='97' h='95' />
		<Unchecked x='898' y='233' w='97' h='95' />
	</Template>
```

Here instead of specifying a texture asset, you can specify the atlass, texture mode and the coordinates directly in the template definition ( **NOTE** let's see which one is more convenientu - using external assets, or specifying that info directly in the template definition )

  * `ImageVisualTemplate` - a template for displaying images

```
<Template type="ImageVisualTemplate" class="MyImage" />
```


  * `AnimationVisualTemplate` - a template for displaying animations

```
<Template type="AnimationVisualTemplate" class="MyAnimation" />
```

## Creating a HUD renderer ##

Once you have an XML with a hud definition, you can instantiate it as a HUD resource, which later on you'll be able to specify to the renderer to tell it how it should draw layouts.

Here's a snippet that sets up a renderer using such a HUD definition:

```
// load the HUD
Hud hud = m_resourceManager.getResource( Hud.class, "hud/hudDefinition.xml" );
if ( hud == null )
{
	throw new RuntimeException( "No HUD definition" );
}
		
HudRenderer hudRenderer = new HudRenderer( game, hud );
```

Once you have a renderer, you need to call its `draw` method each time the `Screen` the hud is in is presented. In order to do that, find the your `Screen.present` method and place a call to the `HudRenderer.draw` method there:

```
@Override
public void present( float deltaTime ) 
{			
	// draw other stuff...

	// draw the hud contents on to of everything else ( the very last call )
	m_hudRenderer.draw( deltaTime );
}
```


## Layout Definition ##

The layout tells what a particular HUD is composed of.
The definition should be enclosed in the `<Layout>` tags and is composed of several `<Widget>` tags that describe the types of the widgets, their attributes, locations and - last but not least - the visual classes telling how they should be displayed.

That's right - those are the classes you defined in the HUD definition XML.

The basic construction of the `<Widget>` tag is:
```
<Widget type="WidgetType" visualClass="VisualClassFromDefinitionXML" id="WidgetID" width='0.8' height='0.1' >
	<pos x='0.5' y='0.1' z='0' />
</Widget>
```

Of course, each type contains a set of additional attributes that can be specified - we'll get to those in a sec.

### Positions and sizes ###

One more thing - about the positions and sizes. When it came to the HUD definition, all coordinates were expressed in **pixels**. It made sense since we were operating in the context of atlases.

It's different when it comes to layouts. Layout will be displayed on the device's screen, and we know we're dealing with all kinds of resolutions and screen sizes.
What we want to achieve is to have a single HUD definition that works with all resolutions. That's why we can't work in pixels, since those will change from one device to another.

Instead, we're specifying them in the range (0, 1):
  * OX - 0 is on the left, 1 is on the right
  * OY - 0 is at the top, 1 is at the bottom

### Widgets ###

You have the following widgets at your disposal:

  * `FrameWidget` - a basic container that can contain other widgets.
```
<Widget type="FrameWidget" visualClass="MyFrame" width='1' height='1'>
	<pos x='0' y='0' z='0' />
</Widget>
```

You can embed other widgets inside this one.

  * `ButtonWidget` - a button

```
<Widget type="ButtonWidget" visualClass="MyButton" id="Button1" width='0.8' height='0.1' caption="Exit" >
	<pos x='0.5' y='0.1' z='0' />
</Widget>
```

In order to receive messages when a button is clicked, you need to attach a `ButtonListener` instance to the `HudLayout` instance. Just remember to detach it once the layout is no longer used - a layout is a resource and can be kept around afterwards.

```
/**
 * Attaches a button listener.
 * 
 * @param listener
 */
public void attachButtonListener( ButtonListener listener );
	
/**
 * Detaches a button listener.
 * 
 * @param listener
 */
public void detachButtonListener( ButtonListener listener );
```

`caption` attribute describes a text that should be displayed on the button. The text will be trimmed to fit into the button.

  * `CheckboxWidget` - a checkbox

```
<Widget type="CheckboxWidget" visualClass="SoundCheckbox" id="Toggle Sound" width='0.202' height='0.119' >
	<pos x='0.554' y='0.606' z='0' />
</Widget>
```

A registered button listener will also be invoked whenever the checkbox is hit an changes its state ( **see** `ButtonWidget` descripiton on attaching a button listener )


  * `ImageWidget` - displays images.

```
<Widget type="ImageWidget" visualClass="MyImage" path="textures/crapHitParticle.xml" width='0.1' height='0.1' >
		<pos x='0.1' y='0.3' z='0' />
	</Widget>
```

`path` attribute desribes a path to a [texture](Renderer2DReference#XML_texture_definition.md) resource that should be displayed.

  * `AnimationWidget` - displays animations

```
<Widget type="AnimationWidget" visualClass="MyAnimation" path="animations/perkPedestrian/walking.xml" width='0.1' height='0.1' >
	<pos x='0.1' y='0.5' z='0' />
</Widget>
```

`path` attribute desribes a path to an [animation](Renderer2DReference#XML_animation_resource.md) resource that should be displayed.

  * `CounterWidget` - a counter widget.

```
<Widget type="CounterWidget" visualClass="MyCounter" id="SomeCounter" width='0.3' height='0.1' min='0' max='10' >
	<pos x='0.5' y='0.1' z='0' />
</Widget>
```

`min` and `max` atributes describe the range in which the counter will allow to change its value.

In order to access the value from your application, you can call its `getValue` method:

```
/**
 * Returns the current counter value.
 * 
 * @return
 */
public int getValue();
```

You can also dynamically change the range by calling the `setLimits` method:

```
/**
 * Sets new limits in the counter.
 * 
 * @param min
 * @param max
 */
public void setLimits( int min, int max );
```

But in order to do any of those, you need to be able to [access a layout control of your choice](HUD#Accessing_layout_widgets.md).

## Accessing layout widgets ##

That's simple - just call `HudLayout.geWidget` method, specifying the type of the widget and the ID you assigned it in the layout definition XML:

```
/**
 * Looks for a widget with the specified ID in the current layout.
 * 
 * @param type
 * @param id
 * @return
 */
public < T extends HudWidget > T getWidget( Class< T > type, String id );
```

Here's an example:
```
CounterWidget counter = m_hudLayout.getWidget( CounterWidget.class, "LevelIdx" );
```