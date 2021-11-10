package com.user.bemp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.user.bemp.model.DaftarLomba
import com.user.bemp.model.repository.LombaRepository

class LombaViewModel(private val lombaRepository: LombaRepository) : ViewModel() {

    lateinit var daftarLomba: DaftarLomba

    fun showLomba(): LiveData<HashMap<String, Any>> {
        return lombaRepository.showLomba()
    }

    fun checkDaftarLomba(): LiveData<Boolean> {
        return  lombaRepository.checkDaftarLomba(daftarLomba)
    }

    fun getMaxId(): LiveData<Int> {
        return lombaRepository.getMaxId()
    }

    fun saveDaftarLomba(): LiveData<Boolean> {
        return lombaRepository.saveDaftarLomba(daftarLomba)
    }

}