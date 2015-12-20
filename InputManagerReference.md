# Input manager #

Input manager allows you to query for the user input.

There are several ways the user can interact with the phone:
  * touching the screen
  * typing something on the keyboard
  * tilting it

## Touchable screen input ##

You can check if the user's input by querying the `Input.getTouchEvents` method.

```
/**
 * Returns a list of all touch-related events that happened.
 * 
 * @return
 */
public List<TouchEvent> getTouchEvents();
```

`Input.TouchEvent` class describes what kind of event transpired:
  * **type**:
    * `TOUCH_DOWN` - the screen was just pressed
    * `TOUCH_UP` - the finger was pulled away from the screen
    * `TOUCH_DRAGGED` - the finger was pressed and moved across the screen
  * **x**, **y** - position of the finger on the screen in screen coordinates. In order to translate that to the world coordinates, use `Camera.touchToWorld` method.
  * **pointer** - index of the finger touching the screen. Since many devices support multi touch screens, this comes in handy if you want to provide support for them.

Moreover - you can check if the specified pointer ( finger ) is pressed up against the screen at the moment by calling `isTouchDown` method:
```
/**
 * Checks if the specified finger is pressed against the screen.
 * 
 * @param pointer
 * @return
 */
public boolean isTouchDown(int pointer);
```

You can also check **how long has the finger been touching the screen** by calling `getTouchDuriation` method.
```
/**
 * Tells how long has a finger been touching the screen ( in seconds )
 * 
 * @param pointer
 * @return
 */
public float getTouchDuriation( int pointer );
```

You just need to specifiy the pointer index that you wish to check, and that's all. The returned value is expressed in **seconds**.

Keep in mind that the duration is memorized so that you can access it even after the touch was released. It will get reset with the next touch.

If you want to reset it at any time, simply call the `clearTouchDuration` method:
```
/**
 * Clears previously registered duration events.
 */
void clearTouchDuration();
```

## Keyboard input ##

You can check the keyboard input in a similar way. `Input` class has a few methods in store just for that occasion:

```
/**
 * Returns a list of all key-related events that happened.
 * 
 * @return
 */
public List<KeyEvent> getKeyEvents();
```

It also supports events, which are very similar to touch events.
`Input.TouchEvent` class describes what kind of event transpired:
  * **type**:
    * `KEY_DOWN` - key was pressed
    * `KEY_UP` - key was released
  * **keyCode** - kode of the key
  * **keyChar** - character the key represents.

Apart from that, you can check if the specified key is pressed at the moment by calling `isKeyPressed` method and passing the code of the key you're interested in:
```
/**
 * Checks if the specified key is pressed.
 * 
 * @param keyCode
 * @return
 */
public boolean isKeyPressed(int keyCode);
```

## Accelerometers ##
An accelerometer informs you how tilted the phone is with respect to one of 3 axes of our real-world reference coordinate system.

You can query those values by calling the following methods that will give you information about respective acceleration values:

```
/**
 * Returns the X axis factor of the accelerometer.
 *  
 * @return
 */
public float getAccelX();
	
/**
 * Returns the Y axis factor of the accelerometer.
 *  
 * @return
 */
public float getAccelY();

/**
 * Returns the Z axis factor of the accelerometer.
 *  
 * @return
 */
public float getAccelZ();
```