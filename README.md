# JARVIS Android 1.0 – Learning Agent Foundation

Dies ist die erste vollständige technische Grundlage für einen lernfähigen persönlichen Android-Agenten.

## Enthalten

### Agent
- zentrale Agent-Klasse
- Gesprächsprotokoll
- Memory-Suche
- Befehlsrouting

### Lernen
- explizite Präferenzen
- Korrekturen
- einfache Muster
- Schutz vor dem Speichern offensichtlicher Geheimnisse

### Gedächtnis
- Room-Datenbank
- Langzeit-Memory
- Gesprächsspeicher

### Sprache
- Android Speech Recognizer
- deutsche Sprachausgabe

### Android Tools
- sichere App-Öffnung
- Web-Öffnung
- erweiterbare Tool-Schnittstelle

## Noch bewusst nicht automatisiert

Ein echter KI-Agent braucht einen externen KI-Anbieter oder ein lokal laufendes Modell. Diese Schnittstelle ist absichtlich noch nicht mit einem geheimen API-Schlüssel im Code verdrahtet.

## Öffnen

Projekt in Android Studio öffnen und Gradle synchronisieren.

## Sicherheitsprinzip

JARVIS darf nicht eigenständig seinen ausführbaren Programmcode verändern. Er darf sein Gedächtnis und seine Präferenzen erweitern. Sensible Daten und folgenreiche Aktionen benötigen zusätzliche Schutzmechanismen.

## Roadmap

1. KI-Provider über sichere Server/API-Schicht
2. Tool Calling mit strukturierten Aktionen
3. echtes Wake-Word/Foreground-Service
4. Kalender, Erinnerungen und Benachrichtigungen
5. Web-Recherche
6. Mustererkennung und Routinen
7. verschlüsselter Memory Store
8. Backup/Restore
9. Tests und CI
10. APK/Release-Build
