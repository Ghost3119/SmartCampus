package com.example.smartcampus.di

import android.app.Application
import androidx.room.Room
import com.example.smartcampus.data.local.CampusDatabase
import com.example.smartcampus.data.repository.*

class CampusApplication : Application() {
    val repository by lazy {
        CampusRepository(Room.databaseBuilder(this, CampusDatabase::class.java, "campus.db").build())
    }
    val preferences by lazy { Preferences(this) }
}
