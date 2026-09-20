# 📦 MiauDX / Miau CQ - Official Releases

This document maintains the official release history of **MiauDX (Miau CQ)**, including Google Play Store metadata, release deliverables, and compliance tracking.

Official release assets can also be directly downloaded from the [GitHub Releases](https://github.com/dirtybug/MiauDX/releases) page.

---

<!-- RELEASES_LIST_START -->
## [v1.0.7] - 2026-09-20

### 📱 Google Play Store Metadata
- **Version Name (`versionName`):** `1.07`
- **Version Code (`versionCode`):** `107` (Direct successor to production release 106)
- **Application ID:** `com.Runner.CQMiau`
- **Target SDK:** Android 16 (API 36) / **Min SDK:** Android 8.0 (API 26)

### 🧪 Tests and Quality
- **Hermetic Build Environment:** Docker (OpenJDK 17 + Android SDK 36)
- **Compilation Toolchain:** Android Gradle Plugin 8.13.2 + Gradle 8.13
- **Test Suite:** 100% Passed (Unit Tests & 8 High-Fidelity UI Frame Screenshots)

### 📥 Release Deliverables
| Artifact | File | Description |
| :--- | :--- | :--- |
| **AAB Bundle** | [`MiauCQ-v1.0.7.aab`](https://github.com/dirtybug/MiauDX/releases/tag/v1.0.7) | Signed production Android App Bundle for Google Play Store |
| **APK Release** | [`MiauCQ-v1.0.7.apk`](https://github.com/dirtybug/MiauDX/releases/tag/v1.0.7) | Signed production Android APK for direct device installation |
| **Test Reports** | [`MiauCQ-v1.0.7-test-reports.zip`](https://github.com/dirtybug/MiauDX/releases/tag/v1.0.7) | Full unit test execution report |
| **Screenshots** | [`MiauCQ-v1.0.7-screenshots.zip`](https://github.com/dirtybug/MiauDX/releases/tag/v1.0.7) | Verified visual screenshots for all 8 frames |

### 🚀 Key Improvements in v1.0.7
- **Accurate UI Visual Screenshots (8 Frames):**
  - **Frame 1 (Main DX Cluster):** Top teal buttons `[ Filter Band ]` and `[ View Log ]`, black 150dp terminal log window (`logScrollView`), and live spots list with authentic graphical national flags (`🇵🇹`, `🇺🇸`, `🇯🇵`, `🇩🇪`, `🇬🇧`, `🇧🇷`, `🇪🇸`).
  - **Frame 2 (Lateral / Options Menu):** Overflow menu popup displaying `Open Radio`, `User Settings`, `CQ Mode`, `Donate`, and `Git source code`.
  - **Frame 3 (Band Filter):** HF Band checklist (160m..6m) with checkboxes and bottom teal `[ Exit ]` button.
  - **Frame 4 (View Logs / QSO Logbook):** Station QSO history with flags, `[ Gen.ADIF ]`, `[ Edit ]`, `[ Delete ]`, and bottom `[ Add To Log ]` button.
  - **Frame 5 (COM Port Config):** Full CAT configuration with Baud Rate (38400), Data/Stop Bits, Parity, Radio Model (FT-891), USB Device, and `Refresh` / `Apply` buttons.
  - **Frame 6 (User Settings):** Callsign entry (`CT1BOH`), station locator info, and teal `[ Save ]` button.
  - **Frame 7 (CQ Mode):** Rig control interface with Call, Freq, RST reports, Date/Time picker, and 4 rig control buttons.
  - **Frame 8 (Spot Details):** Spot inspection, QRZ lookup, frequency rig sync, and log book saving.
- **Strict Compliance:** Fully targets Android 16 (API 36) adhering to Google Play Developer Programme Policies.

<hr>

## [v1.0.6] - 2026-09-20

### 📱 Google Play Store Metadata
- **Version Name (`versionName`):** `1.06`
- **Version Code (`versionCode`):** `10006`
- **Application ID:** `com.Runner.CQMiau`
- **Target SDK:** Android 16 (API 36) / **Min SDK:** Android 8.0 (API 26)

### 🧪 Tests and Quality
- **Hermetic Build Environment:** Docker (OpenJDK 17 + Android SDK 36)
- **Compilation Toolchain:** Android Gradle Plugin 8.13.2 + Gradle 8.13

### 📥 Release Deliverables
| Artifact | File | Description |
| :--- | :--- | :--- |
| **AAB Bundle** | [`MiauCQ-v1.0.6.aab`](https://github.com/dirtybug/MiauDX/releases/tag/v1.0.6) | Signed production Android App Bundle for Google Play Store |
| **APK Release** | [`MiauCQ-v1.0.6.apk`](https://github.com/dirtybug/MiauDX/releases/tag/v1.0.6) | Signed production Android APK for direct device installation |
| **Test Reports** | [`MiauCQ-v1.0.6-test-reports.zip`](https://github.com/dirtybug/MiauDX/releases/tag/v1.0.6) | Full unit test execution report |

### 🚀 Key Improvements in v1.0.6
- **Target Android 16 (API 36):** Full compliance with Google Play Developer Programme Policies requiring API 35/36.
- **Docker Hermetic CI/CD Pipeline:** Reproducible containerized builds with OpenJDK 17 and Android SDK 36.
- **Automated Play Store Signing:** GitHub Actions pipeline supporting base64-encoded keystores and automated AAB bundle generation.
<!-- RELEASES_LIST_END -->
