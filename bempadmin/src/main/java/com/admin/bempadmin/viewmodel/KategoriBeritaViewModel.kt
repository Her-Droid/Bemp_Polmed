package com.admin.bempadmin.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.admin.bempadmin.model.Kategori
import com.admin.bempadmin.model.repository.KategoriBeritaRepository

class KategoriBeritaViewModel(private val kategoriBeritaRepository: KategoriBeritaRepository): ViewModel() {
    lateinit var kategori: Kategori

    fun simpanDivisi(): LiveData<Boolean> {
        return kategoriBeritaRepository.simpanKategori(kategori)
    }

    fun getMaxId(): LiveData<Int> {
        return kategoriBeritaRepository.getMaxId()
    }

    fun checkNamaDivisi(): LiveData<Int> {
        return kategoriBeritaRepository.checkNamaKategori(kategori)
    }

    fun showDivisi(): LiveData<HashMap<String, Any>> {
        return  kategoriBeritaRepository.showKategori()
    }

    fun delete(): MutableLiveData<Boolean> {
        return kategoriBeritaRepository.delete(kategori)
    }
}