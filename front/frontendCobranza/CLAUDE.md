# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project overview

This is the Angular frontend for a "cobranza de plazas" (marketplace/plaza fee collection) system. It is one of three related repositories: a Java Spring Boot backend (`..\..\backend\backendCobranza`), this Angular frontend, and an Android mobile app — this CLAUDE.md covers only this frontend repo.

**Sibling duplicate warning:** `C:\Users\Oswaldo\Documents\projects\cobranza_update\front\cobranzaCli` is a near-identical local-only copy of this same app (no git remote, slightly different commit history). The only functional difference found is `src/app/shared/constantes.ts`: `cobranzaCli` points `base_url` at `http://localhost:8080` while this repo (`frontendCobranza`) points it at `http://localhost:8080/cobranzaPlaza`. This repo (`frontendCobranza`) is the canonical one, pushed to `https://github.com/OswaldoJaimeSolis/frontendCobranza.git`. Do not edit `front/cobranzaCli` when working on this repo.

## Commands

Angular CLI 7 project (internal Angular project name is `cobranzaCli`, set in `angular.json`, even though the folder/repo is `frontendCobranza`).

- Install deps: `npm install`
- Dev server: `npm start` (alias for `ng serve`) — serves at `http://localhost:4200/`, auto-reloads on file changes
- Build (dev): `ng build`
- Build (prod): `ng build --prod` — applies `fileReplacements` swapping in `src/environments/environment.prod.ts`, enables AOT, optimization, output hashing; output goes to `dist/cobranzaCli`
- Run all unit tests: `npm test` (alias for `ng test`) — runs Karma/Jasmine specs via `src/karma.conf.js`
- Run a single test file: use Karma's file filtering, e.g. `ng test --include='**/contribuyente.service.spec.ts'`, or narrow with Jasmine's `fit`/`fdescribe` in the spec temporarily
- Lint: `npm run lint` (alias for `ng lint`) — uses **TSLint** (`tslint.json`, extends `tslint:recommended` + `codelyzer` for Angular-specific rules), not ESLint
- E2E: `ng e2e` (Protractor, config at `e2e/protractor.conf.js`) — present via Angular CLI scaffolding but no e2e specs beyond the CLI default were found to be actively maintained

## Architecture

### Stack
Angular 7 (`@angular/core` ~7.2), Angular Material 7 + Angular CDK + `@angular/flex-layout` for UI/layout, RxJS 6 for async, TypeScript ~3.2, TSLint/Codelyzer for linting, Karma/Jasmine for unit tests. No NgRx or other state management library — state is held locally in components/services.

### Module structure
The app is a **single monolithic `AppModule`** (`src/app/app.module.ts`) — there are no feature `NgModule`s and no lazy-loaded routes. Every component, pipe, and service across the app is declared/provided directly on `AppModule`. Components that open as Angular Material dialogs (e.g. `AddContribuyenteComponent`, `AddRecaudadorComponent`, `DialogInformativoComponent`, `DialgAnswerSiNoComponent`, the `Busqueda*` search dialogs) are registered in `entryComponents`.

Source is organized by feature under `src/app/`:
- `catalogos/` — the CRUD "catalog" features, one subfolder per entity: `contribuyente`, `recaudador`, `tiposplaza`, `propietarioplaza`. Each entity folder follows the same internal layout: `model/` (plain TS class, e.g. `contribuyente.ts`), `service/` (HTTP service, e.g. `contribuyente.service.ts`), `filter/` or `filter.pipe.ts` (a pipe for client-side filtering of table rows), `list-*` (a list/table component), `add-*` (an add/edit component, usually opened as a Material dialog). Some entities have extra sub-features (e.g. `tiposplaza` has an `add-tiposplaza-vigencia` / `list-tiposplaza-vigencia` pair for date-range "vigencia" records; `recaudador` has `list-tipo-plaza-recaudador` for the recaudador↔tipo-plaza relationship; `propietarioplaza` has both `list-propietario-plaza` and a newer `list-propietario-plaza-jb` variant, the latter being the one actually wired into routing).
- `reportes/` — reporting features (`reporte-contribuciones`, `reporte-contribuyentes`) plus a shared `service/reportes.service.ts`.
- `shared/` — cross-feature reusable pieces: `constantes.ts` (API base URL, see below), generic dialog components (`dialog-informativo` for message-only dialogs, `dialg-answer-si-no` for yes/no confirmation dialogs), and `busqueda/` (reusable "search and pick" dialog components per entity: `busqueda-contribuyentes`, `busqueda-recaudadores`, `busqueda-tipos-plaza`, used from add/edit forms to look up a related record).
- `menu-principal/` — the top navigation bar (Angular Material `mat-menu`), hardcodes the two menu groups ("Catálogos", "Reportes") and their `routerLink`s.

### Routing
Flat route table in `src/app/app-routing.module.ts`, no guards, no nesting, no lazy loading:
`contribuyentes`, `recaudadores`, `tiposPlaza`, `propietarioPlaza` (→ `ListPropietarioPlazaJbComponent`), `reporteContribuciones`, `reporteContribuyentes`. There is no default/wildcard route or 404 handling.

### Backend communication
- The backend base URL is a **hardcoded static field**, not driven by `src/environments/*` (those only toggle `production: true/false` and are otherwise unused for API config): `src/app/shared/constantes.ts` exports `Constantes.base_url`. Commented-out alternatives in that file show it's manually edited per environment (LAN IP / plain localhost / context-path localhost).
- Each feature has its own `*.service.ts` (e.g. `ContribuyenteService`, `RecaudadorService`, `TiposplazaService`, `PropietarioPlazaService`, `ReportesService`), injected via `providedIn: 'root'` and additionally listed in `AppModule.providers`. Services build their endpoint as `Constantes.base_url + '/<resource>'` and expose CRUD methods (`getTodos`, `get<Entity>(id)`, `add<Entity>`, `update<Entity>`, `delete<Entity>`) returning RxJS `Observable`s from `HttpClient.get/post/put/delete`.
- **There is no auth/session layer**: no HTTP interceptors, no route guards, no token storage (no `localStorage`/`sessionStorage` usage), no login flow anywhere in the codebase. Requests go out unauthenticated.

### UI/data patterns
- List components fetch full collections (`getTodos()`) and filter client-side using a per-entity Angular `Pipe` (e.g. `FilterPipe`, `RecaudadorPipe`, `TiposplazaPipe`, `PropietarioPlazaPipe`) bound to a search box — there's no server-side search/pagination.
- Add/edit is done through Angular Material dialogs (`MatDialog.open(...)`) rather than separate routed pages; the same `Add*Component` is reused for both create and edit by passing `MAT_DIALOG_DATA` (edit mode is detected by whether `dd` is non-null, toggling an `editarId`/`encabezado` flag).
- Confirmations and messages reuse the two generic shared dialogs (`DialgAnswerSiNoComponent` for yes/no, `DialogInformativoComponent` for info messages) instead of one-off dialogs per feature.
- Cross-entity lookups (e.g. picking a contribuyente or recaudador from a form) reuse the `shared/busqueda/*` dialog components rather than duplicating search UI.
- Service calls generally subscribe inline with the three-callback form (`next`, `error`, `complete`), logging to `console.log` on success/error rather than surfacing errors in the UI.
