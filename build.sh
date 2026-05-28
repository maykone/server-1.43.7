#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"

command -v java >/dev/null    || { echo "ERROR: 'java' introuvable dans le PATH (JDK 11+ requis)"; exit 1; }
command -v javac >/dev/null   || { echo "ERROR: 'javac' introuvable dans le PATH (JDK 11+ requis)"; exit 1; }
command -v kotlinc >/dev/null || { echo "ERROR: 'kotlinc' introuvable (brew install kotlin / apt install kotlin)"; exit 1; }

echo "=== Compilation aegnor_gameV2 ==="
mkdir -p build/classes

CP="$(find librerias -name '*.jar' 2>/dev/null | tr '\n' ':')"

JAVA_LIST="$(mktemp)"
KT_LIST="$(mktemp)"
find src -name '*.java' > "$JAVA_LIST"
find src -name '*.kt'   > "$KT_LIST"

if [ -s "$KT_LIST" ]; then
  kotlinc -cp "$CP" -d build/classes @"$JAVA_LIST" $(cat "$KT_LIST")
fi
javac -encoding UTF-8 -cp "${CP}build/classes" -d build/classes @"$JAVA_LIST"

rm -f "$JAVA_LIST" "$KT_LIST"
echo "=== Build OK ($(find build/classes -name '*.class' | wc -l | tr -d ' ') classes) ==="
echo "Lance ensuite ./start.sh"
