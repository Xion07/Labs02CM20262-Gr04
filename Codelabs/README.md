# Codelabs y retos – Computación Móvil 2026-2 (Grupo 04)

Cinco proyectos Android independientes (Jetpack Compose y Material 3) con los codelabs y retos del curso. Cada carpeta tiene su propio `gradlew`.

| Carpeta | Actividad | Qué demuestra |
|---|---|---|
| `ComposeBasics` | [Codelab Jetpack Compose basics](https://developer.android.com/codelabs/jetpack-compose-basics) | Composables, estado local y listas en Compose. |
| `ComposeTutorial` | [Tutorial de Compose (4 lecciones)](https://developer.android.com/jetpack/compose/tutorial?hl=es-419) | Diseño de mensajes con layouts, listas y animaciones. |
| `RetoDateTimePicker` | RETO (0.15): [DatePicker](https://developer.android.com/develop/ui/compose/components/datepickers?hl=es-419) y [TimePicker](https://developer.android.com/develop/ui/compose/components/time-pickers-dialogs?hl=es-419) | Diálogos de selección de fecha y hora. |
| `TecladoModificado` | [Modificar el teclado](https://developer.android.com/develop/ui/compose/text/user-input?hl=es-419) | `KeyboardOptions`, `ImeAction` y `KeyboardActions` en campos de texto. |
| `RetoViewModel` | RETO (0.15): [ViewModel y estado en Compose](https://developer.android.com/codelabs/basic-android-kotlin-compose-viewmodel-and-state?hl=es-419) | App Unscramble con ViewModel y pruebas unitarias. |

Paquetes: `co.edu.udea.compumovil.gr04_20262.{basics, tutorial, datetimepicker, teclado, viewmodel}`.
Las apps están en inglés y español (`values` / `values-es`) y siguen el idioma del teléfono.

## Compilar

Requisitos: JDK 17 y Android SDK 34 (AGP 8.6.1, Kotlin 2.0.20, Compose BOM 2024.09.02, minSdk 24).

Desde la carpeta de cada proyecto:

```
./gradlew assembleDebug      # APK en app/build/outputs/apk/debug/
./gradlew assembleRelease    # APK firmado si existe Codelabs/keystore.properties
./gradlew testDebugUnitTest  # solo RetoViewModel
```

`Codelabs/keystore.properties` debe definir `storeFile`, `storePassword`, `keyAlias` y `keyPassword`. No se sube al repositorio.

## Retos

**RetoDateTimePicker – DatePicker**
- Usa `DatePickerDialog` de Material 3.
- Las fechas pasadas quedan bloqueadas.
- La fecha elegida se formatea en UTC para evitar desfases de día.

**RetoDateTimePicker – TimePicker**
- Diálogo propio que envuelve `TimePicker` / `TimeInput`, porque material3 1.3 no incluye `TimePickerDialog`.
- Permite alternar entre reloj y entrada por teclado.

**RetoViewModel – Unscramble**
- `StateFlow` con un `GameUiState` inmutable, observado con `collectAsState`.
- La lógica del juego vive en el ViewModel, no en la UI.
- El estado sobrevive a la rotación de pantalla.
- Pruebas unitarias del ViewModel (`./gradlew testDebugUnitTest`).

## Integrantes

Ver [../INTEGRANTES.md](../INTEGRANTES.md).
