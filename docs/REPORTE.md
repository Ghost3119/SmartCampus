# SmartCampus · Reporte de la Unidad 3

**Universidad Tecnológica de Coahuila**  
Desarrollo de Dispositivos Inteligentes  
Proyecto académico de demostración

## Objetivo y caso de estudio

Una persona que llega al campus necesita localizar su siguiente clase, encontrar un aula o consultar un comunicado sin iniciar sesión. SmartCampus reúne esas consultas en una pantalla compartida, con controles táctiles y datos locales. El grupo preferido define qué horario aparece al inicio.

## Arquitectura

La actividad aloja un NavHost con seis módulos y el destino Display. La configuración UI_MODE_TYPE_TELEVISION activa la navegación lateral y los márgenes de seguridad. En dispositivos táctiles se usa una barra superior a partir de 760 dp y un menú en ventanas más pequeñas. El tema sigue la preferencia guardada o el ajuste del sistema. Las superficies de consulta tienen desplazamiento para admitir pantallas verticales.

El contenedor de la aplicación conserva una instancia de Room y de las preferencias. El ViewModel observa cambios en el repositorio, recibe la hora del dispositivo y publica estados de carga, contenido y error. Las pantallas observan ese estado según el ciclo de vida de la actividad. Las escrituras se ejecutan en coroutines y los flujos de las consultas actualizan la interfaz.

## Modelo y reglas

- Room contiene tres grupos, seis materias con sus docentes, seis aulas y sesenta sesiones semanales. Las claves foráneas vinculan cada sesión con un grupo, una materia y un aula.
- El intervalo de una clase incluye el minuto de inicio y termina justo antes del minuto de salida. El aula queda disponible al finalizar, salvo que comience otra sesión en ese momento.
- El horario compara el día elegido contra la semana local en curso. El cálculo de próxima clase busca la siguiente ocurrencia semanal y contempla el cambio de domingo a lunes.
- Los primeros cuatro avisos y tres eventos se crean durante la carga transaccional inicial. Los eventos usan fechas relativas al primer arranque para permitir la demostración. Las ediciones permanecen en la base. Restaurar la cartelera asigna fechas de ejemplo a partir del día de restauración.
- DataStore conserva tema, grupo y pantalla completa. El modo de edición de demostración se mantiene en memoria durante la sesión.

## Preparación del emulador

Abre la raíz en Android Studio, selecciona el JDK 25, instala SDK Platform 37 y sincroniza Gradle. En Device Manager crea una tablet de 10 pulgadas con imagen API 37 y usa **Run** sobre el módulo **app**. Prueba la rotación horizontal y vertical, las barras del sistema y la persistencia tras cerrar y abrir la actividad. El [README](../README.md) incluye los comandos de compilación, pruebas e instalación.

## Recorrido funcional

Desde **Inicio**, consulta las métricas y abre el horario del grupo preferido. Cambia el día para observar los estados de las clases. En **Aulas**, busca 101 y abre la ficha para consultar su ubicación y sus siguientes sesiones. En **Avisos**, selecciona Cultural para acotar la lista. **Eventos** muestra la agenda futura y permite abrir el detalle. Desde **Configuración**, activa la edición local para probar altas, cambios y bajas. El administrador también puede consultar eventos pasados.

## Verificación

Las pruebas JVM fijan una hora conocida para reproducir el inicio y fin de una clase, la siguiente sesión semanal y la disponibilidad de un aula. Comprueban los filtros y la ausencia de cruces de grupo, aula o docente en los ejemplos. La prueba instrumentada de navegación abre los seis destinos y usa los filtros de día y aula. Una segunda prueba instrumentada crea y edita publicaciones en una base temporal, cierra y vuelve a abrir Room, verifica los valores y comprueba el borrado.

### Implementación del diseño aprobado

[SmartCampus — UTC | UI/UX Design System](https://www.figma.com/design/RsZcRdt2WvpSlCWeC0Bw9T) contiene las referencias de TV (1920 × 1080) y tablet (1280 × 800). CampusTheme centraliza los colores de fondo, superficies, texto, foco y selección, las esquinas y la tipografía Inter local. El PNG institucional y la ilustración del campus provienen del archivo aprobado. El icono propio de la aplicación identifica el lanzador.

| Frames | Implementación Compose y adaptación |
| --- | --- |
| Inicio | Dashboard muestra cuatro tarjetas con datos de Room. Fecha, saludo, grupo y disponibilidad se calculan en el dispositivo. Las publicaciones destacadas abren sus detalles. |
| Horarios y aulas | AcademicScreens usa cuadrículas adaptativas, selectores, búsqueda y estados vacíos. En aulas, el detalle es un diálogo desplazable con el foco inicial en Cerrar. |
| Avisos y eventos | Board muestra categorías, cartelera y agenda. El detalle incluye acciones locales cuando está habilitada la administración. |
| Configuración y administración | La cuadrícula de configuración habilita la edición y da acceso a ambas colecciones. BoardEditor implementa los formularios de creación y edición en página. El borrado y la restauración requieren confirmación. |
| Display Mode | DisplayMode consulta el mismo estado Ready y recorre cinco escenas con transición breve. Se añadieron controles de reproducción y salida al diseño de exposición. El botón Atrás recupera el destino anterior. |

Los valores de la paleta corresponden al diseño del producto. En claro, el fondo es `#F2F6F3`, las superficies son blancas, el texto es `#142822` y el primario es `#006448`. El foco usa `#795300` y la selección usa `#CCEBDD`. En oscuro, el fondo es `#0C191B` y el foco es `#FFE186`. El esquema de Room conserva sus entidades y su versión. DataStore incorpora únicamente la preferencia de intervalo.

### Verificación y evidencia

Se ejecutaron las seis pruebas JVM y las cinco instrumentadas en Android TV API 36. Pasaron las once. En el AVD API 37 configurado a 1280 × 800 pasaron las cuatro pruebas táctiles y de persistencia; la prueba exclusiva de mando se omite en dispositivos sin Leanback. La suite instrumentada incluye el recorrido de módulos y filtros, reapertura de Room, un recorrido de creación y eliminación de aviso, cambio de foco con DPAD y apertura con OK, así como persistencia del intervalo, rotación, pausa, reanudación y regreso desde Display Mode. El último escenario usa el reloj de pruebas de Compose para avanzar los intervalos.

| Captura | Escenario |
| --- | --- |
| [TV, Figma y Android](capturas/comparativa-tv.png) | Navegación lateral, foco, cuatro tarjetas y recurso institucional |
| [Tablet, Figma y Android](capturas/comparativa-tablet.png) | Navegación superior y composición horizontal |
| [Horarios](capturas/horarios.png) | Selector de día y grupo, materia y docente |
| [Detalle de aula](capturas/detalle-aula.png) | Próximas sesiones |
| [Administración](capturas/editor.png) | Formulario local |
| [Vista compacta](capturas/inicio-compacto.png) | Adaptación vertical |
| [Display Mode](capturas/display-tv.png) | Pantalla de exposición en el AVD de TV |

La comparación se hace con datos y hora reales del emulador. Las diferencias en el texto y el número de aulas libres dependen del momento de la consulta. Las tarjetas pueden crecer con el contenido y las pantallas se desplazan cuando falta espacio. Las dimensiones finales dependen de la densidad, la escala de fuente y las barras del sistema.
