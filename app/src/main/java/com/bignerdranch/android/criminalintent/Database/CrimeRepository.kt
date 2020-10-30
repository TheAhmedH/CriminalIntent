package com.bignerdranch.android.criminalintent.Database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase
import com.bignerdranch.android.criminalintent.Crime
import java.util.*
import java.util.concurrent.Executor
import java.util.concurrent.Executors

private const val DATABASE_NAME = "crime-database"

class CrimeRepository private constructor(context: Context) {

    private val database: CrimeDatabase = Room.databaseBuilder(
        context.applicationContext,
        CrimeDatabase::class.java,
        DATABASE_NAME
    ).build()

//    private val database: CrimeDatabase = databaseBuilder(context.applicationContext, CrimeDatabase::class.java, DATABASE_NAME)
//            .fallbackToDestructiveMigration()
//            .allowMainThreadQueries()
//            .build()

    private val crimeDao = database.crimeDao()
    private val executor = Executors.newSingleThreadExecutor()

    fun getCrimes(): LiveData<List<Crime>> = crimeDao.getCrimes()

    fun getCrime(id: UUID): LiveData<Crime?> = crimeDao.getCrime(id)

    // Update crime via background thread
    fun updateCrime(crime: Crime) {
        executor.execute {
            crimeDao.updateCrime(crime)
        }
    }

    //Insert crime via background thread
    fun insertCrime(crime: Crime) {
        executor.execute {
            crimeDao.insertCrime(crime)
        }
    }


    companion object {
        private var INSTANCE: CrimeRepository? = null

        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = CrimeRepository(context)
            }
        }

        fun get(): CrimeRepository {
            return INSTANCE ?: throw IllegalStateException("Crime Repository must be initialized")
        }
    }
}