package com.admin.bempadmin.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.admin.bempadmin.model.Foto
import com.admin.bempadmin.model.Lomba
import com.admin.bempadmin.model.repository.LombaRepository

class LombaViewModel(private val lombaRepository: LombaRepository): ViewModel() {
    lateinit var lomba: Lomba
    lateinit var foto: Foto

    fun getMaxId(): LiveData<Int> {
        return lombaRepository.getMaxId()
    }

    fun uploadFoto(): LiveData<String> {
        return lombaRepository.uploadFoto(foto)
    }

    fun saveLomba(): LiveData<Int> {
        return lombaRepository.save(lomba)
    }

    fun showLomba(): LiveData<HashMap<String, Any>> {
        return lombaRepository.showLomba()
    }

    fun deleteImage(): LiveData<Boolean> {
        return lombaRepository.deleteImage(lomba)
    }

    fun delete(): LiveData<Boolean> {
        return lombaRepository.delete(lomba)
    }

    fun showPeserta():LiveData<HashMap<String, Any>>{
        return lombaRepository.showPeserta(lomba)
    }
}
