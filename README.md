# Labs02CM20262-Gr04 · Lab 2: Workers y consumo de servicios REST

App Android hecha con **Jetpack Compose y Material 3** a partir del ejemplo **Jetchat** de
[android/compose-samples](https://github.com/android/compose-samples). Se le agregaron tareas en
segundo plano con **WorkManager** (`CoroutineWorker`) que consumen un servicio REST
([dummyjson.com](https://dummyjson.com)) con **Retrofit**.

- Proyecto: `LabsCM20262-Gr04/`, módulo `Lab2-Services`
- Paquete: `co.edu.udea.compumovil.gr04_20262.lab2`
- Idiomas: inglés (`values/`) y español (`values-es/`); la app sigue el idioma del teléfono
- Integrantes: [INTEGRANTES.md](INTEGRANTES.md)

## Qué hace

| Requisito del enunciado | Dónde está |
|---|---|
| **Workers (30 %)**: tarea recurrente en segundo plano | `worker/QuoteWorker.kt` se ejecuta cada 15 min (el mínimo de Android) y requiere red. Tiene reintentos con backoff exponencial. `worker/WorkScheduler.kt` lo programa como trabajo periódico único. |
| | `worker/ReplyWorker.kt` es un trabajo de una sola vez (`OneTimeWorkRequest`) que se encola al enviar un mensaje. Los mensajes seguidos se responden en orden (`APPEND_OR_REPLACE`). |
| **Funcionalidades de Jetchat con workers (40 %)** | 1. **Recibir mensajes**: el worker periódico trae una frase y la publica en el chat como *Quote Bot*. También avisa con una notificación. |
| | 2. **Responder mensajes**: al enviar un mensaje, un worker pide al servicio un comentario y su autor, y lo publica como respuesta. |
| | 3. **Widget de mensajes sin leer** (Glance): se actualiza desde los workers. |
| | 4. **Menú lateral → "Servicio en segundo plano"**: estado en vivo de los workers (`WorkInfo`), hora de la próxima ejecución y botón *Recibir un mensaje ahora*. |
| **Servicio REST para complementar datos (20 %)** | `data/remote/DummyJsonApi.kt` con Retrofit + kotlinx.serialization. Usa `quotes/random` (mensajes), `comments/{id}` (respuestas) y `users/{id}`. Este último **completa el perfil** con cargo, empresa, correo y usuario, que se muestran como "(servicio REST)". |
| **Navegación de Compose** | Navigation 3 (`NavDisplay` + `NavKey` serializables): conversación ↔ perfil, desde el menú lateral o tocando un autor. |
| **Diseño (10 %)** | Material 3 del ejemplo, tema claro y oscuro, textos traducidos. |

Los mensajes que llegan por los workers se guardan en `data/InboxStore.kt` (SharedPreferences +
`StateFlow`). Así la UI, el widget y los workers comparten datos aunque la app esté cerrada.

## Compilar

Requisitos: JDK 17 y Android SDK con la plataforma 37 (AGP 9.3, Gradle 9.5).

```bash
cd LabsCM20262-Gr04
./gradlew :Lab2-Services:assembleDebug        # APK de prueba
./gradlew :Lab2-Services:assembleRelease      # APK firmado
./gradlew :Lab2-Services:connectedDebugAndroidTest   # pruebas en emulador/celular
```

La firma usa `LabsCM20262-Gr04/keystore.properties`, que **no se sube al repo**:

```properties
storeFile=grupo04-release.jks
storePassword=...
keyAlias=...
keyPassword=...
```

Si ese archivo no existe, el release se firma con el keystore de depuración local.

## Probarlo

1. Abrir la app y aceptar el permiso de notificaciones. Llega una frase de *Quote Bot* al chat.
2. Escribir un mensaje y enviarlo. A los segundos responde un usuario del servicio REST.
3. Menú lateral (tocar el nombre del canal) → *Servicio en segundo plano*: ver el estado del worker y pedir un mensaje ya.
4. Menú lateral → *Taylor Brooks*: el perfil muestra los datos traídos del servicio REST.
5. Cambiar el idioma del teléfono entre español e inglés.
