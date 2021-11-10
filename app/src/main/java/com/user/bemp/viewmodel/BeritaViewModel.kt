package com.user.bemp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.user.bemp.model.repository.BeritaRepository

class BeritaViewModel(private val beritaRepository: BeritaRepository): ViewModel() {

    fun showBerita(): LiveData<HashMap<String, Any>> {
        return beritaRepository.showBerita()
    }

}