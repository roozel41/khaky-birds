# How to draw your entities on the screen #

You need to use one of the supplied renderers ( or write your own ).
At the moment the only available renderer is the Renderer2D.

This is a view that you can attach to the world. It instantiates implementations of `EntityVisual` class.

[Create such implementations](Renderer2DReference#Entity_visual.md) and [register them](Renderer2DReference#Visuals_factory.md) with the renderer in order to have your enitites properly displayed.