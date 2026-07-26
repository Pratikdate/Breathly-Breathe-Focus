# 🧘‍♂️ Breathly - Deep Breathing & Focus

[![Google Play](https://img.shields.io/badge/Google_Play-Download_App-green?style=for-the-badge&logo=googleplay&logoColor=white)](https://play.google.com/store/apps/details?id=com.shanacoder.breathly)
[![Open Source](https://img.shields.io/badge/Open_Source-%E2%9D%A4-brightgreen?style=for-the-badge)](#)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9%2B-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![License](https://img.shields.io/badge/License-MIT-blue.svg?style=for-the-badge)](#license)

**Breathly** is a beautiful, minimalist, and 100% open-source Android app designed to help you reduce stress, increase focus, improve sleep quality, and practice mindfulness through guided breathing exercises.

Whether you need a quick 2-minute reset during work, a deep relaxation routine before bed, or a way to test your lung capacity, Breathly provides visual, audio, and haptic guidance for every session.

---

## ✨ Features

- 🧘 **Guided Breathing Exercises**: Pre-configured, science-backed breathing routines for instant calm, focus, and sleep support.
- 🛠️ **Custom Routine Builder**: Create, save, and favorite your own breathing patterns by tailoring inhale, hold, exhale, and hold durations alongside custom cycle counts and accent colors.
- 🫁 **Breath Holding Test**: Measure your breath retention time and track your personal best and average performance over time.
- 📊 **Progress & Analytics**:
  - Detailed daily, weekly, monthly, and yearly progress charts.
  - Session statistics including total sessions, total breathing duration, and active daily streaks.
- 🖐️ **Customizable Home & Progress Layout**: Drag-and-drop reorderable card layouts tailored to your routine preferences.
- 🔔 **Audio & Haptic Guidance**: Gentle phase transition cues and tactile vibration feedback so you can breathe with your eyes closed.
- 🔒 **100% Offline & Privacy First**: Zero tracking, zero telemetry, no ad networks, and no user sign-in. All data is saved securely on your local device.
- 🎨 **Modern Material 3 Design**: Crafted with Jetpack Compose featuring ambient color themes and fluid animations.

---

## 📱 Included Breathing Techniques

| Technique | Phase Pattern (Inhale - Hold - Exhale - Hold) | Best For |
| :--- | :---: | :--- |
| **Equal Breathing** *(Sama Vritti)* | `4s - 0s - 4s - 0s` | Quick relaxation, daily focus, and stress reduction |
| **Box Breathing** *(Four-Square)* | `4s - 4s - 4s - 4s` | Deep stress relief, mental clarity, and tactical focus |
| **4-7-8 Breathing** *(Relaxing Breath)* | `4s - 7s - 8s - 0s` | Anxiety relief, falling asleep faster, and calming the nervous system |
| **Breath Holding Test** | Custom / Retention | Tracking lung capacity and breath control progress |

---

## 📲 Download

Get the official release directly on the Google Play Store:

<a href="https://play.google.com/store/apps/details?id=com.shanacoder.breathly">
  <img alt="Get it on Google Play" src="https://play.google.com/intl/en_us/badges/static/images/badges/en_badge_get_it_on.png" width="220"/>
</a>

---

## 🛠️ Architecture & Tech Stack

Breathly is built following modern Android development best practices and recommended architecture patterns:

- **Language**: [Kotlin](https://kotlinlang.org/)
- **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose) with Material Design 3
- **Architecture Pattern**: MVVM (Model-View-ViewModel) + Unidirectional Data Flow (UDF)
- **Asynchronous & Reactive**: Kotlin Coroutines & `StateFlow` / `Flow`
- **Local Persistence**:
  - [Room Database](https://developer.android.com/training/data-storage/room) (for sessions history and custom breathing routines)
  - [Jetpack DataStore](https://developer.android.com/topic/libraries/architecture/datastore) (for app settings and user preferences)
- **Dependency Processing**: KSP (Kotlin Symbol Processing)
- **In-App Reviews**: Google Play Review API

---

## 🚀 Building from Source

### Prerequisites
- **Android Studio**: Jellyfish (2024.1.1) or newer recommended.
- **JDK**: Java 17
- **Android SDK**: API level 34 compile SDK (minimum SDK 24 - Android 7.0+).

### Steps

1. **Clone the repository**:
   ```bash
   git clone https://github.com/Pratikdate/Breathly-Breathe-Focus.git
   cd Breathly-Breathe-Focus
   ```

2. **Open in Android Studio**:
   - Launch Android Studio and choose **Open an Existing Project**.
   - Select the cloned root directory.

3. **Build the Debug APK**:
   - Connect your Android device or start an emulator.
   - Run the debug build via command line:
     ```bash
     ./gradlew assembleDebug
     ```
   - Or click **Run ('app')** directly in Android Studio.

---

## 🛡️ Privacy Policy

Breathly respects your privacy.
- **No Data Collection**: We do not collect, transmit, or share any personal information.
- **Local Storage Only**: All your breathing logs, streaks, and custom settings remain 100% on your device inside local SQLite storage via Room.

---

## 🤝 Contributing

Contributions, issues, and feature requests are welcome!
Feel free to check the issues page or submit a pull request:

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git checkout -b feature/AmazingFeature`)
5. Open a Pull Request

---

## 📄 License

This project is open-source and available under the [MIT License](LICENSE).

---

<p align="center">Made with ❤️ for mindfulness and well-being.</p>
