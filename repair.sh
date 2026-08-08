#!/usr/bin/env bash
set -euo pipefail

export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
export PATH="$JAVA_HOME/bin:$PATH"

echo "======================================"
echo " JARVIS REPARATUR"
echo "======================================"

echo "[1/6] Java prüfen..."
java -version

echo "[2/6] Gradle-Konfiguration..."

cat > gradle.properties <<'PROPS'
org.gradle.jvmargs=-Xmx512m -XX:MaxMetaspaceSize=256m -Dfile.encoding=UTF-8
org.gradle.daemon=false
org.gradle.parallel=false
org.gradle.workers.max=1
android.useAndroidX=true
android.nonTransitiveRClass=true
kotlin.code.style=official
PROPS

echo "[3/6] Android-Quellstruktur..."

mkdir -p app/src/main/java/com/etno2500pixel/jarvis/core
mkdir -p app/src/main/java/com/etno2500pixel/jarvis/data
mkdir -p app/src/main/java/com/etno2500pixel/jarvis/tools
mkdir -p app/src/main/java/com/etno2500pixel/jarvis/voice

for f in ConversationDao.kt ConversationEntity.kt JarvisDatabase.kt MemoryDao.kt MemoryEntity.kt; do
    if [ -f "$f" ]; then
        mv -f "$f" app/src/main/java/com/etno2500pixel/jarvis/data/
    fi
done

if [ -f LearningEngine.kt ]; then
    mv -f LearningEngine.kt app/src/main/java/com/etno2500pixel/jarvis/core/
fi

if [ -f AndroidTools.kt ]; then
    mv -f AndroidTools.kt app/src/main/java/com/etno2500pixel/jarvis/tools/
fi

if [ -f VoiceManager.kt ]; then
    mv -f VoiceManager.kt app/src/main/java/com/etno2500pixel/jarvis/voice/
fi

if [ -f app/src/main/core/JarvisAgent.kt ]; then
    mv -f app/src/main/core/JarvisAgent.kt \
      app/src/main/java/com/etno2500pixel/jarvis/core/JarvisAgent.kt
fi

rm -rf app/src/main/core

echo "[4/6] Android/Kotlin-Einstellungen..."

python3 - <<'PY'
from pathlib import Path

p = Path("app/build.gradle.kts")
s = p.read_text()

s = s.replace(
    '    id("org.jetbrains.kotlin.plugin.serialization")\n',
    ''
)

if 'compileOptions {' not in s:
    s = s.replace(
        '    buildFeatures {\n        compose = true\n    }\n',
        '''    buildFeatures {
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
'''
    )

p.write_text(s)
PY

echo "[5/6] Projektdateien prüfen..."

echo
echo "--- Kotlin-Dateien ---"
find app/src/main/java -type f -name "*.kt" | sort

echo
echo "--- Gradle ---"
grep -n "jvmargs\|daemon\|workers" gradle.properties

echo
echo "[6/6] Reparatur erfolgreich abgeschlossen."

echo
echo "WICHTIG:"
echo "Es wurde absichtlich KEIN Gradle-Build gestartet."
echo "======================================"
