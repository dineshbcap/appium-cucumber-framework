# Appium Cucumber Framework

Mobile test automation framework using **Appium 2 + Java 17 + Cucumber 7 + TestNG**, covering Android and iOS from the same codebase, with parallel execution support.

## Controls & Capabilities Covered

| Area | Feature File | Page Object |
|------|-------------|-------------|
| Button (tap / long-press / double-tap) | `button.feature` | `ButtonControlPage` |
| Text Input (type / clear / password / multiline) | `text_input.feature` | `TextInputControlPage` |
| Checkbox | `checkbox.feature` | `CheckboxControlPage` |
| Radio Button | `radio_button.feature` | `RadioButtonControlPage` |
| Dropdown / Spinner | `dropdown.feature` | `DropdownControlPage` |
| Slider / SeekBar | `slider.feature` | `SliderControlPage` |
| Scroll / Swipe | `scroll.feature` | `ScrollControlPage` |
| Alert / Dialog | `alert.feature` | `AlertControlPage` |
| Date & Time Picker | `date_picker.feature` | `DatePickerControlPage` |
| Gestures (swipe/pinch/zoom/drag/mobile commands) | `gesture.feature` | `GestureControlPage` |
| Switch / Toggle | `switch.feature` | `SwitchControlPage` |
| List / RecyclerView | `list.feature` | `ListControlPage` |
| WebView (hybrid context switching) | `web_view.feature` | `WebViewControlPage` |
| Keyboard interaction (visibility, key events, Done action) | `keyboard.feature` | `KeyboardPage` |
| Clipboard (copy/paste, set/get) | `clipboard.feature` | `ClipboardPage` |
| App Lifecycle (background/foreground/terminate/relaunch/state) | `app_lifecycle.feature` | `AppLifecyclePage` |
| Biometric Authentication (Touch ID / Face ID / Fingerprint) | `biometric.feature` | `BiometricPage` |
| Runtime Permissions | `permissions.feature` | `PermissionPage` |
| Deep Linking | `deep_link.feature` | `DeepLinkPage` |
| Device Orientation | `orientation.feature` | `OrientationPage` |
| Locator Strategies (accessibility id, XPath, predicate, class chain, UiSelector, relative locators) | `locator_strategies.feature` | `LocatorStrategyPage` |
| Cloud Execution (BrowserStack / Sauce Labs / LambdaTest) | `cloud_execution.feature` | — (capability-driven, see `DriverFactory`) |

## Project Structure

```
appium-cucumber-framework/
├── pom.xml
├── testng-parallel.xml          ← parallel run config
├── testng.xml                   ← single/smoke run config
├── apps/
│   ├── README.md                ← how to obtain the demo APK / iOS app
│   ├── ApiDemos-debug.apk       ← Android fixture app
│   └── UIKitCatalog.app         ← iOS fixture app (simulator build)
└── src/
    ├── main/java/com/appium/framework/
    │   ├── config/ConfigReader.java
    │   ├── driver/
    │   │   ├── DriverManager.java   ← ThreadLocal driver
    │   │   └── DriverFactory.java   ← Android + iOS capability/session creation
    │   ├── pages/
    │   │   ├── BasePage.java
    │   │   └── controls/            ← 21 page objects (dual @AndroidFindBy/@iOSXCUITFindBy)
    │   └── utils/                   ← Wait, Gesture, Mobile Gesture, Keyboard, Clipboard,
    │                                   App, Device, Scroll, Screenshot, Recording, File Transfer
    └── test/
        ├── java/com/appium/tests/
        │   ├── hooks/Hooks.java
        │   ├── runners/
        │   │   ├── ParallelRunner.java
        │   │   ├── SingleRunner.java
        │   │   ├── RetryAnalyzer.java
        │   │   └── RetryAnnotationTransformer.java
        │   └── stepdefs/            ← 23 step definition files
        └── resources/
            ├── features/            ← 22 feature files
            ├── config.properties
            ├── log4j2.xml
            ├── allure.properties
            ├── extent.properties
            └── extent-config.xml
```

## Prerequisites

| Tool | Version |
|------|---------|
| Java | 17 |
| Maven | 3.8+ |
| Node.js | 18+ |
| Appium | 2.x (`npm i -g appium`) |
| UiAutomator2 driver | `appium driver install uiautomator2` |
| XCUITest driver (iOS) | `appium driver install xcuitest` |
| Android SDK / Emulator | API 30+ |
| Xcode + iOS Simulator (macOS only) | Matching the `ios.platformVersion` you target |

## Setup

### 1. Install Appium 2 and drivers

```bash
npm install -g appium
appium driver install uiautomator2   # Android
appium driver install xcuitest        # iOS (macOS only)
```

### 2. Get the demo apps

```bash
# Android — ApiDemos
curl -L https://github.com/appium/appium/raw/master/packages/appium/sample-code/apps/ApiDemos-debug.apk \
     -o apps/ApiDemos-debug.apk

# iOS — UIKitCatalog (build from Apple's sample project for the Simulator,
# then drop the resulting UIKitCatalog.app into apps/)
```

See `apps/README.md` for details.

### 3. Configure devices

Edit `src/test/resources/config.properties`:

```properties
# Supported: android | ios
platform=android

# ─── Android ───
android.udid=emulator-5554          # run: adb devices
android.platformVersion=16
android.app=apps/ApiDemos-debug.apk
android.appPackage=io.appium.android.apis
android.appActivity=io.appium.android.apis.ApiDemos

# ─── iOS ───
ios.udid=<simulator-udid>           # run: xcrun simctl list devices
ios.platformVersion=18.4
ios.deviceName=iPhone 16 Pro Max
ios.app=apps/UIKitCatalog.app
ios.bundleId=com.example.apple-samplecode.UICatalog
```

`platform` can also be overridden per-run without editing the file: `-Dplatform=ios`.

### 4. Start Appium server

```bash
appium --port 4723
```

### 5. Start the device

```bash
# Android
emulator -avd Pixel_6_API_33 &

# iOS — boot the simulator UDID configured above
xcrun simctl boot <simulator-udid>
open -a Simulator
```

## Running Tests

### Parallel execution (default — 4 threads, Android)

```bash
mvn clean test
```

> The parallel runner dispatches scenarios across N threads against **one configured device**. For iOS, use the single runner below instead — running 4 parallel sessions against a single simulator UDID will cause session collisions.

### Change thread count

```bash
mvn clean test -Dparallel.thread.count=2
```

### Single device / smoke only (required for iOS)

```bash
# Android
mvn clean test -Psingle

# iOS
mvn clean test -Psingle -Dplatform=ios -Dcucumber.filter.tags="not @androidOnly and not @cloud"
```

### Run by tag

```bash
mvn clean test -Dcucumber.filter.tags="@smoke"
mvn clean test -Dcucumber.filter.tags="@button or @checkbox"
mvn clean test -Dcucumber.filter.tags="@gesture and not @dragDrop"
```

Platform-scoping tags used throughout the feature files:

| Tag | Meaning |
|-----|---------|
| `@androidOnly` | Scenario only applies to / only runs on Android |
| `@iosOnly` | Scenario is written for iOS (may still carry `@androidOnly` if currently disabled pending a fixture screen — see [Outstanding Gaps](#outstanding-gaps--platform-limitations)) |
| `@crossPlatform` | Runs on both platforms unmodified |
| `@cloud` | Only runs against BrowserStack/Sauce Labs/LambdaTest, activated explicitly |

A full iOS run always needs `-Dcucumber.filter.tags="not @androidOnly and not @cloud"` (or a narrower tag expression) to skip scenarios that don't apply.

### Run a specific feature

```bash
mvn clean test -Dcucumber.features="src/test/resources/features/button.feature"
```

### Run against the cloud (BrowserStack / Sauce Labs / LambdaTest)

```bash
mvn clean test -Dcucumber.filter.tags="@cloud" -Dcloud.provider=browserstack \
    -Dcloud.username=$BS_USER -Dcloud.access.key=$BS_KEY -Dcloud.app.url=bs://<app-id>
```

## Reports

After a test run, open:

- **Allure Report**: `allure serve target/allure-results` (or `mvn allure:report` → `target/site/allure-maven-plugin/`)
- **Extent HTML Report**: `target/extent-reports/SparkReport.html`
- **Cucumber HTML Report**: `target/cucumber-reports/cucumber.html` (parallel) / `single-run.html` (single)
- **Cucumber JSON**: `target/cucumber-reports/cucumber.json` / `single-run.json`
- **Logs**: `target/logs/appium-tests.log`
- **Screenshots** (on failure): `target/screenshots/`

## Parallel Execution Architecture

```
TestNG DataProvider (parallel=true)
        │
        ├─ Thread-1 ─► Scenario A ─► DriverManager(ThreadLocal) ─► Device/Emulator-1
        ├─ Thread-2 ─► Scenario B ─► DriverManager(ThreadLocal) ─► Device/Emulator-2
        ├─ Thread-3 ─► Scenario C ─► DriverManager(ThreadLocal) ─► Device/Emulator-3
        └─ Thread-4 ─► Scenario D ─► DriverManager(ThreadLocal) ─► Device/Emulator-4
```

Each scenario gets its own `AppiumDriver` instance via `InheritableThreadLocal`.
`Hooks.java` creates and destroys the driver in `@Before` / `@After`.
Failed scenarios retry once automatically (`test.retry.count` in `config.properties`, wired via `RetryAnnotationTransformer`).

## Adding a New Control

1. Create a page object in `src/main/java/.../pages/controls/` — use dual `@AndroidFindBy` / `@iOSXCUITFindBy` annotations where the same locator strategy works on both platforms, or branch on `isAndroid()`/`isIOS()` (see `BasePage`) where the underlying app screens differ.
2. Create step definitions in `src/test/java/.../stepdefs/`
3. Add a `Given "the X screen is displayed"` step in `CommonStepDefs`
4. Write a feature file in `src/test/resources/features/`
5. Tag scenarios `@androidOnly` / `@iosOnly` / `@crossPlatform` as appropriate, and verify against **both** fixture apps before considering the control done — a locator that "compiles" under `@iOSXCUITFindBy` isn't the same as one verified against the real accessibility tree (see the WDA element-cache gotcha documented in `AppLifecyclePage`/`AppLifecycleStepDefs` as a cautionary example).

## Outstanding Gaps / Platform Limitations

These are the scenarios/capabilities that could **not** be exercised against the current fixture apps (`ApiDemos-debug.apk` for Android, `UIKitCatalog.app` for iOS), and why — everything else in the feature files runs against real, verified app behavior.

### Android

| Capability | Reason |
|---|---|
| Biometric, Permissions (6 scenarios) | No such screen exists in this APK build |
| 2 fictional Deep Link URIs | ApiDemos never registered handlers for them |
| RelativeLocator (3 scenarios) | UiAutomator2 driver doesn't implement this Selenium locator strategy |
| WebView content interaction (4 scenarios) | Needs a matching ChromeDriver for Chrome 149.0.7827, not installed/auto-downloadable here |
| Double tap gesture | No real target anywhere in the app produces any observable, verifiable effect |
| Standalone long press (isolated from drag-and-drop) | Same — no real target with a verifiable result |
| Pinch-to-zoom (W3C and `mobile:` commands) | No pinch-zoomable view found anywhere in the app |
| `mobile: doubleClickGesture`, `longClickGesture`, `pinchOpenGesture`, `pinchCloseGesture` | Same root cause as above — no real target |
| Horizontal swipe / scroll-left-right | Real app behavior: this list has no horizontal scroll; a horizontal drag registers as a tap and navigates away instead |
| Multi-line text entry | Confirmed: no such field exists in this app build |
| IME "Done"/"Search" submitting a form with visible result | No screen has both a submit-reactive field and a result label |

### iOS

**True platform limitations (no iOS equivalent exists):**

| Capability | Reason |
|---|---|
| Android hardware key events — Tab, Enter, Volume Up/Down (`keyboard.feature`) | iOS has no hardware-key-press API; only Backspace was portable (`sendKeys(Keys.BACK_SPACE)` workaround, implemented) |
| `mobile: flingGesture` (`gesture.feature`) | Physics-based inertial fling has no XCUITest/W3C Actions equivalent |
| Android `currentActivity` (`app_lifecycle.feature`) | Activity/package model doesn't exist on iOS |
| ADB-based permission grant/revoke (`permissions.feature`) | `adb shell pm grant/revoke` has no iOS analogue (no ADB, no shell) |
| UiAutomator2 selector strategies (`locator_strategies.feature`) | `new UiSelector()...` is Android-native; iOS's equivalent (predicate string / class chain) is already covered separately |

**Blocked by the UIKitCatalog fixture app, not by Appium/iOS itself** — real Appium capabilities that work fine on iOS, just untestable against this particular sample app:

| Capability | Reason |
|---|---|
| Biometric auth — Touch ID / Face ID (`biometric.feature`) | UIKitCatalog has no screen that invokes `LocalAuthentication`. All 5 iOS-tagged scenarios exist but are also tagged `@androidOnly` deliberately, i.e. currently disabled pending a fixture app |
| iOS deep linking (`deep_link.feature`) | UIKitCatalog's `Info.plist` registers no `CFBundleURLTypes`, so there's no real `uikit://` scheme to open |
| iOS system permission alerts (`permissions.feature`) | No screen requests camera/location/photos, so no real permission alert ever fires |
| Copy via UI button (`clipboard.feature`) | No in-app Copy button exists (paste-via-UI is covered instead) |
| Scroll-to-text with a known anchor (`scroll.feature`) | `"WebView3"` is an ApiDemos-only string; no iOS equivalent list exists (index-based scroll-to-bottom is covered instead) |
| Drag-and-drop gesture (`gesture.feature`) | Only exists in Appium's separate `TestApp` fixture (not currently bundled in `apps/`) |

**Portable, just not re-implemented yet:**

| Capability | Reason |
|---|---|
| Selenium 4 Relative Locators (`locator_strategies.feature`) | `.below()`/`.near()`/chained anchors work identically on iOS via the W3C protocol; the 3 scenarios are tagged `@androidOnly` only because they hardcode ApiDemos anchor labels ("Text"/"Views"). Swapping in UIKitCatalog anchors (e.g. "Alert Views"/"Buttons") would make these run on iOS with no code changes — lowest-effort item to close |

**Bottom line:** everything genuinely coverable on iOS with the current `UIKitCatalog.app` fixture (71 scenarios) passes. Closing the biometric/deep-link/permission gaps on either platform requires swapping in (or adding a custom) fixture app that actually exercises those APIs.
