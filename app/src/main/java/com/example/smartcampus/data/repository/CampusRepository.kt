package com.example.smartcampus.data.repository

import androidx.room.withTransaction
import com.example.smartcampus.data.local.*
import kotlinx.coroutines.flow.combine
import java.time.LocalDate

data class Academic(val groups: List<CampusGroup>, val subjects: List<Subject>, val rooms: List<Classroom>, val sessions: List<Session>)
data class Campus(val academic: Academic, val notices: List<Notice>, val events: List<CampusEvent>)

class CampusRepository(private val db: CampusDatabase) {
    val dao = db.dao()
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
