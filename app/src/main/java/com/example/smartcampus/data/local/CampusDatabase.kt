package com.example.smartcampus.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao interface CampusDao {
    @Query("SELECT * FROM CampusGroup ORDER BY code") fun groups(): Flow<List<CampusGroup>>
    @Query("SELECT COUNT(*) FROM CampusGroup") suspend fun groupCount(): Int
    @Query("SELECT * FROM Subject ORDER BY name") fun subjects(): Flow<List<Subject>>
    @Query("SELECT * FROM Classroom ORDER BY name") fun rooms(): Flow<List<Classroom>>
    @Query("SELECT * FROM Session ORDER BY day,start") fun sessions(): Flow<List<Session>>
    @Query("SELECT * FROM Notice ORDER BY date DESC,id DESC") fun notices(): Flow<List<Notice>>
    @Query("SELECT * FROM CampusEvent ORDER BY date,time,id") fun events(): Flow<List<CampusEvent>>
    @Insert suspend fun insertGroups(items: List<CampusGroup>)
    @Insert suspend fun insertSubjects(items: List<Subject>)
    @Insert suspend fun insertRooms(items: List<Classroom>)
    @Insert suspend fun insertSessions(items: List<Session>)
    @Upsert suspend fun save(item: Notice)
    @Upsert suspend fun save(item: CampusEvent)
    @Delete suspend fun delete(item: Notice)
    @Delete suspend fun delete(item: CampusEvent)
    @Query("DELETE FROM Notice") suspend fun clearNotices()
    @Query("DELETE FROM CampusEvent") suspend fun clearEvents()
}

@Database(entities = [CampusGroup::class, Subject::class, Classroom::class, Session::class, Notice::class, CampusEvent::class], version = 1, exportSchema = false)
abstract class CampusDatabase : RoomDatabase() { abstract fun dao(): CampusDao }

