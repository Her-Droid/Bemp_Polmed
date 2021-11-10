package com.admin.bempadmin.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.admin.bempadmin.model.Divisi
import com.admin.bempadmin.model.repository.DivisiRepository

class DivisiViewModel(private val divisiRepository: DivisiRepository) : ViewModel() {
    lateinit var divisi: Divisi

    fun simpanDivisi(): LiveData<Boolean> {
        return divisiRepository.simpanDivisi(divisi)
    }

    fun getMaxId(): LiveData<Int> {
        return divisiRepository.getMaxId()
    }

    fun checkNamaDivisi(): LiveData<Int> {
        return divisiRepository.checkNamaDivisi(divisi)
    }

    fun showDivisi(): LiveData<HashMap<String, Any>>{
        return  divisiRepository.showDivisi()
    }

    fun delete(): MutableLiveData<Boolean> {
        return divisiRepository.delete(divisi)
    }
}