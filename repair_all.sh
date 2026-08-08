#!/usr/bin/env bash
set -e

export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
export PATH="$JAVA_HOME/bin:$PATH"

echo "=== JARVIS KOMPLETTREPARATUR ==="

# Verzeichnisse
mkdir -p app/src/main/java/com/etno2500pixel/jarvis/core
mkdir -p app/src/main/java/com/etno2500pixel/jarvis/data
mkdir -p app/src/main/java/com/etno2500pixel/jarvis/tools
mkdir -p app/src/main/java/com/etno2500pixel/jarvis/voice
mkdir -p app/src/main/res/values

# Kotlin-Dateien einsortieren
for f in ConversationDao.kt ConversationEntity.kt JarvisDatabase.kt MemoryDao.kt MemoryEntity.kt; do
    [ -f "$f" ] && mv -f "$f" app/src/main/java/com/etno2500pixel/jarvis/data/
done

[ -f LearningEngine.kt ] && mv -f LearningEngine.kt app/src/main/java/com/etno2500pixel/jarvis/core/
[ -f AndroidTools.kt ] && mv -f AndroidTools.kt app/src/main/java/com/etno2500pixel/jarvis/tools/
[ -f VoiceManager.kt ] && mv -f VoiceManager.kt app/src/main/java/com/etno2500pixel/jarvis/voice/

[ -f app/src/main/core/JarvisAgent.kt ] && \
mv -f app/src/main/core/JarvisAgent.kt \
app/src/main/java/com/etno2500pixel/jarvis/core/JarvisAgent.kt

rm -rf app/src/main/core

# Ressourcen
[ -f styles.xml ] && mv -f styles.xml app/src/main/res/values/styles.xml

# Root-Manifest entfernen – Android verwendet app/src/main/AndroidManifest.xml
rm -f AndroidManifest.xml

# Alte Root-Kotlin-Dateien entfernen, falls noch vorhanden
rm -f Agent.kt MainActivity.kt AndroidTools.kt ConversationDao.kt \
ConversationEntity.kt JarvisDatabase.kt LearningEngine.kt MemoryDao.kt \
MemoryEntity.kt VoiceManager.kt

# Gradle stabilisieren
cat > gradle.properties <<'PROPS'
org.gradle.jvmargs=-Xmx512m -XX:MaxMetaspaceSize=256m -Dfile.encoding=UTF-8
org.gradle.daemon=false
org.gradle.parallel=false
org.gradle.workers.max=1
android.useAndroidX=true
android.nonTransitiveRClass=true
kotlin.code.style=official
PROPS

# Android/Kotlin Java 17
python3 - <<'PY'
from pathlib import Path

p=Path("app/build.gradle.kts")
s=p.read_text()

s=s.replace('    id("org.jetbrains.kotlin.plugin.serialization")\n','')

if "compileOptions" not in s:
    s=s.replace(
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

echo
echo "=== STRUKTUR ==="
find app/src/main -type f | sort

echo
echo "=== REPARATUR ABGESCHLOSSEN ==="
