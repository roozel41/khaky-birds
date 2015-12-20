# How to populate your game world? #

You use [entities](WorldReference#Entities_management.md) for that.

Simply extend the `Entity` class, and off you go.
An entity should only describe the state of you world's object and provide methods that allow to change that state. If you want to display it, give it some AI capabilities etc. - `Entity` class is not a good place for that.

Depending on your entity's profile, you may want to display it on the screen or give it some kind of AI, or control it with the user input.

For that you need to use the views.

  * displaying - use the [renderer view](Renderer2DReference.md) for that.
  * AI - you can implement it by giving the entity a [controller](ControllersViewReference.md)
  * want the entity to be notified about collisions with other entities - give it a simple [physical body](PhysicsViewReference#Physical_Body.md)
  * want the entity to bounce off the walls and do other cool physical stuff - implement a decent collision response mechanism using a [physical body](PhysicsViewReference#Physical_Body.md)

If you need some custom things, you may want to come up with a view of your own, create a representation of an entity in that view and control it from there.