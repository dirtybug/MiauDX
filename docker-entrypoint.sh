#!/usr/bin/env bash
set -e

if [ "$ENTRYPOINT_RELOADED" != "1" ] && [ -f /workspace/docker-entrypoint.sh ] && [ "$0" != "/workspace/docker-entrypoint.sh" ]; then
    export ENTRYPOINT_RELOADED=1
    sed -i 's/\r$//' /workspace/docker-entrypoint.sh 2>/dev/null || true
    chmod +x /workspace/docker-entrypoint.sh 2>/dev/null || true
    exec /bin/bash /workspace/docker-entrypoint.sh "$@"
fi

echo "====================================================="
echo "               MiauDX / CQMiau Test Runner            "
echo "====================================================="

VERSION_TAG="${APP_VERSION_NAME:-1.06}"
if [[ "$VERSION_TAG" != v* ]]; then
    VERSION_TAG="v${VERSION_TAG}"
fi
RELEASE_DIR="${RELEASE_DIR:-/workspace/release/development}"
mkdir -p "$RELEASE_DIR/reports/unit-tests"

# Update AndroidManifest.xml with Play Store version if APP_VERSION_NAME is provided
if [ -n "$APP_VERSION_NAME" ] && [ -f /workspace/app/src/main/AndroidManifest.xml ]; then
    CLEAN_VER="${APP_VERSION_NAME#v}"
    sed -i -E "s/android:versionName=\"[^\"]*\"/android:versionName=\"${CLEAN_VER}\"/g" /workspace/app/src/main/AndroidManifest.xml 2>/dev/null || true
    if [ -n "$APP_VERSION_CODE" ]; then
        sed -i -E "s/android:versionCode=\"[^\"]*\"/android:versionCode=\"${APP_VERSION_CODE}\"/g" /workspace/app/src/main/AndroidManifest.xml 2>/dev/null || true
    fi
fi
[ -f /workspace/gradlew ] && chmod +x /workspace/gradlew 2>/dev/null || true
rm -f /root/.gradle/caches/journal-1/*.lock 2>/dev/null || true
rm -f /root/.gradle/caches/*.lock 2>/dev/null || true

ACTION="${1:-unit}"

export GRADLE_OPTS="-Dorg.gradle.daemon=false -Dorg.gradle.jvmargs=\"-Xmx2048m -XX:MaxMetaspaceSize=512m\""

case "$ACTION" in
    dev|shell|bash|sh)
        export GRADLE_OPTS="-Dorg.gradle.daemon=true -Dorg.gradle.jvmargs=\"-Xmx2048m -XX:MaxMetaspaceSize=512m\""
        echo "====================================================="
        echo "        MiauDX - Development Environment             "
        echo "====================================================="
        echo "Java:        $(java -version 2>&1 | head -n 1)"
        echo "Android SDK: $ANDROID_HOME (API 36, Build-Tools 36.0.0)"
        echo "Workspace:   $(pwd)"
        echo "Available quick commands:"
        echo "  test                        -> Run unit tests"
        echo "  build                       -> Compile Debug APK"
        echo "  release                     -> Compile Release APK and AAB"
        echo "  lint                        -> Run Android Lint analysis"
        echo "  ./gradlew test --continuous -> Automatically re-test on edit"
        echo "  exit                        -> Exit container"
        echo "====================================================="
        exec /bin/bash
        ;;

    unit|test)
        echo ">>> Running Unit Tests for MiauDX / com.Runner.CQMiau..."
        ./gradlew test -PversionName="${APP_VERSION_NAME:-1.06}" -PversionCode="${APP_VERSION_CODE:-10006}" --info --stacktrace
        echo ""
        echo ">>> Copying unit test reports to $RELEASE_DIR/reports/unit-tests..."
        if [ -d "app/build/reports/tests/testDebugUnitTest" ]; then
            cp -r app/build/reports/tests/testDebugUnitTest/* "$RELEASE_DIR/reports/unit-tests/"
            [ ! -f "$RELEASE_DIR/reports/index.html" ] && cp "$RELEASE_DIR/reports/unit-tests/index.html" "$RELEASE_DIR/reports/index.html" 2>/dev/null || true
            echo "✓ Unit test report saved to $RELEASE_DIR/reports/unit-tests/index.html"
        fi
        echo "✓ Unit tests completed successfully in $RELEASE_DIR!"
        ;;

    build|assemble)
        echo ">>> Building Debug APK (Version: ${APP_VERSION_NAME:-1.06}, Code: ${APP_VERSION_CODE:-10006})..."
        ./gradlew assembleDebug -PversionName="${APP_VERSION_NAME:-1.06}" -PversionCode="${APP_VERSION_CODE:-10006}" --info
        mkdir -p "$RELEASE_DIR"
        find app/build/outputs/apk/debug -name "*.apk" -exec cp {} "$RELEASE_DIR/MiauCQ-debug.apk" \; 2>/dev/null || true
        cp -f "$RELEASE_DIR/MiauCQ-debug.apk" "$RELEASE_DIR/MiauCQ-development-debug.apk" 2>/dev/null || true
        echo "✓ Debug APK saved to $RELEASE_DIR/MiauCQ-debug.apk"
        ;;

    release)
        echo ">>> Building Release Deliverables (Version: ${APP_VERSION_NAME:-1.06}, Code: ${APP_VERSION_CODE:-10006})..."
        KEY_ALIAS_PARAM=""
        if [ -n "$KEY_ALIAS" ]; then
            KEY_ALIAS_PARAM="-PkeyAlias=$KEY_ALIAS"
        fi
        KEY_PASS_PARAM=""
        if [ -n "$KEYSTORE_PASSWORD" ]; then
            KEY_PASS_PARAM="-PkeystorePassword=$KEYSTORE_PASSWORD"
        fi
        mkdir -p "$RELEASE_DIR"
        ./gradlew assembleRelease bundleRelease -PversionName="${APP_VERSION_NAME:-1.06}" -PversionCode="${APP_VERSION_CODE:-10006}" $KEY_PASS_PARAM $KEY_ALIAS_PARAM --info
        find app/build/outputs/apk/release -name "*.apk" -exec cp {} "$RELEASE_DIR/MiauCQ-release.apk" \; 2>/dev/null || true
        cp -f "$RELEASE_DIR/MiauCQ-release.apk" "$RELEASE_DIR/MiauCQ-development-release.apk" 2>/dev/null || true
        find app/build/outputs/bundle/release -name "*.aab" -exec cp {} "$RELEASE_DIR/MiauCQ-release.aab" \; 2>/dev/null || true
        (cd "$RELEASE_DIR" && sha256sum *.apk *.aab > SHA256SUMS.txt 2>/dev/null || true)
        echo "✓ Release APK saved to $RELEASE_DIR/MiauCQ-release.apk"
        [ -f "$RELEASE_DIR/MiauCQ-release.aab" ] && echo "✓ Release AAB saved to $RELEASE_DIR/MiauCQ-release.aab"
        ;;

    bundle|aab)
        echo ">>> Building Release AAB (Android App Bundle for Google Play Store)..."
        KEY_ALIAS_PARAM=""
        if [ -n "$KEY_ALIAS" ]; then
            KEY_ALIAS_PARAM="-PkeyAlias=$KEY_ALIAS"
        fi
        KEY_PASS_PARAM=""
        if [ -n "$KEYSTORE_PASSWORD" ]; then
            KEY_PASS_PARAM="-PkeystorePassword=$KEYSTORE_PASSWORD"
        fi
        ./gradlew bundleRelease -PversionName="${APP_VERSION_NAME:-1.06}" -PversionCode="${APP_VERSION_CODE:-10006}" $KEY_PASS_PARAM $KEY_ALIAS_PARAM --info
        AAB_FILE=$(find app/build/outputs/bundle/release -name "*.aab" 2>/dev/null | head -n 1)
        if [ -n "$AAB_FILE" ] && [ -f "$AAB_FILE" ]; then
            TARGET_VER="${APP_VERSION_NAME:-1.06}"
            TARGET_VER="${TARGET_VER#v}"
            VERSION_DIR="/workspace/release/v${TARGET_VER}"
            mkdir -p "$VERSION_DIR"
            mkdir -p "/workspace/release/development"
            cp -f "$AAB_FILE" "$VERSION_DIR/MiauCQ-release.aab"
            cp -f "$AAB_FILE" "/workspace/release/development/MiauCQ-release.aab"
            (cd "$VERSION_DIR" && sha256sum MiauCQ-release.aab > MiauCQ-release.aab.sha256 2>/dev/null || true)
            echo "✓ Release AAB saved to $VERSION_DIR/MiauCQ-release.aab"
            echo "✓ Development AAB saved to /workspace/release/development/MiauCQ-release.aab"
        else
            echo "❌ Error: Release AAB file was not generated!"
            exit 1
        fi
        ;;

    lint)
        echo ">>> Running Android Lint..."
        ./gradlew lintDebug || true
        mkdir -p "$RELEASE_DIR/reports/lint"
        if [ -d "app/build/reports" ]; then
            find app/build/reports -name "lint-results*" -exec cp {} "$RELEASE_DIR/reports/lint/" \; 2>/dev/null || true
        fi
        ;;

    all)
        echo ">>> Running Full Build Suite (Unit Tests + Debug Build + Release APK & AAB)..."
        "$0" unit
        "$0" build
        "$0" release
        echo "✓ Full suite finished! All artifacts saved to $RELEASE_DIR/!"
        ;;

    *)
        echo ">>> Executing custom command: $@"
        exec "$@"
        ;;
esac
