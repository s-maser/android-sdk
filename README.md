#BETA Relayr-Android SDK - Developer's documentation

relayr is all about bringing things to life, allowing app developers access to the physical world around them.

The Android SDK enables app developers to easily interact with the relayr platform.


##Setup

- Create an Android Project [See Thermometer demo](https://github.com/relayr/android-demo-apps/commit/3e33f01c7e693e5ee0f9884dea8218731b8c9c8b)
- Create an empty Git repository or reinitialize an existing one:
    ```
    git init
    ```
- Add the android relayr sdk as a git submodule: [Add the Android Relayr Sdk](https://github.com/relayr/android-demo-apps/commit/f2c17c6a9a20f0c0e1f12cf8c38c2afd5ed4449d)
    ```
    git submodule add https://github.com/relayr/android-sdk.git libraries/relayr-sdk
    ```
- Reference the relayr sdk project in your settings.gradle file: 
    ```
    include ':app' ':libraries:relayr-sdk:android-sdk'
    ```
- Reference the relayr sdk project in your build.gradle inside your app folder:
    ```
    dependencies {
        compile project(':libraries:relayr-sdk:android-sdk')
    }
    ```
- [Create a relayr app](https://developer.relayr.io/dashboard/apps/myApps) and [save the relayrsdk.properties file inside src/main/assets](https://github.com/relayr/android-demo-apps/commit/06b85d467fdf6300367d6d997a0f89fc3b9a184c) 
- Subclass an Android Application inside your app and [initialize the Sdk](https://github.com/relayr/android-demo-apps/commit/27bef2e3c588c0e2351294a7fdc6418240af4bd4)
    ```
    public class MyApplication extends Application {
        @Override
        public void onCreate() {
            super.onCreate();
            RelayrSdk.initSdk(this);
        }
    }
    ```
- Reference your Application and add internet permission in the Android Manifest
    ```
    <uses-permission android:name="android.permission.INTERNET" />
    <application
        android:allowBackup="true"
        android:icon="@drawable/ic_launcher"
        android:label="@string/app_name"
        android:theme="@style/AppTheme"
        android:name=".MyApplication">
    ```
- [Log the user into the app](https://github.com/relayr/android-demo-apps/commit/19bf3578de9fd2c20e2ebab50c5a280500d411c9) and start using the sdk!
    ```
    RelayrSdk.logIn(this, this);
    ```
    
##Examples

Take a look at our [Android Demos](https://github.com/relayr/android-demo-apps) to get started.