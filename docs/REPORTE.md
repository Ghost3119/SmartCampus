# SmartCampus · Reporte de la Unidad 3

**Universidad Tecnológica de Coahuila**  
Desarrollo de Dispositivos Inteligentes  
Proyecto académico de demostración

## Objetivo y caso de estudio

Una persona que llega al campus necesita localizar su siguiente clase, encontrar un aula o consultar un comunicado sin iniciar sesión. SmartCampus reúne esas consultas en una pantalla compartida, con controles táctiles y datos locales. El grupo preferido define qué horario aparece al inicio.

## Arquitectura

La actividad aloja un NavHost de Compose con seis destinos. La barra lateral aparece a partir de 840 dp de ancho y en ventanas compactas el encabezado permite abrir un menú. El tema sigue la preferencia guardada o el ajuste del sistema. Las superficies de consulta tienen desplazamiento para admitir pantallas verticales.

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

### Resultados obtenidos

La compilación debug finalizó correctamente. Pasaron seis pruebas JVM y dos pruebas instrumentadas, sin fallos ni omisiones. Se usó un AVD Android 17 (API 37) con área de pantalla de 1280 × 800 y densidad de 160 dpi para comprobar el diseño de tablet. También se abrió el menú compacto en 420 × 850. El recorrido de capturas se realizó en modo avión con Wi-Fi desactivado.

## Evidencia visual

| Captura del emulador | Contenido |
| --- | --- |
| [Inicio en tablet](capturas/inicio-tablet.png) | Resumen, hora local, próxima clase y navegación lateral |
| [Horario](capturas/horarios.png) | Sesiones del grupo 9IDGSB, docentes y estados |
| [Aula A-101](capturas/detalle-aula.png) | Capacidad, planta y siguientes sesiones |
| [Edición local](capturas/editor.png) | Formulario para crear un aviso |
| [Inicio compacto](capturas/inicio-compacto.png) | Navegación y tarjetas en orientación vertical |
