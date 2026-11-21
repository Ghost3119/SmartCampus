package com.example.smartcampus.domain

import com.example.smartcampus.data.local.*
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.Locale

val spanish = Locale.forLanguageTag("es-MX")
fun LocalDate.label(pattern: String) = format(DateTimeFormatter.ofPattern(pattern, spanish))
fun clock(minutes: Int) = "%02d:%02d".format(Locale.ROOT, minutes / 60, minutes % 60)
val categories = listOf("Académico", "Administrativo", "Cultural", "Importante")
fun timetable(rows: List<Session>, group: String, day: Int) =
    rows.filter { it.groupCode == group && it.day == day }.sortedBy { it.start }
fun notices(rows: List<Notice>, category: String) = rows.filter { category == "Todas" || it.category == category }

enum class ClassStatus(val label: String) {
    UPCOMING("Próxima"),
    IN_PROGRESS("En curso"),
    FINISHED("Finalizada")
}

fun classStatus(s: Session, now: LocalDateTime): ClassStatus {
    val day = now.dayOfWeek.value
    val minute = now.hour * 60 + now.minute
    return when {
        s.day < day || (s.day == day && minute >= s.end) -> ClassStatus.FINISHED
        s.day == day && minute >= s.start -> ClassStatus.IN_PROGRESS
        else -> ClassStatus.UPCOMING
    }
}

fun status(s: Session, now: LocalDateTime): String = classStatus(s, now).label

data class ScheduleFilter(val statuses: Set<ClassStatus> = emptySet(), val from: Int? = null, val to: Int? = null) {
    val active: Boolean get() = statuses.isNotEmpty() || from != null || to != null
    fun toggle(status: ClassStatus) = copy(statuses = if (status in statuses) statuses - status else statuses + status)
}

fun filterSessions(rows: List<Session>, filter: ScheduleFilter, now: LocalDateTime): List<Session> {
    return rows.filter { s ->
        val matchesStatus = filter.statuses.isEmpty() || classStatus(s, now) in filter.statuses
        val matchesFrom = filter.from == null || s.end > filter.from
        val matchesTo = filter.to == null || s.start < filter.to
        matchesStatus && matchesFrom && matchesTo
    }
}

fun countdown(session: Session, now: LocalDateTime): String? {
    if (session.day != now.dayOfWeek.value) return null
    val minute = now.hour * 60 + now.minute
    return when (classStatus(session, now)) {
        ClassStatus.IN_PROGRESS -> "Termina en ${formatDuration(session.end - minute)}"
        ClassStatus.UPCOMING -> "Empieza en ${formatDuration(session.start - minute)}"
        ClassStatus.FINISHED -> null
    }
}

private fun formatDuration(diff: Int): String {
    val h = diff / 60
    val m = diff % 60
    return when {
        h > 0 && m > 0 -> "$h h $m min"
        h > 0 -> "$h h"
        else -> "$m min"
    }
}

data class RoomAvailability(val available: Boolean, val until: Int? = null) {
    fun label(): String = when {
        !available && until != null -> "Ocupada hasta ${clock(until)}"
        !available -> "Ocupada"
        until != null -> "Libre hasta ${clock(until)}"
        else -> "Libre"
    }
}

fun roomAvailability(room: String, sessions: List<Session>, now: LocalDateTime): RoomAvailability {
    val day = now.dayOfWeek.value
    val minute = now.hour * 60 + now.minute
    val roomSessions = sessions.filter { it.room == room && it.day == day }.sortedBy { it.start }
    val current = roomSessions.find { minute >= it.start && minute < it.end }
    return if (current != null) {
        var end = current.end
        while (true) {
            val next = roomSessions.find { it.start <= end && it.end > end } ?: break
            end = next.end
        }
        RoomAvailability(available = false, until = end)
    } else {
        val next = roomSessions.find { it.start > minute }
        RoomAvailability(available = true, until = next?.start)
    }
}

fun available(room: String, sessions: List<Session>, now: LocalDateTime): Boolean =
    roomAvailability(room, sessions, now).available

data class Occurrence(val session: Session, val date: LocalDate)
fun upcoming(sessions: List<Session>, now: LocalDateTime): List<Occurrence> =
    sessions.map {
        var date = now.toLocalDate().plusDays(((it.day - now.dayOfWeek.value + 7) % 7).toLong())
        if (!date.atStartOfDay().plusMinutes(it.start.toLong()).isAfter(now)) date = date.plusWeeks(1)
        Occurrence(it, date)
    }.sortedWith(compareBy({ it.date }, { it.session.start }))

fun upcomingEvents(items: List<CampusEvent>, now: LocalDateTime) =
    items.filter { !LocalDateTime.parse(it.date + "T" + it.time).isBefore(now.withSecond(0).withNano(0)) }
