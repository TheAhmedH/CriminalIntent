package com.bignerdranch.android.criminalintent

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.bignerdranch.android.criminalintent.Database.CrimeRepository
import java.util.*

class CrimeDetailViewModel : ViewModel() {
    private val crimeRepository = CrimeRepository.get()
    private val crimeIDLiveData = MutableLiveData<UUID>()

    var crimeLiveData: LiveData<Crime> =
        Transformations.switchMap(crimeIDLiveData) { crimeIDLiveData ->
            crimeRepository.getCrime(crimeIDLiveData)
        }

    fun loadCrime(id: UUID) {
        crimeIDLiveData.value = id
    }

    fun saveCrime(crime: Crime){
        crimeRepository.updateCrime(crime)
    }
}