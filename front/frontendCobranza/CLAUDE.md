# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project overview

This is the Angular frontend for a "cobranza de plazas" (marketplace/plaza fee collection) system. It is one of three related repositories: a Java Spring Boot backend (`..\..\backend\backendCobranza`), this Angular frontend, and an Android mobile app — this CLAUDE.md covers only this frontend repo.

**Sibling duplicate warning:** `C:\Users\Oswaldo\Documents\projects\cobranza_update\front\cobranzaCli` is a near-identical local-only copy of this same app (no git remote, slightly different commit history). The only functional difference found is `src/app/shared/constantes.ts`: `cobranzaCli` points `base_url` at `http://localhost:8080` while this repo (`frontendCobranza`) points it at `http://localhost:8080/cobranzaPlaza`. This repo (`frontendCobranza`) is the canonical one, pushed to `https://github.com/OswaldoJaimeSolis/frontendCobranza.git`. Do not edit `front/cobranzaCli` when working on this repo.

## Commands

Angular CLI 22 project (internal Angular project name is `cobranzaCli`, set in `angular.json`, even though the folder/repo is `frontendCobranza`).

**Node requirement:** Angular 22 needs Node `>= 22.22.3` (or 24.15+/26+). Older Node versions make every `ng` command exit immediately with a version error.

- Install deps: `npm install` (or `npm ci`)
- Dev server: `npm start` (alias for `ng serve`) — serves at `http://localhost:4200/`, auto-reloads on file changes
- Build (dev): `ng build`
- Build (prod): `ng build --configuration production` — applies `fileReplacements` swapping in `src/environments/environment.prod.ts`, enables optimization and output hashing; output goes to `dist/cobranzaCli`
- Run all unit tests: `npm test` (alias for `ng test`) — Karma/Jasmine. There is no `karma.conf.js`; the CLI supplies the default Karma configuration, and `src/test.ts` only sets up the TestBed environment. Headless: `ng test --watch=false --browsers=ChromeHeadless`
- Run a single test file: `ng test --include='**/contribuyente.service.spec.ts'`, or narrow with Jasmine's `fit`/`fdescribe` in the spec temporarily
- Lint: `npm run lint` (alias for `ng lint`) — **ESLint** flat config in `eslint.config.js` (`eslint` 9 + `typescript-eslint` 8 + `angular-eslint` 22). TSLint/Codelyzer are gone.
- E2E: none. The Protractor scaffolding was removed along with the `e2e/` directory; no e2e framework is wired up.

## Architecture

### Stack
Angular 22 (`@angular/core` ^22.2), Angular Material 22 + Angular CDK 22 for UI, plain CSS Flexbox for layout, RxJS 7 for async, TypeScript 6, ESLint (`angular-eslint`) for linting, Karma/Jasmine for unit tests. No NgRx or other state management library — state is held locally in components/services.

Build tooling is the esbuild-based `@angular-devkit/build-angular:application` builder. `tsconfig.json` deliberately sets `"strict": false` (TypeScript 6 defaults it to true) and maps `src/*` through `paths` because `baseUrl` is deprecated; `src/tsconfig.app.json` / `src/tsconfig.spec.json` set `strictTemplates: false`.

### Module structure
The app is a **single monolithic `AppModule`** (`src/app/app.module.ts`) — there are no feature `NgModule`s and no lazy-loaded routes. Every component, pipe, and service across the app is declared/provided directly on `AppModule`. Nothing is standalone: every `@Component`/`@Pipe` carries an explicit `standalone: false`, which is what keeps the NgModule architecture working on Angular 19+. Dialog components (e.g. `AddContribuyenteComponent`, `AddRecaudadorComponent`, `DialogInformativoComponent`, `DialgAnswerSiNoComponent`, the `Busqueda*` search dialogs) are simply declared on `AppModule` and opened with `MatDialog.open()`; `entryComponents` no longer exists.

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
- Templates use Angular's built-in control flow (`@if` / `@else` / `@for`), migrated from `*ngIf`/`*ngFor` by the v21 schematic. Layout that used to rely on `@angular/flex-layout` (`fxLayout`, `fxLayoutGap`, …) is now the global `.container` flexbox rule in `src/styles.css`.
- List components fetch full collections (`getTodos()`) and filter client-side using a per-entity Angular `Pipe` (e.g. `FilterPipe`, `RecaudadorPipe`, `TiposplazaPipe`, `PropietarioPlazaPipe`) bound to a search box — there's no server-side search/pagination.
- Add/edit is done through Angular Material dialogs (`MatDialog.open(...)`) rather than separate routed pages; the same `Add*Component` is reused for both create and edit by passing `MAT_DIALOG_DATA` (edit mode is detected by whether `dd` is non-null, toggling an `editarId`/`encabezado` flag).
- Confirmations and messages reuse the two generic shared dialogs (`DialgAnswerSiNoComponent` for yes/no, `DialogInformativoComponent` for info messages) instead of one-off dialogs per feature.
- Cross-entity lookups (e.g. picking a contribuyente or recaudador from a form) reuse the `shared/busqueda/*` dialog components rather than duplicating search UI.
- Service calls generally subscribe inline with the three-callback form (`next`, `error`, `complete`), logging to `console.log` on success/error rather than surfacing errors in the UI. That form is deprecated in RxJS 7 but still functional.

## Known debt

- `ng lint` reports ~285 pre-existing style errors (double quotes, `var`, `any`, unused vars). They predate the Angular 22 upgrade — the old TSLint setup reported ~336 — and none of them are configuration problems. `ng lint --fix` can clear ~179 of them.
- 16 of the 40 Karma specs fail. They are untouched Angular CLI scaffolding (`should create`) that call `TestBed.configureTestingModule({ declarations: [X] })` with no providers, so components injecting `HttpClient`, `MatDialogRef` or `MAT_DIALOG_DATA` blow up with `NullInjectorError`. They have never passed; fixing them means writing real TestBed setups.
- `@angular/animations` is npm-deprecated but still required by `BrowserAnimationsModule`. It is pinned in lockstep with the other Angular packages.
- The app compiles non-strict (`"strict": false`); enabling TypeScript strict mode is a separate piece of work.
