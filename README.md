# SmartCampus

Pantalla informativa interactiva para la Unidad 3 de Desarrollo de Dispositivos Inteligentes de la Universidad Tecnológica de Coahuila. La aplicación identifica permanentemente su carácter de **demostración no oficial**. Los grupos, clases, docentes, avisos y eventos son ejemplos ficticios.

## Ejecutar en Android Studio

1. Abre la carpeta que contiene `settings.gradle.kts` con una versión de Android Studio compatible con Android Gradle Plugin 9.3.3.
2. En **SDK Manager**, instala Android SDK Platform 37 y las herramientas de compilación que solicite Gradle. En la configuración de Gradle selecciona el JDK 25 integrado de Android Studio. El wrapper incluido usa Gradle 9.5.0.
3. Sincroniza el proyecto. La primera compilación necesita acceso a los repositorios de dependencias.
4. En **Device Manager → Create Virtual Device**, elige una tablet de unas 10 pulgadas, instala una imagen Android API 37 para tu arquitectura y arranca el dispositivo. La aplicación admite Android 7.0 (API 24) en adelante.
5. Selecciona la configuración **app**, el emulador y **Run ▶**. En tablet horizontal verás la navegación superior. Para TV, crea un AVD de categoría **TV** (Android TV API 36), arráncalo y selecciónalo como destino de **Run**. Usa las flechas, Enter y Atrás del control virtual. Al primer inicio se carga la base local automáticamente.

## Recorrido

- **Inicio** muestra la hora local, la próxima sesión del grupo preferido, las aulas disponibles y la cartelera.
- **Horarios** permite cambiar de grupo y día. La semana se interpreta de lunes a domingo. Selecciona 9IDGSB y un día laborable para consultar cuatro sesiones.
- **Aulas** combina búsqueda por número o nombre y filtros de edificio y disponibilidad. Toca un resultado para ver su capacidad y próximas clases.
- **Avisos** filtra comunicados por categoría. Una estrella identifica los de prioridad alta.
- **Eventos** presenta la agenda futura por fecha y hora. Toca una publicación para consultar sus detalles.
- **Configuración** guarda grupo preferido, tema y pantalla completa. Activa **Editar avisos y eventos** para crear publicaciones o abrir una existente y editarla o eliminarla. La edición se desactiva al terminar el proceso de la aplicación. Restaurar los ejemplos solicita confirmación y reemplaza la cartelera del dispositivo.

## Diseño aprobado y pantalla informativa

El [archivo de Figma](https://www.figma.com/design/RsZcRdt2WvpSlCWeC0Bw9T) define las tarjetas, la jerarquía tipográfica y el comportamiento adaptativo. TV usa un menú lateral con selección verde y contorno de foco dorado. En tablet se usa una fila superior, y en ventanas compactas se abre un menú. El tema inicial es claro. Las preferencias de tema existentes tienen prioridad.

En **Configuración → SmartCampus Display Mode**, elige 8, 12, 20 o 30 segundos e inicia el recorrido. La pantalla alterna bienvenida, horarios del grupo, eventos, avisos y aulas. **Pausar**, **Reanudar**, **Salir** y el botón Atrás funcionan con el mando. La rotación se detiene al pasar a segundo plano. El intervalo se guarda en DataStore. Mientras esta vista está abierta se mantiene encendida la pantalla.

## Código y almacenamiento

Kotlin, Compose Material 3 y Navigation Compose forman la interfaz. Un ViewModel combina flujos de Room, DataStore y el reloj local con coroutines y StateFlow. Room conserva las publicaciones tras cerrar la aplicación. La disponibilidad expresa una estimación por horario. Toda la consulta y la edición funcionan sin conexión.

Dentro de `app/src/main/java/com/example/smartcampus/`, `data/local` contiene las entidades, DAO y datos iniciales. `data/repository` gestiona persistencia y preferencias. `domain` calcula estados y recurrencias. `di` crea las dependencias. `navigation`, `ui/components`, `ui/screens` y `ui/theme` forman la presentación.

## Compilar, probar e instalar

Desde PowerShell en la raíz, con `JAVA_HOME` apuntando al JDK de Android Studio:

```powershell
.\gradlew.bat assembleDebug
.\gradlew.bat testDebugUnitTest
.\gradlew.bat connectedDebugAndroidTest
.\gradlew.bat installDebug
```

Los dos últimos comandos requieren un emulador o dispositivo conectado. En macOS o Linux usa `./gradlew` en lugar de `.\gradlew.bat`.

El APK se genera en `app/build/outputs/apk/debug/app-debug.apk`. Los informes HTML se guardan en `app/build/reports/tests/testDebugUnitTest/` y `app/build/reports/androidTests/connected/debug/`. El [reporte académico](docs/REPORTE.md) describe la arquitectura y los escenarios de verificación.
