package com.bignerdranch.android.criminalintent.Database

import androidx.room.TypeConverter
import java.util.*

class CrimeTypeConverter {

    @TypeConverter
    fun fromDate(date: Date): Long? {
        return date?.time
    }

    @TypeConverter
    fun toDate(millissSinceEponch: Long?): Date? {
        return millissSinceEponch?.let {
            Date(it)
        }
    }

    @TypeConverter
    fun fromUUID(uuid: UUID?): String? {
        return uuid.toString()
    }

    @TypeConverter
    fun toUUID(uuid: String?): UUID? {
        return UUID.fromString(uuid)
    }
}