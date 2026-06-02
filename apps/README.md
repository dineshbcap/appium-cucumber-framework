# Demo Apps

Place your test APK / IPA files in this directory.

## Android — ApiDemos

Download the official Appium demo APK:

```bash
curl -L https://github.com/appium/appium/raw/master/packages/appium/sample-code/apps/ApiDemos-debug.apk \
     -o apps/ApiDemos-debug.apk
```

Or build it from source:
- Repo: https://github.com/appium/android-apidemos

## iOS — UIKitCatalog

Download the sample app from Apple or build UIKitCatalog from Xcode sample projects.
Place the `.app` or `.ipa` in this directory and update `ios.app` in `config.properties`.

## Updating config.properties

```properties
# Android
android.app=apps/ApiDemos-debug.apk
android.appPackage=io.appium.android.apis
android.appActivity=io.appium.android.apis.ApiDemos

# iOS
ios.app=apps/UIKitCatalog.app
ios.bundleId=com.example.apple-samplecode.UIKitCatalog
```
