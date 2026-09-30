# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

This is the Android mobile app of a 3-repo "cobranza de plazas" (municipal marketplace/plaza fee collection) system, alongside a separate Java Spring Boot backend and an Angular frontend — this repo covers only the Android client.

## Project basics

- Single-module legacy Android app, package `com.jalpa.cobranza`, applicationId `com.jalpa.cobranza`.
- Gradle 8.14.5 (see `gradle/wrapper/gradle-wrapper.properties`), Android Gradle Plugin 8.13.2. Build with a Java 21 JDK (`JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64` on the maintainer's machine).
- `compileSdk` 36, `targetSdk` 35, `minSdk` 26. Language is plain Java (no Kotlin), compiled at source/target level 21 with core library desugaring enabled.
- `targetSdk` intentionally lags `compileSdk`: at 36 Android's edge-to-edge enforcement can no longer be opted out of, and none of these fixed legacy layouts apply window insets. `res/values-v35/styles.xml` carries the `android:windowOptOutEdgeToEdgeEnforcement` opt-out. Raising `targetSdk` to 36 means auditing every Activity's layout for insets first.
- No dependency injection framework, no ViewModel/LiveData, no Retrofit/OkHttp — this predates those conventions in the codebase.
- There is no README.md, no `.cursor/rules`, and no `.github/copilot-instructions.md` in this repo.
- Git history is effectively a single commit ("cobranza app entregada" — app delivered), so treat this as a delivered/legacy codebase rather than one with an evolving convention log.

## Commands

- Build debug APK: `./gradlew assembleDebug`
- Build release APK: `./gradlew assembleRelease` (minifyEnabled is false; proguard rules in `app/proguard-rules.pro` are effectively empty/default)
- Install on a connected device/emulator: `./gradlew installDebug`
- Run unit tests (JVM, under `app/src/test`): `./gradlew test`
- Run instrumented tests (device/emulator, under `app/src/androidTest`): `./gradlew connectedAndroidTest`
- Run a single test class: `./gradlew test --tests "com.jalpa.cobranza.ExampleUnitTest"` (unit) or `./gradlew connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.jalpa.cobranza.ExampleInstrumentedTest` (instrumented)
- Lint: `./gradlew lint`
- Note: both `app/src/test` and `app/src/androidTest` currently contain only the default generated placeholder test (`ExampleUnitTest`, `ExampleInstrumentedTest`); there is no real test coverage to run against business logic.

## Architecture

The app is a plain Activity-based application with no architectural layering framework (no MVVM/MVP, no Architecture Components beyond Room). Each `Activity` owns its own view state, kicks off `AsyncTask`/plain `Thread`/`IntentService` work directly, and talks straight to a `WebService` instance and to Room DAOs — there is no repository or use-case abstraction layer.

### Screen flow (Activities, all under `app/src/main/java/com/jalpa/cobranza/`)

- `MainActivity` is the entry point (`LAUNCHER`) and hub screen: enter/confirm a plaza contribution (pago de plaza), pick a contributor and plaza, print/reprint tickets, generate a QR for a contributor, and reach every other screen via the options menu.
- `LogueoActivity` is launched by `MainActivity` at startup (`startActivityForResult`) to authenticate a collector (`Recaudador`) against the locally cached recaudadores list; there is no server-side session/token — login is just a local username/password match, and the authenticated collector is held in the static in-memory holder `RecaudadorActivo` for the rest of the app session.
- `ScanActivity` opens the camera (Google Play Services Vision `BarcodeDetector`, QR only) and returns a scanned code to the caller via `onActivityResult`.
- `busqueda.BusquedaContribuyente` — Android search-framework activity (`SearchManager`/`searchable.xml`) for looking up a contributor by code/name, backed by `ContribuyenteAdapter`.
- `busqueda.contribuciones.BusquedaContribuciones` / `ContribucionAdapter` — lists existing contributions.
- `contribuyente.ContribuyenteActivity` — registers a new contributor (`Contribuyente`) plus their plaza and ownership record (`PropietarioPlaza`) locally; uses `GeneradorCodigos` to derive contributor/plaza codes and `FusedLocationProviderClient` to stamp the new plaza with GPS coordinates.
- `DatosEquipoActivity` — sets/edits the collector-device identity (`EquipoRecaudador`, i.e. which physical collection device this is), used to tag contributions and to gate remote contribution sync.
- `ActualizarContribucionesRemotasActivity` — lets the user pick a date/tipo de plaza range to pull already-collected-elsewhere contributions from the server into the local DB.
- All cross-activity data (lists of catalogs, selected entities) is passed as `Serializable` extras in `Intent`s, not through any shared view-model or app-wide state (the one exception is `RecaudadorActivo`, a static singleton holding the logged-in collector).

### Networking

- `WebService.java` is the entire network layer: raw `HttpURLConnection`/`HttpsURLConnection` calls (no Retrofit/OkHttp/Volley), manually building `application/x-www-form-urlencoded` POST bodies and parsing responses with `org.json`.
- Base URL is hardcoded in `WebService`: `https://jalpa.gob.mx/cobranza/`, hitting PHP endpoints (`getTipoPlaza.php`, `getContribuyentes.php`, `getContribuciones.php`, `getRecaudadores.php`, `insert_contribuciones.php`, `insert_equipo_recaudador.php`, `insert_plazas.php`, `insert_contribuyentes.php`, `insert_propietario_plaza.php`). There is no build-variant/environment config for the URL and no auth token — treat any change to the base URL or endpoint contracts as touching the live production surface.
- There is no shared response envelope: "success" is signaled ad hoc, either by response body literally equal to `"correcto"` or by parsing a JSON array/object per endpoint.
- All network calls are synchronous/blocking and are always invoked from a background thread (`AsyncTask.doInBackground`, a raw `Thread`, or from `ServiceUpload`, never directly on the UI thread).

### Local persistence and sync

- Room (`androidx.room`) is the persistence layer. `model/dao/AppDataBase.java` declares the schema (`Contribucion`, `Contribuyente`, `Plaza`, `PropietarioPlaza`, `Recaudador`, `TipoPlaza`, `TipoPlazaHistorial`, `RecaudadorTipoPlaza`, `EquipoRecaudador`, `Folio`), currently at version 11 with `fallbackToDestructiveMigration()` (schema changes wipe local data rather than migrate it — see `model/dao/Database.java`).
- `model/dao/Database.java` is a manual singleton wrapping `Room.databaseBuilder(...).build()` — always obtained via `Database.getInstance(context).getAppDatabase()`; there is no DI, so every Activity/Service fetches it this way directly.
- `model/dao/OperacionesDao.java` is the one large DAO holding almost all queries/transactions (folio numbering, catalog upsert/delete, contribution lookups, offline-sync bookkeeping); `ContribuyentesDao` is a second, smaller DAO.
- Offline-first sync pattern: locally created/modified rows (`Contribucion`, `Plaza`, `Contribuyente`, `PropietarioPlaza`, `EquipoRecaudador`) each carry an `inServer*`/`estadoServidor` boolean flag. `ServiceUpload` (an `IntentService`, started from `MainActivity` on launch and re-triggerable via the "forzar envío" menu action) loops, gathers everything not yet marked as uploaded, POSTs it through `WebService`, and flips the flag once the server acknowledges — this is the only sync mechanism, there's no WorkManager/JobScheduler.
- `Folio` rows are used as per-tipo-de-plaza sequence counters to generate dedicated folio numbers (`folioDedicado`) for contributions and contributor codes, generated transactionally inside `OperacionesDao` methods (`finalizarContribucion`, `insertarContribuyenteNuevo`).

### Hardware-integration features

- **Camera / QR scanning**: `ScanActivity` uses CameraX (`androidx.camera:camera-core/camera2/lifecycle/view`) to drive the camera preview/frame feed and on-device MLKit (`com.google.mlkit:barcode-scanning`, QR only) to decode it, returning the decoded string to the caller as a plain `String` extra (`"barCode"`) rather than a vendor `Barcode` object. This replaced the deprecated Play Services Vision API (`com.google.android.gms.vision.barcode`), which Google stopped updating; `MainActivity.onActivityResult` reads the result with `getStringExtra`, not `getParcelableExtra`. `google.zxing` is used the other direction, to *generate* a QR PNG for a contributor (see `MainActivity.generarImprimirQR`), saved to `getExternalFilesDir(null)/Cobranza/qr_contribuyente.png` (i.e. `Android/data/com.jalpa.cobranza/files/Cobranza/`, via the `MainActivity.getDirectorioCobranza()` helper). This used to be the public `<external storage>/Cobranza/` directory; scoped storage (API 29+) made that unwritable.
- **Bluetooth receipt printing**: `MainActivity.imprimirTicket(...)` and `Imprimir.java` both drive a Bixolon SPP-R310 POS printer over Bluetooth using the `jpos118-controls` / `bixolon_printer_v130` jars (`jpos.POSPrinter`, `com.bxl.config.editor.BXLConfigLoader`). The paired Bluetooth device name is expected to start with `"SPP"`; the printer is (re)configured via `BXLConfigLoader` each time a ticket is printed. `MainActivity` has its own inline printing path and `Imprimir` has a near-duplicate implementation (`imprimirDo`) plus the actual ticket-text formatting (`generarCadena`) used for reprints.
- **Location**: `ContribuyenteActivity` uses `FusedLocationProviderClient` (`play-services-location`) to stamp a newly-registered `Plaza` with lat/long when the contributor is created.
- Firebase is gone. `firebase-ml-vision` / `firebase-core` were dependencies with no `com.google.firebase` usage anywhere in the source and no `google-services.json`, so they were removed. They were, however, transitively supplying `com.google.android.gms.vision`, which the scanner does use — that now comes from an explicit `play-services-vision` dependency.
- The ticket logo is read from the same app-specific directory as the QR (`Android/data/com.jalpa.cobranza/files/Cobranza/logo_ticket.png`). Operators who previously dropped `logo_ticket.png` in the public `Cobranza/` folder must move it, otherwise tickets print without the logo (the code guards on `File.exists()`).
- Required runtime permissions (see `AndroidManifest.xml` and `LogueoActivity.permisosRequeridos()`): camera, fine/coarse location, internet, and Bluetooth. The permission list is built per API level — `BLUETOOTH_CONNECT` from API 31 (the legacy `BLUETOOTH`/`BLUETOOTH_ADMIN` below that), and the storage permissions only on the API levels where they are still grantable, since the app now writes only inside its own external files dir. Permissions are requested in bulk from `LogueoActivity.enablePermisos()` rather than per-feature just-in-time; `MainActivity.imprimirTicket` additionally re-checks `BLUETOOTH_CONNECT` before touching the printer.

### Data model quirks worth knowing before editing

- Contributors, plazas, and ownership records are matched/deduplicated by string business codes (`codigoContribuyente`, `codigoPlaza`, `codigoPropietarioPlaza`), not surrogate DB ids in most lookups — equality/`contains` checks on these codes drive dedup logic in both `WebService` parsing and `OperacionesDao` queries.
- `TipoPlaza.importeGlobal` decides whether a plaza type's fee comes from a shared, date-ranged `TipoPlazaHistorial` table or from a per-`PropietarioPlaza` fixed `importe` — several call sites (`MainActivity.actualizaImporte`, `ContribuyenteActivity.establecerImporte`) branch on this flag.
- `MainActivity.cargarCatalogosLocal()` contains a large block of commented-out one-off data-repair/debug queries (accent-normalization fixes for contributor codes, DB backups, etc.) — these are historical maintenance scratch code, not active logic; don't treat them as documentation of current behavior.
