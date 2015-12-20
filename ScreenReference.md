# Screen #

## Introduction ##
A screen is where you put your game piece logic - be it the main menu or the actual game screen itself.


The screen is where you place your **rendering calls** as well as where you can hook up the things you want to **update** each frame.

But don't worry - you won't be forced to write the facilites responsible for that yourself - the engine is already equipped with a [renderer](Renderer2DReference.md), a [controllers view](ControllersViewReference.md) that will instantiate AI behaviors for you and many other facilites.

## Sample implementation ##

Here's a sample implementation with comments as to where to put your stuff:

```

class MyScreen extends Screen
{
	public MyScreen( Game game )
	{
		super( game );

		// Create the screen subsystems ( like world, the renderer etc. ) here
	}

	@Override
	public abstract void resume()
	{
		// Create the resources the screen uses here, restart the renderer etc.
	}

	@Override
	public void present(float deltaTime)
	{
		// Render the screen here
	}
	
	@Override
	public abstract void pause()
	{
		// Release the resources you allocated in the `resume` method here
	}
	
	@Override
	public abstract void dispose()
	{
		// Cleanup after your screen here - destroy what you created in the constructor.
	}
}

```

## I have things I want to update each frame ##

Seems as though a perfect place for calling them would be the **present** method - it receives a deltaTime value and is called periodically. The thing is it's actually NOT the place for that.

A screen has another facility - two methods that allow you to define objects that should be updated periodically.

Such an object needs to implement the `Updateable` interface, that provides its implementation with an `update( float deltaTime )` method - just what the doctor's ordered.

You need to call the following `Screen` methods to add the objects to the update loop:

```
/**
 * Adds a new updatable object.
 * 
 * @param updatable
 */
void addUpdatable( Updatable updatable );
	
/**
 * Removes an updatable object.
 * 
 * @param updatable
 */
void removeUpdatable( Updatable updatable );
```