# 📦 MiauDX / Miau CQ - Official Releases

This document maintains the official release history of **MiauDX (Miau CQ)**, including Google Play Store metadata, release deliverables, and compliance tracking.

Official release assets can also be directly downloaded from the [GitHub Releases](https://github.com/dirtybug/MiauDX/releases) page.

---

<!-- RELEASES_LIST_START -->
## [v1.0.7] - 2026-09-20

### 📱 Google Play Store Metadata
- **Version Name (`versionName`):** `1.07`
- **Version Code (`versionCode`):** `107` (Next in sequence after 106)
- **Application ID:** `com.Runner.CQMiau`
- **Target SDK:** Android 16 (API 36) / **Min SDK:** Android 8.0 (API 26)

### 🧪 Tests and Quality
- **Hermetic Build Environment:** Docker (OpenJDK 17 + Android SDK 36)
- **Compilation Toolchain:** Android Gradle Plugin 8.13.2 + Gradle 8.13
- **Test Suite:** Unit tests and automated visual frame screenshots

### 📥 Release Deliverables
| Artifact | File | Description |
| :--- | :--- | :--- |
| **AAB Bundle** | [`MiauCQ-v1.0.7.aab`](https://github.com/dirtybug/MiauDX/releases/tag/v1.0.7) | Signed production Android App Bundle for Google Play Store |
| **APK Release** | [`MiauCQ-v1.0.7.apk`](https://github.com/dirtybug/MiauDX/releases/tag/v1.0.7) | Signed production Android APK for direct device installation |
| **Test Reports** | [`MiauCQ-v1.0.7-test-reports.zip`](https://github.com/dirtybug/MiauDX/releases/tag/v1.0.7) | Full unit test execution report |
| **Screenshots** | [`MiauCQ-v1.0.7-screenshots.zip`](https://github.com/dirtybug/MiauDX/releases/tag/v1.0.7) | Visual per-frame screenshots for Google Play listing |

### 🚀 Key Improvements in v1.0.7
- **Target Android 16 (API 36):** Full compliance with Google Play Developer Programme Policies requiring API 35/36.
- **Sequential Version Code `107` (`1.07`):** Direct successor to production release `106 (1.06)` on Google Play Console.
- **Docker Hermetic CI/CD Pipeline:** Reproducible containerized builds with OpenJDK 17 and Android SDK 36.
- **Automated Play Store Signing:** Signed with release keystore and password from repository secrets.

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
