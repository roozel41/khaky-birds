# Workspace setup #

## Required SDKs & Eclipse ##

1. Download the latest Eclipse

2. Download the latest [Java JDK](http://www.oracle.com/technetwork/java/javase/downloads/jdk-7u1-download-513651.html)

3. Setup an environment variable JDK\_HOME to point to your JDK install directory

4. Download and install [Android SDK](http://dl.google.com/android/installer_r15-windows.exe) (if it asks you to install a Java JDK, simply press the 'Back' button and proceed again )

5. Startup your Eclipse - setup your workspace directory etc. Keep in mind that this will be the directory where all your project files will be stored, so choose wisely so you can find it afterwards in case of an Eclipse failure ;)

## ADT Eclipse plugins ##

6. Open menu **Help** / **Install New Software**.

7. Paste this address: `https://dl-ssl.google.com/android/eclipse` into the field visible on the screen and select all found options:

![http://khaky-birds.eclipselabs.org.codespot.com/files/adt_plugin.jpg](http://khaky-birds.eclipselabs.org.codespot.com/files/adt_plugin.jpg)

8. Press **Next>** and proceed with accepting all agreements and whatnot the installer's gonna present you with.

9. Restart Eclipse.

## Subversive Eclipse plugins ##

10. Again, open menu **Help** / **Install New Software**.

11. Here, the address for the site's gonna differ from one Eclipse version to another. Basically what you need to do is expand the **Work with** dropdown list - there you'll have a few links listed, and one of them's gonna be named just as your Eclipse version.
The version I'm using at the time of writing this manual is called **Idigo**, so just follow the pattern.

12. Type in`subversive` into the **filter** text area. You should see something similar to this:

![http://khaky-birds.eclipselabs.org.codespot.com/files/subversive.jpg](http://khaky-birds.eclipselabs.org.codespot.com/files/subversive.jpg)

13. Once again select all listed packages with a name that contains **subversive**, and press **Next>**

14. Restart Eclipse.

15. At this point you'll be asked to select an **SVN Connector**. Select **SVNKit 1.3.5** or something similar.