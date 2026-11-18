package com.example.smartcampus.data.local

import java.time.LocalDate

object DemoData {
    val groups = listOf("9IDGSB", "9IDGSA", "6MECB").map(::CampusGroup)
    val subjects = listOf(
        Subject("Dispositivos inteligentes", "Mtra. Elena Ríos"),
        Subject("Arquitectura de software", "Mtro. Diego Luna"),
        Subject("Sistemas embebidos", "Dra. Nora Gil"),
        Subject("Gestión de proyectos", "Mtra. Sara Leal"),
        Subject("Inglés técnico", "Mtro. Luis Vega"),
        Subject("Diseño de interfaces", "Mtra. Julia Solís")
    )
    val rooms = listOf(
        Classroom("A-101", "Edificio A", "Planta baja", 32, "Aula de seminarios con proyector y mesas de trabajo."),
        Classroom("A-204", "Edificio A", "Primer piso", 28, "Laboratorio de desarrollo móvil."),
        Classroom("B-102", "Edificio B", "Planta baja", 24, "Laboratorio de electrónica con estaciones de prototipado."),
        Classroom("B-201", "Edificio B", "Primer piso", 36, "Aula multimedia para sesiones de teoría."),
        Classroom("C-103", "Edificio C", "Planta baja", 40, "Sala de idiomas con equipo audiovisual."),
        Classroom("C-205", "Edificio C", "Primer piso", 30, "Taller de diseño y colaboración.")
    )
    val sessions = buildList {
        for (day in 1..5) for (slot in 0..3) for ((index, group) in groups.withIndex()) {
            val course = (day + slot + index) % subjects.size
            val start = listOf(480, 600, 780, 900)[slot]
            add(Session(size + 1, group.code, subjects[course].name, rooms[course].name, day, start, start + 90))
        }
    }

    suspend fun board(dao: CampusDao, today: LocalDate) {
        dao.save(Notice(title = "Registro de proyectos integradores", description = "Entrega la ficha de tu equipo en la coordinación de carrera durante esta semana.", category = "Académico", date = today.toString(), priority = "Alta"))
        dao.save(Notice(title = "Mantenimiento del laboratorio B", description = "La revisión del equipo se realizará después del último turno.", category = "Importante", date = today.toString(), priority = "Alta"))
        dao.save(Notice(title = "Renovación de credenciales", description = "Presenta tu credencial anterior en servicios escolares.", category = "Administrativo", date = today.minusDays(1).toString(), priority = "Normal"))
        dao.save(Notice(title = "Convocatoria de fotografía", description = "Comparte una fotografía sobre la vida universitaria en el taller cultural.", category = "Cultural", date = today.minusDays(2).toString(), priority = "Normal"))
        dao.save(CampusEvent(title = "Encuentro de innovación", description = "Muestra de prototipos y proyectos de ingeniería de los equipos estudiantiles.", place = "Auditorio central", date = today.plusDays(2).toString(), time = "10:00"))
        dao.save(CampusEvent(title = "Laboratorio abierto", description = "Sesión práctica de diseño y fabricación de circuitos.", place = "B-102", date = today.plusDays(4).toString(), time = "12:30"))
        dao.save(CampusEvent(title = "Tarde de ensambles", description = "Presentación de los grupos de música del campus.", place = "Plaza cultural", date = today.plusDays(7).toString(), time = "16:00"))
    }
}
