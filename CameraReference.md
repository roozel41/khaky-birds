# Camera #

Camera lets you view a an area of the game world.
Because at this moment the engine supporst only 2D rendering, there's only one type of camera available - `Camera2D`.


What a camera can see is defined by the following paramteres:
  * **m\_position**  - position of the camera in the world space
  * **m\_zoom**      - camera zoom. It's a factor in range ( 0, inf ), so be sure not to set it to anything <= 0.  Values <1 will enlarge the viewed fragment, allowing to see its central part better. Values >1 will decrease the size of the viewed objects, allowing to see a larger part of the world.
  * **m\_frustumWidth**, **m\_frustumHeight** - size of the view frustum. Sets how wide and high is the viewport area. It also defines camera's **aspect ratio** - `m_frustumWidth / m_frustumHeight`.


By changing the values of those parameers, you can control what camera sees ( m\_position, m\_zoom ), and how it presents it ( m\_frustumWidth, m\_frustumHeight )