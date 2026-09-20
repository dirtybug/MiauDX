# MiauDX – Source of the Miau CQ Android App

![Miau CQ Logo](https://play-lh.googleusercontent.com/Z1_example_logo_url)  
*Android ham radio tool for DX cluster, logbook, and CAT control*

[![MiauDX CI/CD](https://github.com/dirtybug/MiauDX/actions/workflows/ci.yml/badge.svg)](https://github.com/dirtybug/MiauDX/actions/workflows/ci.yml)

---

## 📡 What is Miau CQ?

**Miau CQ** is an Android app designed for amateur radio operators.  
It combines three essential tools in one place:

- **DX Cluster Client** – connect to popular clusters and view real-time DX spots, WWV reports, and announcements.  
- **Logbook** – record your QSOs with callsign, frequency, mode, RST, and timestamp.  
- **CAT Control** – control supported rigs (currently focused on **Yaesu FT-891** and others) via USB CAT interface.  

Play Store: [Miau CQ on Google Play](https://play.google.com/store/apps/details?id=com.Runner.CQMiau)

---

## ✨ Features

- 📡 **Real-time DX spots** with custom filters  
- 📓 **Integrated logbook** (ADIF export planned)  
- 🎛 **CAT rig control** (FT-891 tested, other Yaesu and Icom rigs supported)  
- ⏱ **Frequency management** from cluster to rig in one tap  
- 🌙 **Dark/Light themes** (optional)  
- 🔌 **USB connection support** for CAT cables (via FTDI / CP210x / CH34x USB-to-Serial)  
- 🔔 **Notifications** when a desired callsign or band appears  

---

## 🛠 Technical Details & Google Play Compliance

- **Target SDK:** Android 16 (API level 36) — *Compliant with Google Play Developer Programme Policies*
- **Min SDK:** Android 8.0 (API level 26)
- **Language:** Pure Java for high reliability and low latency
- **Build Toolchain:** Android Gradle Plugin 8.13.2 + Gradle 8.13
- **Containerization:** Hermetic Docker build container (Eclipse Temurin JDK 17 + Android SDK 34 & 36)

---

## 🚀 CI/CD Pipeline & GitHub Secrets

This repository features an automated CI/CD pipeline modeled after **MorseGO**, building signed **APK** and **AAB (Android App Bundle)** deliverables automatically inside Docker on tag pushes and pull requests.

### 🔐 Setting Up GitHub Secrets for Release Signing

To sign release builds for Google Play Store upload, configure these three repository secrets under **Settings > Secrets and variables > Actions**:

| Secret Name | Description | Example |
| :--- | :--- | :--- |
| `KEYSTORE_BASE64` | Base64-encoded string of your release keystore (`.jks` or `.keystore`) | *(generated via bash command below)* |
| `KEYSTORE_PASSWORD` | Keystore password and private key password | `YourStrongKeystorePassword` |
| `KEY_ALIAS` | Key alias in the keystore *(optional, defaults to `key0`)* | `key0` |

#### How to convert your keystore to Base64:
- **On Linux / macOS:**
  ```bash
  base64 -w 0 release.jks > keystore_base64.txt
  ```
- **On Windows PowerShell:**
  ```powershell
  [Convert]::ToBase64String([IO.File]::ReadAllBytes("release.jks")) | Set-Content "keystore_base64.txt"
  ```
Copy the contents of `keystore_base64.txt` directly into the `KEYSTORE_BASE64` secret in GitHub.

---

## 🏷️ How to Release a New Version

When you push a git tag, the CI pipeline automatically:
1. Calculates the `versionName` and `versionCode` (e.g. `v1.0.6` -> `versionName: 1.0.6`, `versionCode: 10006`).
2. Updates `app/src/main/AndroidManifest.xml`.
3. Restores your keystore from `KEYSTORE_BASE64`.
4. Runs unit tests and builds signed `MiauCQ-v1.0.6.apk` and `MiauCQ-v1.0.6.aab`.
5. Publishes a GitHub Release with download assets.
6. Commits the updated metadata and `RELEASES.md` back to repository branch.

```bash
git tag v1.0.6
git push origin v1.0.6
```

---

## 💻 Local Development & Build

### 🐳 Using Docker (Recommended):
- **Run all builds & tests:**
  - Windows: `.\run-docker-tests.bat all`
  - PowerShell: `.\run-docker-tests.ps1 -Target all`
  - Linux: `./run-docker-tests.sh all`
- **Build Release APK & AAB locally:**
  - Windows: `.\run-docker-tests.bat release`
- **Clean workspace:**
  - Windows: `.\clean.bat`

### ☕ Using Local Gradle & Android Studio:
```bash
git clone https://github.com/dirtybug/MiauDX.git
cd MiauDX
./gradlew assembleDebug
./gradlew test
```
