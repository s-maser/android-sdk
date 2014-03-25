#Relayr-Android SDK - Developer's documentation

##Sortware required
- Eclipse (*Lastversion revised: 4.2.2*)
- Eclipse ADT plugin: http://developer.android.com/tools/sdk/eclipse-adt.html

##Setup
- Clone the *android-sdk-code* repository: https://github.com/relayr/android-sdk-code.git
- Clone the *android-sdk* repository: https://github.com/relayr/android-sdk.git
- Import both projects into Eclipse: File -> Import -> Existing Android code into workspace

##Debug
Relayr-Android SDK is divided in two Android libraries: *android-sdk-code* and *android-sdk*. The first one is used 
to store all the classes involved in the SDK funtionalities. The second one is the Android library which is given to the
costumers with the SDK classes included as a jar file.

New classes have to be included in the android-sdk-code. To generate the library jar:
- Select the project
- Click on File (at the top menu) 
- Export 
- JAR file
- Select just the 'src' folder of the *android-sdk-code* project to export. Define the destination path and click on finish
- Include the jar file in the livs folder of the *android-sdk project*.

Check the *Relayr-Android SDK - User's documentation* to know how to add the *android-sdk* library into an Android app.

##Test
SDK tests are located on the test folder of https://github.com/relayr/android-test.git. The tests are jUnit4 files. To run all the test you need to checkout the sdk test environment at https://github.com/relayr/android-sdk-test-environment.git.

Check the link to know how to run jUnit4
test on Eclipse: http://javarevisited.blogspot.nl/2013/03/how-to-write-unit-test-in-java-eclipse-netbeans-example-run.html.

##Release
- Export the *android-sdk-code* project to a jar file.
- Copy the android-sdk-code.jar file to the libs directory of the *android-sdk* library.
- Compress the *android-sdk* library (into a zip file for example).
- Create a new realese at https://github.com/relayr/android-sdk and create a link to download the zip file.
