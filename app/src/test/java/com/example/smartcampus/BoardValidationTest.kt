package com.example.smartcampus

import com.example.smartcampus.domain.isEventValid
import com.example.smartcampus.domain.isNoticeValid
import com.example.smartcampus.domain.isValidDate
import com.example.smartcampus.domain.isValidTime
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BoardValidationTest {
    @Test fun validatesIsoDatesStrictly() {
        assertTrue(isValidDate("2026-11-21"))
        assertTrue(isValidDate("2028-02-29"))
        assertFalse(isValidDate("2026-02-29"))
        assertFalse(isValidDate("2026-02-30"))
        assertFalse(isValidDate("2026-13-01"))
        assertFalse(isValidDate("2026-1-05"))
        assertFalse(isValidDate("21-11-2026"))
        assertFalse(isValidDate(""))
        assertFalse(isValidDate(" 2026-11-21"))
    }

    @Test fun validatesTwentyFourHourTimesStrictly() {
        assertTrue(isValidTime("00:00"))
        assertTrue(isValidTime("08:05"))
        assertTrue(isValidTime("10:00"))
        assertTrue(isValidTime("23:59"))
        assertFalse(isValidTime("24:00"))
        assertFalse(isValidTime("10:60"))
        assertFalse(isValidTime("9:00"))
        assertFalse(isValidTime("10:0"))
        assertFalse(isValidTime("ab:cd"))
        assertFalse(isValidTime(""))
    }

    @Test fun validatesNotices() {
        assertTrue(isNoticeValid("Aviso general", "Contenido del comunicado", "2026-11-21"))
        assertFalse(isNoticeValid("   ", "Contenido", "2026-11-21"))
        assertFalse(isNoticeValid("Aviso", "", "2026-11-21"))
        assertFalse(isNoticeValid("Aviso", "Contenido", "2026-02-30"))
    }

    @Test fun validatesEvents() {
        assertTrue(isEventValid("Feria UTC", "Muestra estudiantil", "Auditorio", "2026-11-25", "10:00"))
        assertFalse(isEventValid("", "Detalle", "Auditorio", "2026-11-25", "10:00"))
        assertFalse(isEventValid("Feria UTC", "   ", "Auditorio", "2026-11-25", "10:00"))
        assertFalse(isEventValid("Feria UTC", "Detalle", "", "2026-11-25", "10:00"))
        assertFalse(isEventValid("Feria UTC", "Detalle", "Auditorio", "2026-11-31", "10:00"))
        assertFalse(isEventValid("Feria UTC", "Detalle", "Auditorio", "2026-11-25", "9:30"))
    }
}
