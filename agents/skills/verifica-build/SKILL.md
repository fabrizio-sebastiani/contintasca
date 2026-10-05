---
name: verifica-build
description: Comandi per creare, compilare, testare e avviare backend Spring Boot e frontend Angular su questo PC (Windows, Git Bash), regole per leggere gli errori e limite di 3 tentativi di correzione prima dell'escalation. Da usare ogni volta che si crea, compila, testa o avvia l'app.
---

# Verifica della build

Ambiente: Windows 11, Git Bash, JDK 25, Maven 3.9, Node 24, npm 11. Angular CLI **non** è installato globalmente: usa sempre `npx`. Tutti i comandi vanno lanciati dalla radice del progetto.

## Comandi
| Scopo | Comando |
|---|---|
| Crea il backend | `mkdir -p app && cd app && curl -s https://start.spring.io/starter.tgz -d type=maven-project -d language=java -d javaVersion=21 -d groupId=it.hagenthon -d artifactId=backend -d name=backend -d packageName=it.hagenthon.backend -d dependencies=web,data-jpa,h2,validation -d baseDir=backend \| tar -xzf -` |
| Test del backend | `cd app/backend && mvn -q test` |
| Avvia il backend (in background) | `mkdir -p "$USERPROFILE/.itm-tmp" && cd app/backend && mvn -q spring-boot:run "-Dspring-boot.run.jvmArguments=-Djdk.net.unixdomain.tmpdir=$USERPROFILE/.itm-tmp"` |
| Backend pronto? | `curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/` → qualunque codice diverso da `000` |
| Ferma il backend | `powershell -NoProfile -Command "Get-NetTCPConnection -LocalPort 8080 -State Listen \| ForEach-Object { Stop-Process -Id $_.OwningProcess -Force }"` |
| Crea il frontend | `cd app && NG_CLI_ANALYTICS=false npx -y @angular/cli@latest new frontend --defaults --skip-git --style=css --ssr=false --routing=true` |
| Build del frontend | `cd app/frontend && npx ng build` |
| Avvia il frontend | `cd app/frontend && npx ng serve` → `http://localhost:4200` |

Se `ng new` si ferma su una domanda interattiva o rifiuta un'opzione, leggi il messaggio, aggiungi o togli **solo** l'opzione indicata e riprova (conta come tentativo).

## Come leggere gli errori
1. **Maven**: cerca le prime righe `[ERROR]` che contengono un percorso `.java:[riga,colonna]`. Correggi **solo il primo errore**, poi ricompila: gli altri sono spesso conseguenze.
2. **Test falliti**: cerca `Tests run: … Failures: …` e il nome del test; leggi il messaggio `expected … but was …`.
3. **Angular**: cerca le righe `✘ [ERROR]` o `error TS…` con `file.ts:riga:colonna`. Correggi solo il primo errore.
4. Non leggere mai i log interi in contesto: filtra con `grep -m 20 -E "\[ERROR\]|error TS|✘"`.

## Problemi noti di questo PC
| Sintomo | Causa probabile | Azione |
|---|---|---|
| `EBUSY`, `EPERM`, file bloccati durante `npm install` o `mvn` | il progetto è in una cartella sincronizzata da OneDrive | `ESITO: SERVE_UTENTE`: chiedere di mettere in pausa la sincronizzazione di OneDrive |
| `Port 8080 was already in use` | backend già avviato | usa il comando "Ferma il backend", poi riprova |
| `Unsupported class file major version` | versione Java non allineata | verifica `javaVersion=21` nel `pom.xml` (`<java.version>21</java.version>`) |
| `ng: command not found` | CLI non globale | usa `npx ng …` |
| `Unable to establish loopback connection` / `SocketException: Invalid argument: connect` all'avvio di Tomcat | Java non riesce a creare il socket AF_UNIX interno di NIO nella cartella temporanea di Windows (non è OneDrive) | avvia con `-Dspring-boot.run.jvmArguments=-Djdk.net.unixdomain.tmpdir=<cartella esistente, es. $USERPROFILE/.itm-tmp>` (già nel comando di avvio) |

## Limite di tentativi
- Massimo **3 tentativi di correzione** per la stessa build. Un tentativo = una modifica seguita da una nuova build.
- Dopo il terzo fallimento: **fermati**, rispondi `ESITO: BLOCCATO` con l'ultimo errore riassunto in massimo 5 righe (comando, file:riga, messaggio, cosa hai provato).
