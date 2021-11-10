package com.admin.bempadmin.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.admin.bempadmin.model.Foto
import com.admin.bempadmin.model.Prestasi
import com.admin.bempadmin.model.repository.PrestasiRepository

class PrestasiViewModel(private val prestasiRepository: PrestasiRepository): ViewModel() {
    lateinit var prestasi: Prestasi
    lateinit var foto: Foto
    lateinit var search: String

    fun getMaxId(): LiveData<Int> {
        return prestasiRepository.getMaxId()
    }

    fun uploadFoto(): LiveData<String> {
        return prestasiRepository.uploadFoto(prestasi, foto)
    }

    fun savePrestasi(): LiveData<Int> {
        return prestasiRepository.save(prestasi)
    }

    fun showPrestasi(): LiveData<HashMap<String, Any>> {
        return prestasiRepository.showPrestasi()
    }

    fun deleteImage(): LiveData<Boolean> {
        return prestasiRepository.deleteImage(prestasi)
    }

    fun delete(): LiveData<Boolean> {
        return prestasiRepository.delete(prestasi)
    }

    fun searchPrestasi(): LiveData<HashMap<String, Any>> {
        return prestasiRepository.searchPrestasi(search)
    }

}