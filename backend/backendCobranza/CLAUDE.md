# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

This repo (`backendCobranza`) is the Spring Boot API for a "cobranza de plazas" (municipal marketplace/plaza fee collection) system. It is one of three related, separately-repo'd projects: this backend, an Angular frontend, and an Android mobile app. Only this backend is in scope here.

## Build, run, test

Maven wrapper is present (`mvnw` / `mvnw.cmd`) — use it instead of a global Maven install.

- Build: `./mvnw clean install` (packages as a `.war`, per `pom.xml` `<packaging>war</packaging>`)
- Run locally: `./mvnw spring-boot:run`, or run the built artifact with `java -jar target/*.war` after packaging
- Run all tests: `./mvnw test`
- Run a single test class: `./mvnw test -Dtest=CobranzaPlazasApplicationTests`
- There is effectively one test (`src/test/java/com/cobranzaplazas/CobranzaPlazasApplicationTests.java`, JUnit 4 via `SpringRunner`), and it only asserts the Spring context loads — there is no real test coverage of business logic.

Stack: Java 8, Spring Boot 2.1.7 (parent POM), packaged as WAR with `spring-boot-starter-tomcat` (deployable to an external servlet container as well as run standalone via `SpringBootServletInitializer`). Key libs: Spring Data JPA, Spring Security, Thymeleaf, MySQL Connector/J 5.1.6, JasperReports 6.8.1 (+ iText 2.1.7 for PDF), Apache POI (`poi` 4.1.0 / `poi-ooxml` 3.15) for Excel.

## Architecture

This is a **single-controller, no-service-layer** application — everything (endpoint routing, business/validation logic, folio/code generation, report generation, Excel parsing) lives directly in one class:

- `src/main/java/com/cobranzaplazas/controller/CobranzaPlazasController.java` (~1,200 lines) — a `@RestController` mapped at `"/"` that exposes REST CRUD for every entity (tipos de plaza, vigencias, contribuyentes, recaudadores, recaudador↔tipo-plaza assignments, propietarios de plaza) plus three reporting endpoints (`/contribucionesreporte`, `/contribucionespagoreporte`, `/contribuyentesreporte`) that build JasperReports PDFs/XLS on the fly. There is no separate service/business layer — DAOs are `@Autowired` straight into the controller and called directly from request-handling methods.
- `src/main/java/com/cobranzaplazas/repo/` — combines JPA `@Entity` classes and their `JpaRepository` interfaces in the same package (e.g. `Contribucion` + `ContribucionDao`, `PropietarioPlaza` + `PropietarioPlazaDao`, plus `Contribuyente`, `Recaudador`, `RecaudadorTipoPlaza`, `TipoPlaza`, `TipoPlazaVigencia`, `Plaza`, `EquipoRecaudador`, `Folio`). Several DAOs define large hand-written JPQL constants (string concatenation of `SELECT new com.cobranzaplazas.model.JBxxx(...)`) with multiple filter variants (by período, by contribuyente, by tipo de plaza, or combinations) rather than using Spring Data derived queries or `Specification`s.
- `src/main/java/com/cobranzaplazas/model/` — plain DTOs used as JPQL constructor-expression targets for reporting/list endpoints (`JBContribucionReporte`, `JBContribuyente`, `JBPropietarioPlaza`), plus `GeneradorCodigos`, a small helper that derives a contribuyente's code from initials of apellido paterno/materno/nombre.
- `src/main/java/com/cobranzaplazas/config/SpringSecurityConfig.java` — the only config class.

### Domain model
Core entities and how they relate: `Contribuyente` (taxpayer) owns `PropietarioPlaza` records (ownership of a `Plaza` for a `TipoPlaza`, with `vigenciaInicial`/`vigenciaFinal` validity dates and an `importe`); `Contribucion` (a fee payment) references `Contribuyente`, `Recaudador` (collector), `Plaza`, `TipoPlaza`, and `EquipoRecaudador` (collector's device/team), and carries `estadoPago` (payment status code) plus `fechaContribucion`/`fechaModificacion`. `TipoPlazaVigencia` tracks the historical validity/pricing of a `TipoPlaza`. `Folio` is a simple per-key sequence counter (`codigoFolio` → `folio` int) used to mint new codes for contribuyentes and for `propietarioPlaza` IDs — all reads-then-increment-then-save, with no locking, so it is not safe under concurrent writers.

### Request flow
Controller method → `@Autowired` `*Dao` (Spring Data `JpaRepository`) → Hibernate/JPA → MySQL. PUT-style "replace" handlers follow a `findById(...).map(existing -> { mutate fields; return dao.save(existing); }).orElseGet(() -> dao.save(newEntity))` pattern (update-if-exists, else insert) rather than distinct create/update paths for most entities. There is no `@ExceptionHandler`/`@ControllerAdvice` anywhere — lookups that miss throw a bare `new RuntimeException(id)`, and reporting endpoints catch broadly and just log or return `null` on failure. Error handling is inconsistent per-endpoint rather than centralized.

### Authentication / CORS
`SpringSecurityConfig` (`WebSecurityConfigurerAdapter`) configures:
- In-memory single user (`cobranza`/role `ADMIN`) via `NoOpPasswordEncoder` (plaintext password comparison) — no DB-backed user store, no JWT.
- HTTP Basic auth, stateless sessions (`SessionCreationPolicy.STATELESS`), CSRF disabled.
- `authorizeRequests().antMatchers("/**").permitAll()` — i.e. the matcher rule itself currently permits all paths, effectively neutralizing the auth requirement at the security-filter level (most controller methods also have `@Secured("ROLE_ADMIN")` commented out).
- CORS is handled per-endpoint via `@CrossOrigin(origins = "http://localhost:" + port)` repeated on nearly every controller method, hardcoded to `http://localhost:4200` (the Angular dev server) — there is no global CORS `WebMvcConfigurer`, so any change to allowed origins currently means editing every method.

### Reporting (JasperReports)
The three `*reporte` endpoints compile `.jrxml` templates from `src/main/resources/reportes/` (`contribuciones_reporte.jrxml`, `contribuyentes_reporte.jrxml` — compiled `.jasper` versions are checked in alongside) at request time via `JasperCompileManager`, fill them from a `JRBeanCollectionDataSource` wrapping the JPQL DTO results, then export to PDF (`JRPdfExporter`, encrypted, print-only permission) or XLSX (`JRXlsExporter`). The exporter writes the output to a file named `cargos.pdf`/`cargos.xls` **in the process's working directory** before reading it back into memory to return as the HTTP response body — this is why `cargos.pdf`/`cargos.xls` exist at the repo root (leftover artifacts from local runs, not real project files). The report embeds `src/main/resources/logos/logo.png`.

### Excel import (currently dead code)
`poi`/`poi-ooxml` are on the classpath and `src/main/resources/padron_ambulante.xls` / `padron_tianguis_local.xls` are sample padron (registry) spreadsheets, but the `/UploadFromXLS` controller method that would parse them into `Contribuyente`/`PropietarioPlaza` records is entirely commented out in `CobranzaPlazasController`. Treat this feature as unimplemented/disabled rather than working code.

### Persistence / config
`src/main/resources/application.properties` configures a MySQL datasource (`spring.jpa.database=mysql`, `com.mysql.jdbc.Driver`) pointed at a remote production-looking host, with `spring.jpa.hibernate.ddl-auto=update` commented out (disabled via a leading `!`, not a real Spring Boot syntax — effectively that property is unset) and `spring.cache.type=NONE`. There are no Flyway/Liquibase migrations; schema is whatever Hibernate/JPA infers from the `@Entity` annotations, and the property file mixes a live-looking DB URL/credentials with commented-out (`!`-prefixed) alternates for localhost and a second "_rastro" schema — treat the checked-in credentials as already-exposed/rotatable rather than copying or reusing them elsewhere. There is no Spring `@Profile`/multi-environment setup (dev/prod), just this single properties file.

Static/template resources: `src/main/resources/templates/` has two Thymeleaf templates (`greeting.html`, `UploadFromXLS.html`) that appear to be leftovers from the Spring Boot starter guide / the disabled upload feature rather than part of the live app (the controller is a pure `@RestController`, not `@Controller` returning views).
