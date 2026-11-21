package com.example.smartcampus

import com.example.smartcampus.data.local.*
import com.example.smartcampus.domain.*
import java.time.LocalDateTime
import org.junit.Assert.*
import org.junit.Test

class TimetableTest {
    private val monday = LocalDateTime.parse("2026-11-16T08:00")
    private val session = Session(1, "9IDGSB", "Dispositivos", "A-101", 1, 480, 570)
    private val second = session.copy(id = 2, start = 570, end = 660)

    @Test fun currentClassIncludesStartExcludesEnd() {
        assertEquals("Próxima", status(session, monday.minusMinutes(1)))
        assertEquals("En curso", status(session, monday))
        assertEquals("Finalizada", status(session, monday.plusMinutes(90)))
        assertEquals("En curso", status(second, monday.plusMinutes(90)))
    }

    @Test fun classStatusEnumValues() {
        assertEquals(ClassStatus.UPCOMING, classStatus(session, monday.minusMinutes(1)))
        assertEquals(ClassStatus.IN_PROGRESS, classStatus(session, monday))
        assertEquals(ClassStatus.FINISHED, classStatus(session, monday.plusMinutes(90)))
        assertEquals(ClassStatus.IN_PROGRESS, classStatus(second, monday.plusMinutes(90)))
    }

    @Test fun nextClassAccountsForWeeklyRecurrence() {
        val next = upcoming(listOf(session, second), monday)
        assertEquals(second, next.first().session)
        assertEquals(monday.toLocalDate().plusWeeks(1), next.last().date)
        assertEquals(monday.toLocalDate(), upcoming(listOf(session), monday.minusDays(2)).single().date)
    }

    @Test fun estimatedRoomAvailabilityUsesActualSessions() {
        assertFalse(available("A-101", listOf(session), monday))
        assertTrue(available("A-101", listOf(session), monday.plusMinutes(90)))
        assertTrue(available("B-201", listOf(session), monday))
        assertTrue(available("A-101", listOf(session), monday.plusDays(1)))
    }

    @Test fun filtersByGroupAndDayInChronologicalOrder() {
        val data = listOf(second, session.copy(day = 2), session.copy(groupCode = "Otro"), session)
        assertEquals(listOf(session, second), timetable(data, "9IDGSB", 1))
    }

    @Test fun filtersByStatusAndRange() {
        val sessions = listOf(session, second)
        val inProgressFilter = ScheduleFilter(statuses = setOf(ClassStatus.IN_PROGRESS))
        assertTrue(inProgressFilter.active)
        assertEquals(listOf(session), filterSessions(sessions, inProgressFilter, monday))

        val emptyFilter = ScheduleFilter()
        assertFalse(emptyFilter.active)
        val toggled = emptyFilter.toggle(ClassStatus.UPCOMING)
        assertTrue(toggled.active)
        assertEquals(setOf(ClassStatus.UPCOMING), toggled.statuses)

        val upcomingFilter = ScheduleFilter(statuses = setOf(ClassStatus.UPCOMING))
        assertEquals(listOf(second), filterSessions(sessions, upcomingFilter, monday))

        val rangeFilter = ScheduleFilter(from = 540, to = 660)
        assertEquals(listOf(session, second), filterSessions(sessions, rangeFilter, monday))

        val lateRange = ScheduleFilter(from = 570, to = 660)
        assertEquals(listOf(second), filterSessions(sessions, lateRange, monday))
    }

    @Test fun countdownFormatForTodaySessions() {
        assertEquals("Termina en 1 h 30 min", countdown(session, monday))
        assertEquals("Empieza en 30 min", countdown(session, monday.minusMinutes(30)))
        assertNull(countdown(session, monday.plusMinutes(90)))
        val tuesday = monday.plusDays(1)
        assertNull(countdown(session, tuesday))
    }

    @Test fun roomAvailabilityWithConsecutiveSessions() {
        val sessions = listOf(session, second)

        val busy = roomAvailability("A-101", sessions, monday)
        assertFalse(busy.available)
        assertEquals(660, busy.until)
        assertEquals("Ocupada hasta 11:00", busy.label())

        val freeBefore = roomAvailability("A-101", sessions, monday.minusHours(1))
        assertTrue(freeBefore.available)
        assertEquals(480, freeBefore.until)
        assertEquals("Libre hasta 08:00", freeBefore.label())

        val freeAfter = roomAvailability("A-101", sessions, monday.plusHours(3))
        assertTrue(freeAfter.available)
        assertNull(freeAfter.until)
        assertEquals("Libre", freeAfter.label())
    }

    @Test fun SundayAndEmptyListsDoNotFail() {
        val sunday = LocalDateTime.parse("2026-11-22T10:00")
        val empty: List<Session> = emptyList()

        assertTrue(timetable(empty, "9IDGSB", 7).isEmpty())
        assertTrue(filterSessions(empty, ScheduleFilter(), sunday).isEmpty())

        val availability = roomAvailability("A-101", empty, sunday)
        assertTrue(availability.available)
        assertNull(availability.until)
        assertEquals("Libre", availability.label())
        assertNull(countdown(session, sunday))
    }

    @Test fun filtersNoticesByCategory() {
        val notice = Notice(1,"Aviso","Contenido","Cultural","2026-11-17","Normal")
        val data = listOf(notice, notice.copy(id = 2, category = "Académico"))
        assertEquals(listOf(notice), notices(data,"Cultural"))
        assertEquals(data, notices(data,"Todas"))
        assertTrue(notices(data,"Importante").isEmpty())
    }

    @Test fun seedHasNoOverlappingRoomsTeachersOrGroups() {
        DemoData.sessions.groupBy { it.day }.values.forEach { day ->
            for (first in day) for (other in day.filter { it.id > first.id && it.start < first.end && first.start < it.end }) {
                assertNotEquals(first.groupCode, other.groupCode)
                assertNotEquals(first.room, other.room)
                assertNotEquals(DemoData.subjects.first { it.name == first.subject }.teacher, DemoData.subjects.first { it.name == other.subject }.teacher)
            }
        }
    }
}
