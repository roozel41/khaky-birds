# Finite state machine #

A finite state machine helps you define more sophisticated AI for your agents.
If you want an agent to be going safely home, and start running as soon as he notices a danger - this is a tool for you.

## Introduction ##
`FiniteStateMachine` class is a specialization of an `EntityController`, so if you want your agent to be controlled by it, simply extend your controller class from it instead of an `EntityController` class:

```
class MyCoolAI extends FiniteStateMachine // and not EntityConroller
{
     // ...
}
```

## Introduction to states ##
FSM allows you to express your agent behavior in terms of states.

Think of the agent as of a living being for a while - he may wake up, then have a breakfast, go to work, pretend to do stuff when his boss is around and sit idly as no one's watching him, and then get back home.

At each moment, the agent is in some state:
  * at first he's `Asleep`
  * as soon as his **restedness** value reaches a certain level, he wakes up and transitions to the `Hungry` state
  * He looks for food, eats it, and once he's not hungry any more, he can transition to `Idle` state
  * In `Idle` state, he finds for something to do etc.

Of course this is a gross oversimplification - actually each of this states is an FSM in its own, however you get the point.


The point is - each state defines a set of actions an agent should execute as long as he stays in this state, and external or internal events can make the agent **transition** to a different state, in which he'll execute different actions.

This simple scheme provides an ability for an agent to **change** the actions it's executing in response to some external conditions - and thus it appears to react to its environment.

## State ##
A state is a class - you need to extend `FSMState` class to define your own.

In case of small FSMs, you can simply define the states inside your FSM controller, however for more sophisticated controllers, use diferent files.

`FSMState` class gives you a few methods you should use:

```
@Override
public void activate()
{
   // put the code related to the state activation here
}

@Override
public void deactivate()
{
   // put the code related to the state deactivation here
}

@Override
public void execute( float deltaTime )
{
   // put the code that should be executed each frame when the state is active
}
```

Those three methods are called during the state's lifecycle: **activate** is called when a state becomes active.
Then its **execute** method will be called periodically, and once it transitions to a different state, its **deactivate** method will get called.

You can change the state by calling the `transitionTo` method and passing it the state class name you want to transition to.
```
transitionTo( Idle.class );
```

There can be **only one active state at a time**, and a `FiniteStateMachine` makes sure that constraint is fulfilled.

## Registering states ##
You need to register your states with your `FiniteStateMachine` instance before you can use them.

You do that using two methods:
```
/**
 * Registers a new state for the machine to operate on.
 *   
 * @param newState
 */
public final void register( FSMState newState ); 

/**
 * Defines an entry point to the machine - a state the machine 
 * should start processing first.
 * 
 * @param 	state
 * @return 	state instance that will be activated 
 */
public final < T extends FSMState  > T begin( Class< T > state );
```

**register** adds instances of your states to the FSM.

**begin** method on the other hand tells which state the FSM should start working with first. The state needs to be registered before you can call it.

It's typically done in the FSM's constructor:

```
public class MyFsmAI extends FiniteStateMachine
{
	public MyFsmAI()
	{
		// setup the state machine
		register( new Wander() );
		register( new TurnAround() );
		register( new Observe() );
		begin( Wander.class );
	}
}
```


## Configuring destination state ##
Both **begin** and **transitionTo** methods return an instance of the state you're about to transition to, so you can simply call any methods on it or set its member variables in order to customize how it should work.

It may come in handy if you want to run away from some specific threat you noticed in the current state - and the running away will be realized by the target state.

## Events ##
You can define transitions in response to events comming in from outside.

Each entity is an event transponder - you can send it events, and other parts of your game can listen to those events.

Let's put it to use for our FSM. Let's say that if our agent is in `Idle` state, and something frightens him, he should transition to `Scared` state and start running away.

Here's how we'd implement that:
```
class Idle extends FSMState implements EntityEventListener
{
	// ...
	@Override
	public void onEvent( EntityEvent event ) 
	{
		if ( event instanceof ScaryThingNoticed )
		{
			transitionTo( Scared.class );
		}
	}
}

class Scared extends FSMState implements EntityEventListener
{
	// ...

	@Override
	public void activate()
	{
		m_steeringBeh.begin().seek( m_safePos ).faceMovementDirection();
	}

	// ...
}

```

To learn more how you can set up the events, take a look [here](WorldReference#Entity_events.md)

## Good old updates ##
There's also a way to update something despite of the active state. A good candidate for such updates is a steering behavior instance, which needs to be updated every frame.

To use it, simply override it:
```
@Override
public void onUpdate( float deltaTime )
{
	// your update code goes here
}
```