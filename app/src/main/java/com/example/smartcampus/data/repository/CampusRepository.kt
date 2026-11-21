package com.example.smartcampus.data.repository

import androidx.room.withTransaction
import com.example.smartcampus.data.local.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import java.time.LocalDate
import java.time.LocalDateTime

data class Academic(val groups: List<CampusGroup>, val subjects: List<Subject>, val rooms: List<Classroom>, val sessions: List<Session>)
data class Campus(val academic: Academic, val notices: List<Notice>, val events: List<CampusEvent>)

class CampusRepository(private val db: CampusDatabase) {
    val dao = db.dao()
    val clock: Flow<LocalDateTime> = flow {
        while (true) {
            val now = LocalDateTime.now()
            emit(now)
            val delayMillis = (60 - now.second) * 1000L - (now.nano / 1_000_000L)
            delay(if (delayMillis > 0) delayMillis else 1000L)
        }
    }
    private val academic = combine(dao.groups(), dao.subjects(), dao.rooms(), dao.sessions(), ::Academic)
    val campus = combine(academic, dao.notices(), dao.events(), ::Campus)

    suspend fun initialize() = db.withTransaction {
        if (dao.groupCount() == 0) {
            dao.insertGroups(DemoData.groups)
            dao.insertSubjects(DemoData.subjects)
            dao.insertRooms(DemoData.rooms)
            dao.insertSessions(DemoData.sessions)
            DemoData.board(dao, LocalDate.now())
        }
    }
    suspend fun restore() = db.withTransaction {
        dao.clearNotices()
        dao.clearEvents()
        DemoData.board(dao, LocalDate.now())
    }
}
