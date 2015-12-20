# How to create a game #

We have a special tool for that - `GLGame` and `Screen` classes.

## Introduction ##
Think of a game as of a program composed of several screens - there's a MainMenu screen, an Options screen, and the game screen on which you actually play.

The `Game` interface is an access point to the engine's major subsystems - like the input manager, the graphics system, the sound system etc.

The game runs a screen instance. It's the screen that gets updated and drawn at each frame.

You should put all you game management code inside the screen.

Only one screen can run at a time.

To tell the game which screen is the startup screen, you simply override the `GLGame.getStartScreen` method.

Here's how your game implementation might look like:
```
public class MyGame extends GLGame 
{
	@Override
	public Screen getStartScreen()
	{
		return new MainMenuScreen( this );
	}
}

```

This game will display the main menu screen when it starts. You have to create the `MainMenuScreen` class that extends the `Screen` class though.

## How to change the current screen ##

The `GLGame` class has a handy method called **setScreen**. When called, it will stop the current screen, release all of its data, and start running the screen you specified.

So when should you call this method and where?
That's simple - you can call it on the GLGame instance only, and abiding the **Hollywood principle**, the best place to call it is actually from your `Screen` implementation itself.

This makes the screens into a finite state machine, where the transitions between the subsequent instances are managed by the screens themselves, and the game is the state manager.

## Screens ##

A screen is where you want to do the game stuff. Basically you should provide all the stuff relevant to a part of your game there.

If it's a main menu, then draw some background and buttons and implement responses to the button clicks.

If it's a game screen, create a world, hook it up with some views and fill it with entities.

See [.md](.md) to learn how a [Screen](ScreenReference.md) should be implemented and what does particular methods provide you with.

## Creating a world ##

## Setting up a renderer view ##

## Setting up a controllers view ##

## Setting up a physics view ##