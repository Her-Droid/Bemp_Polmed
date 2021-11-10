package com.user.bemp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.user.bemp.model.repository.PrestasiRepository

class PrestasiViewModel(private val prestasiRepository: PrestasiRepository): ViewModel() {

    lateinit var search: String

    fun showPrestasi(): LiveData<HashMap<String, Any>> {
        return prestasiRepository.showPrestasi()
    }

    fun searchPrestasi(): LiveData<HashMap<String, Any>> {
        return prestasiRepository.searchPrestasi(search)
    }

}