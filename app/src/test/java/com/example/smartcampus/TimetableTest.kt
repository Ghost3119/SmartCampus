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
