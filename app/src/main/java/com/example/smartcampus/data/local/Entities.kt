package com.example.smartcampus.data.local

import androidx.room.*

@Entity data class CampusGroup(@PrimaryKey val code: String)
@Entity data class Subject(@PrimaryKey val name: String, val teacher: String)
@Entity data class Classroom(@PrimaryKey val name: String, val building: String, val floor: String, val capacity: Int, val description: String)
@Entity(foreignKeys = [
    ForeignKey(entity = CampusGroup::class, parentColumns = ["code"], childColumns = ["groupCode"]),
    ForeignKey(entity = Subject::class, parentColumns = ["name"], childColumns = ["subject"]),
    ForeignKey(entity = Classroom::class, parentColumns = ["name"], childColumns = ["room"])
], indices = [Index("groupCode"), Index("subject"), Index("room")])
data class Session(@PrimaryKey val id: Int, val groupCode: String, val subject: String, val room: String, val day: Int, val start: Int, val end: Int)

@Entity data class Notice(@PrimaryKey(autoGenerate = true) val id: Long = 0, val title: String, val description: String, val category: String, val date: String, val priority: String)
@Entity data class CampusEvent(@PrimaryKey(autoGenerate = true) val id: Long = 0, val title: String, val description: String, val place: String, val date: String, val time: String)

