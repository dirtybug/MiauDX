#!/usr/bin/env bash
set -e

echo "===================================================="
echo "            MiauDX - Docker Test Runner             "
echo "===================================================="

TARGET="${1:-all}"

# Initialize and verify Yaesu Radio Simulators (FT-891 & FT-857)
if command -v python3 >/dev/null 2>&1; then
    if [ -f "./tools/ft891_simulator.py" ]; then
        echo "[RADIO-SIM] Starting Yaesu FT-891 Radio Simulator verification..."
        python3 ./tools/ft891_simulator.py --test
        echo "[RADIO-SIM] Yaesu FT-891 Radio Simulator verified and ready!"
    fi
    if [ -f "./tools/ft857_simulator.py" ]; then
        echo "[RADIO-SIM] Starting Yaesu FT-857 Radio Simulator verification..."
        python3 ./tools/ft857_simulator.py --test
        echo "[RADIO-SIM] Yaesu FT-857 Radio Simulator verified and ready!"
    fi
    echo ""
elif command -v python >/dev/null 2>&1; then
    if [ -f "./tools/ft891_simulator.py" ]; then
        echo "[RADIO-SIM] Starting Yaesu FT-891 Radio Simulator verification..."
        python ./tools/ft891_simulator.py --test
        echo "[RADIO-SIM] Yaesu FT-891 Radio Simulator verified and ready!"
    fi
    if [ -f "./tools/ft857_simulator.py" ]; then
        echo "[RADIO-SIM] Starting Yaesu FT-857 Radio Simulator verification..."
        python ./tools/ft857_simulator.py --test
        echo "[RADIO-SIM] Yaesu FT-857 Radio Simulator verified and ready!"
    fi
    echo ""
fi

if ! command -v docker >/dev/null 2>&1; then
    echo "[ERROR] Docker is not installed or not in PATH."
    exit 1
fi

if ! docker info >/dev/null 2>&1; then
    echo "[ERROR] Docker daemon is not running."
    exit 1
fi

echo "Running Docker container for target: $TARGET..."
case "$TARGET" in
    clean)
        ./clean.sh
        ;;
    unit)
        docker compose run --rm test-unit
        ;;
    build)
        docker compose run --rm build-apk
        ;;
    release)
        docker compose run --rm build-release
        ;;
    instrumented)
        docker compose run --rm test-instrumented
        ;;
    all)
        docker compose run --rm test-all
        ;;
    *)
        docker compose run --rm test-unit "$TARGET"
        ;;
esac
