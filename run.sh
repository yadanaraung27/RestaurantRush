#!/usr/bin/env bash
set -euo pipefail

# Run from repository root
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

if ! command -v javac >/dev/null 2>&1 || ! command -v java >/dev/null 2>&1; then
    echo "Error: javac/java not found. Install JDK 16 or newer." >&2
    exit 1
fi

JAVAC_VERSION="$(javac -version 2>&1 | awk '{print $2}')"
JAVAC_MAJOR="${JAVAC_VERSION%%.*}"
if ! [[ "$JAVAC_MAJOR" =~ ^[0-9]+$ ]] || [ "$JAVAC_MAJOR" -lt 16 ]; then
    echo "Error: JDK 16 or newer is required (found javac $JAVAC_VERSION)." >&2
    exit 1
fi

OUT_DIR="out"
rm -rf "$OUT_DIR"
mkdir -p "$OUT_DIR"

javac -Xlint:all -d "$OUT_DIR" $(find src -name '*.java' -print)

java -cp "$OUT_DIR" src.Main
