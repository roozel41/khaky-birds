# How to make your entities do stuff? #

Actually - you don't want them to do just any stuff. You want them to do your bidding - meaning you want to **control** them.

And that's what the [controllers view](ControllersViewReference.md) is all about.

It allows you to register `EntityControllers` for your entities that will take care of guiding their steps during the game.

Suffice to say, you have to extend the `EntityController` class, [register it](ControllersViewReference#Controllers_factory.md) with the `ControllersView` instance, and off you go.

## FSM ##
[Finite state machines](FSM.md) give you more flexibility in terms of how you control an agent in particular situation.
If you want to create more sophisticated AI - I suggest yoou get familiar with it.