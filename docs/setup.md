# Setup & Build Guide

## Prerequisites

- **Java Development Kit (JDK)**: version 17 or higher.
- **Android Studio**: Ladybug (2024.2.1) or higher recommended.
- **Android SDK**: API Level 36 (compileSdk) and API Level 26 (minSdk).

## Initial Setup

1.  **Clone the Repository**:
    ```bash
    git clone https://github.com/Rahul-Bhatt03/Kotlin_Task.git
    ```
2.  **Open in Android Studio**:
    - Launch Android Studio.
    - Select **Open** and navigate to the `Kotlin_Task` folder.
3.  **Sync Gradle**:
    - Android Studio should automatically prompt for a Gradle sync. If not, go to **File > Sync Project with Gradle Files**.

## Building & Running

### Run the App
- Connect an Android device (API 26+) or launch an Emulator.
- Click the **Run** button (green play icon) in the toolbar or press `Shift + F10`.

### Run Unit Tests
- Open the **Terminal** tab in Android Studio.
- Run the following command:
    ```bash
    ./gradlew test
    ```
- Or right-click the `app/src/test` folder and select **Run 'Tests in...'**.

## Generating the APK

1.  Go to **Build > Build Bundle(s) / APK(s) > Build APK(s)**.
2.  Once finished, a notification will appear. Click **locate** to find the debug APK in `app/build/outputs/apk/debug/app-debug.apk`.
3.  Alternatively, run via CLI:
    ```bash
    ./gradlew assembleDebug
    ```
