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
fun status(s: Session, now: LocalDateTime): String {
    val day = now.dayOfWeek.value
    val minute = now.hour * 60 + now.minute
    return when {
        s.day < day || (s.day == day && minute >= s.end) -> "Finalizada"
        s.day == day && minute >= s.start -> "En curso"
        else -> "Próxima"
    }
}
fun available(room: String, sessions: List<Session>, now: LocalDateTime) =
    sessions.none { it.room == room && status(it, now) == "En curso" }

data class Occurrence(val session: Session, val date: LocalDate)
fun upcoming(sessions: List<Session>, now: LocalDateTime): List<Occurrence> =
    sessions.map {
        var date = now.toLocalDate().plusDays(((it.day - now.dayOfWeek.value + 7) % 7).toLong())
        if (!date.atStartOfDay().plusMinutes(it.start.toLong()).isAfter(now)) date = date.plusWeeks(1)
        Occurrence(it, date)
    }.sortedWith(compareBy({ it.date }, { it.session.start }))

fun upcomingEvents(items: List<CampusEvent>, now: LocalDateTime): List<CampusEvent> {
    val threshold = now.withSecond(0).withNano(0)
    return items.filter { event ->
        runCatching {
            !LocalDateTime.parse("${event.date}T${event.time}").isBefore(threshold)
        }.getOrDefault(false)
    }
}

