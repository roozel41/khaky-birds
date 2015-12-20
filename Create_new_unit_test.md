# How to create a new unit test #

  * Select the `BubblyTests` project
  * Go to the unit tests package - **unittests\com.hypefoundry.engine.test**
  * Select an applicable existing package for your tests, or create a new one
  * Create a new class, that extends from the `android.test.AndroidTestCase` class
  * fill the class with test cases - each test case is represented by a **public** method the name of which starts with the **test** prefix


```

package com.hypefoundry.engine.test.sandbox;

import android.test.AndroidTestCase;


public class ObjectsBasicTests extends AndroidTestCase 
{

	public void testSimpleObjectInstantiation ()
	{	
		// ......
	}
	
	public void testPrimitiveTypes()
	{
		// ......
	}
}
```