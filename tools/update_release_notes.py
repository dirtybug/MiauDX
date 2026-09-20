import os
import sys
from datetime import datetime

def main():
    version = os.environ.get('VERSION', 'development')
    app_version = os.environ.get('APP_VERSION_NAME', '1.06')
    app_code = os.environ.get('APP_VERSION_CODE', '10006')
    today = datetime.now().strftime('%Y-%m-%d')

    entry = f"""## [{version}] - {today}

### 📱 Google Play Store Metadata
- **Version Name (`versionName`):** `{app_version}`
- **Version Code (`versionCode`):** `{app_code}`
- **Application ID:** `com.Runner.CQMiau`
- **Target SDK:** Android 16 (API 36) / **Min SDK:** Android 8.0 (API 26)

### 🧪 Tests & Quality
- **Hermetic Build Environment:** Docker (OpenJDK 17 + Android SDK 36)

### 📥 Release Deliverables
| Artifact | File | Description |
| :--- | :--- | :--- |
| **AAB Bundle** | [`MiauCQ-{version}.aab`](https://github.com/dirtybug/MiauDX/releases/tag/{version}) | Signed production Android App Bundle for Google Play Store |
| **APK Release** | [`MiauCQ-{version}.apk`](https://github.com/dirtybug/MiauDX/releases/tag/{version}) | Signed production Android APK for direct device installation |
| **Test Reports** | [`MiauCQ-{version}-test-reports.zip`](https://github.com/dirtybug/MiauDX/releases/tag/{version}) | Test execution reports |

---

"""
    releases_path = 'RELEASES.md'
    if os.path.exists(releases_path):
        with open(releases_path, 'r', encoding='utf-8') as f:
            content = f.read()
        marker = '<!-- RELEASES_LIST_START -->\n'
        if marker in content and f'## [{version}]' not in content:
            idx = content.index(marker) + len(marker)
            new_content = content[:idx] + entry + content[idx:]
            with open(releases_path, 'w', encoding='utf-8') as f:
                f.write(new_content)
            print(f"Added {version} to {releases_path}")
        else:
            print(f"Skipping {version}, already present or marker missing.")

if __name__ == '__main__':
    main()
