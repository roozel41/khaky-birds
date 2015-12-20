# How to create a new functional test #

To create the test:

  * Open the `BubblyTest` project
  * Open the functional tests package located in **src\com.hypefoundry.bubbly.test.tests**
  * find a package that corresponds to your test's category or create a new one
  * create a class with the test. The class should at some level extend from `android.app.Activity`. Basically you want to extend directly from that if you're testing some Android features, or extend from the `com.hypefoundry.engine.impl.game.GLGame` if you want to test some engine features.

Time to register the new test:
  * Open the `BubblyTest` project
  * Open the functional tests package located in **src\com.hypefoundry.bubbly.test**
  * open the class `BubblyTestMain` - on the very top you'll find a member called **m\_testClassNames**
  * add your test class name along with the package it's located in. i.e, If you created a class called `MyFunctionalTest` and placed it in the package **com.hypefoundry.bubbly.test.tests.my\_functional\_tests**, then after adding the test to the array, it should look like so:

```
/// Add your test classes here
private final String m_testClassNames[] = 
{ 
	"my_functional_tests.MyFunctionalTest"
	, "prototypes.khaky_birds.KhakyBirds"
	, "openGL.GLSurfaceViewTest"
	, "apiBasics.SurfaceViewTest"
	, "apiBasics.FontTest"
	, "apiBasics.BitmapTest"
	, "apiBasics.ShapeTest"
	, "apiBasics.SoundPoolTest"
	, "apiBasics.ExternalStorageTest"
	, "apiBasics.AssetsTest"
	, "apiBasics.AccelerometerTest"
	, "apiBasics.KeyTest"
	, "apiBasics.SingleTouchTest"
	, "misc.LifeCycleTest"
};
```

  * One last thing - we need to register the class in the Android manifest XML file - it's located in the main directory of the `BubblyTest` project and is called `AndroidManifest.xml` - open it and from the bottom tab select the `AndroidManifest.xml` view
  * find a sequence of `<activity ...>` tags in the `<application>` tag premisses and add a new one - it should look like so:

```
<activity android:label="My functional tests"
	android:name=".tests.my_functional_tests.MyFunctionalTest"
			android:screenOrientation="portrait" />
```

As you can see, the `android:label` field contains a simple description - put anything you want here.

The `android:name` field contains the package & class name - it differs from the name we added to the **m\_testClassNames** array only with being prefixed with the **tests.** package name.

Enjoy!