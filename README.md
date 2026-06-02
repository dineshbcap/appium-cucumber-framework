# Appium Cucumber Framework

Mobile test automation framework using **Appium 2 + Java 11 + Cucumber 7 + TestNG** with parallel execution support.

## Controls Covered

| Control | Feature File | Page Object |
|---------|-------------|-------------|
| Button (tap / long-press / double-tap) | `button.feature` | `ButtonControlPage` |
| Text Input (type / clear / password / multiline) | `text_input.feature` | `TextInputControlPage` |
| Checkbox | `checkbox.feature` | `CheckboxControlPage` |
| Radio Button | `radio_button.feature` | `RadioButtonControlPage` |
| Dropdown / Spinner | `dropdown.feature` | `DropdownControlPage` |
| Slider / SeekBar | `slider.feature` | `SliderControlPage` |
| Scroll / Swipe | `scroll.feature` | `ScrollControlPage` |
| Alert / Dialog | `alert.feature` | `AlertControlPage` |
| Date & Time Picker | `date_picker.feature` | `DatePickerControlPage` |
| Gestures (pinch/zoom/drag) | `gesture.feature` | `GestureControlPage` |
| Switch / Toggle | `switch.feature` | `SwitchControlPage` |
| List / RecyclerView | `list.feature` | `ListControlPage` |
| WebView (hybrid) | `web_view.feature` | `WebViewControlPage` |

## Project Structure

```
appium-cucumber-framework/
├── pom.xml
├── testng-parallel.xml          ← parallel run config
├── testng.xml                   ← single/smoke run config
├── apps/
│   └── README.md                ← how to download demo APKs
└── src/
    ├── main/java/com/appium/framework/
    │   ├── config/ConfigReader.java
    │   ├── driver/
    │   │   ├── DriverManager.java   ← ThreadLocal driver
    │   │   └── DriverFactory.java   ← Android + iOS creation
    │   ├── pages/
    │   │   ├── BasePage.java
    │   │   └── controls/            ← 13 page objects
    │   └── utils/
    │       ├── WaitUtils.java
    │       ├── GestureUtils.java
    │       └── ScreenshotUtils.java
    └── test/
        ├── java/com/appium/tests/
        │   ├── hooks/Hooks.java
        │   ├── runners/
        │   │   ├── ParallelRunner.java
        │   │   └── SingleRunner.java
        │   └── stepdefs/            ← 14 step definition files
        └── resources/
            ├── features/            ← 13 feature files
            ├── config.properties
            ├── log4j2.xml
            ├── extent.properties
            └── extent-config.xml
```

## Prerequisites

| Tool | Version |
|------|---------|
| Java | 11+ |
| Maven | 3.8+ |
| Node.js | 18+ |
| Appium | 2.x (`npm i -g appium`) |
| UiAutomator2 driver | `appium driver install uiautomator2` |
| XCUITest driver (iOS) | `appium driver install xcuitest` |
| Android SDK / Emulator | API 30+ |

## Setup

### 1. Install Appium 2 and drivers

```bash
npm install -g appium
appium driver install uiautomator2   # Android
appium driver install xcuitest        # iOS (macOS only)
```

### 2. Download demo app

```bash
curl -L https://github.com/appium/appium/raw/master/packages/appium/sample-code/apps/ApiDemos-debug.apk \
     -o apps/ApiDemos-debug.apk
```

### 3. Configure devices

Edit `src/test/resources/config.properties`:

```properties
platform=android
android.udid=emulator-5554          # run: adb devices
android.platformVersion=13.0
```

### 4. Start Appium server

```bash
appium --port 4723
```

### 5. Start emulator

```bash
emulator -avd Pixel_6_API_33 &
```

## Running Tests

### Parallel execution (default — 4 threads)

```bash
mvn clean test
```

### Change thread count

```bash
mvn clean test -Dparallel.thread.count=2
```

### Single device / smoke only

```bash
mvn clean test -Psingle
```

### Run by tag

```bash
mvn clean test -Dcucumber.filter.tags="@smoke"
mvn clean test -Dcucumber.filter.tags="@button or @checkbox"
mvn clean test -Dcucumber.filter.tags="@gesture and not @dragDrop"
```

### Run a specific feature

```bash
mvn clean test -Dcucumber.features="src/test/resources/features/button.feature"
```

## Reports

After a test run, open:

- **Extent HTML Report**: `target/extent-reports/SparkReport.html`
- **Cucumber HTML Report**: `target/cucumber-reports/cucumber.html`
- **Cucumber JSON**: `target/cucumber-reports/cucumber.json`
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

## Adding a New Control

1. Create a page object in `src/main/java/.../pages/controls/`
2. Create step definitions in `src/test/java/.../stepdefs/`
3. Add a `Given "the X screen is displayed"` step in `CommonStepDefs`
4. Write a feature file in `src/test/resources/features/`
