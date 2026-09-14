# Job Application Tracker — Java, REST und SQL

Die App besteht aus einem Java-Backend mit Spring Boot, einer dateibasierten H2-SQL-Datenbank und einer kleinen HTML/CSS/JavaScript-Oberfläche. Du kannst Bewerbungen hinzufügen, anzeigen, bearbeiten, löschen und nach Status filtern.

## Starten

**In IntelliJ IDEA:** Öffne das Projekt, importiere `pom.xml` als Maven-Projekt und starte die `main`-Methode in `JobTrackerApplication.java`. Wähle dafür ein JDK ab Version 17. Auf dem Entwicklungsrechner wurde das mit IntelliJs JBR 25 getestet.

**Im PowerShell-Terminal:** Wenn Java 17+ installiert und `JAVA_HOME` gesetzt ist, starte `.\mvnw.cmd spring-boot:run`. Auf dem Entwicklungsrechner ohne separat installiertes JDK geht es so:

```powershell
$env:JAVA_HOME = 'C:\Program Files\JetBrains\IntelliJ IDEA 2026.2.2\jbr'
.\mvnw.cmd spring-boot:run
```

Öffne danach **http://localhost:8080** im Browser. Die HTML-Datei muss jetzt über den Java-Server geladen werden; ein Doppelklick auf die Datei reicht nicht mehr. Mit `Ctrl+C` beendest du den Server. Die Daten bleiben in `data/applications.mv.db` erhalten. Daten aus der älteren `localStorage`-Version werden nicht automatisch übernommen.

Tests ausführst du mit `.\mvnw.cmd test`.

## Wie eine Aktion durch die App läuft

1. Im Browser liest [`app.js`](src/main/resources/static/app.js) das Formular und sendet JSON mit `fetch()` an `/api/applications`.
2. [`ApplicationController.java`](src/main/java/com/example/jobtracker/ApplicationController.java) nimmt die HTTP-Anfrage entgegen und prüft die Eingaben mit [`ApplicationRequest.java`](src/main/java/com/example/jobtracker/ApplicationRequest.java).
3. [`ApplicationRepository.java`](src/main/java/com/example/jobtracker/ApplicationRepository.java) führt SQL mit Parametern (`?`) aus. Dort stehen `SELECT`, `INSERT`, `UPDATE` und `DELETE` gut sichtbar.
4. H2 speichert die Zeile in der Tabelle aus [`schema.sql`](src/main/resources/schema.sql). Der Controller schickt die Antwort als JSON zurück; das Frontend aktualisiert die Tabelle.

Das Datenmodell ist [`ApplicationEntry.java`](src/main/java/com/example/jobtracker/ApplicationEntry.java): `id`, `company`, `role`, `dateApplied`, `url`, `status` und `notes`. Die Datenbank erzeugt die ID selbst.

## Die REST-API ausprobieren

| Methode | Pfad | Bedeutung |
|---|---|---|
| `GET` | `/api/applications` | Alle Bewerbungen lesen, neueste zuerst |
| `GET` | `/api/applications?status=Interview` | Nach Status filtern |
| `GET` | `/api/applications/1` | Eine Bewerbung lesen |
| `POST` | `/api/applications` | Neue Bewerbung anlegen (`201 Created`) |
| `PUT` | `/api/applications/1` | Bewerbung vollständig ändern |
| `DELETE` | `/api/applications/1` | Bewerbung löschen (`204 No Content`) |

Probiere in PowerShell zuerst `Invoke-RestMethod http://localhost:8080/api/applications`. So legst du einen Eintrag ohne Benutzeroberfläche an:

```powershell
$application = @{
  company = 'Exxeta AG'
  role = 'Java Developer'
  dateApplied = '2026-09-14'
  url = 'https://example.com/job'
  status = 'Applied'
  notes = 'Lebenslauf verschickt'
} | ConvertTo-Json
Invoke-RestMethod -Method Post -Uri http://localhost:8080/api/applications -ContentType 'application/json' -Body $application
```

Die Antwort enthält eine `id`. Ersetze damit die `1` in den Pfaden oben. Für eine Änderung benutzt du `Invoke-RestMethod -Method Put -Uri http://localhost:8080/api/applications/1 -ContentType 'application/json' -Body $application` (mit angepasstem JSON). Bei ungültigen Pflichtfeldern gibt die API `400 Bad Request`, bei einer unbekannten ID `404 Not Found` zurück.

Die Datenbankdatei und Maven-Builddateien sind von Git ausgeschlossen. Die App bindet sich nur an `127.0.0.1`, also an deinen eigenen Rechner.
