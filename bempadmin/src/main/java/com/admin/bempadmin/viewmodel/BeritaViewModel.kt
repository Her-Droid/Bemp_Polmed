package com.admin.bempadmin.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.admin.bempadmin.model.Berita
import com.admin.bempadmin.model.Foto
import com.admin.bempadmin.model.repository.BeritaRepository

class BeritaViewModel(private val beritaRepository: BeritaRepository):ViewModel() {

    lateinit var berita: Berita
    lateinit var foto: Foto

    fun getMaxId(): LiveData<Int> {
        return beritaRepository.getMaxIdBerita()
    }
    fun uploadFoto(): LiveData<String> {
        return beritaRepository.uploadFoto(foto)
    }

    fun saveBerita(): LiveData<Int> {
        return beritaRepository.saveBerita(berita)
    }

    fun showBerita(): LiveData<HashMap<String, Any>> {
        return beritaRepository.showBerita()
    }

    fun deleteImage() : LiveData<Boolean>{
        return beritaRepository.deleteImage(berita)
    }

    fun delete(): LiveData<Boolean> {
        return beritaRepository.delete(berita)
    }
}