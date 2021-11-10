package com.admin.bempadmin.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.admin.bempadmin.model.Anggota
import com.admin.bempadmin.model.Foto
import com.admin.bempadmin.model.repository.AnggotaOrganisasiRepository

class AnggotaOrganisasiViewModel(private val anggotaOrganisasiRepository: AnggotaOrganisasiRepository) :
    ViewModel() {

    lateinit var anggota: Anggota
    lateinit var foto: Foto
    fun getMaxId(): LiveData<Int> {
        return anggotaOrganisasiRepository.getMaxIdAnggota()
    }

    fun uploadFoto(): LiveData<String> {
        return anggotaOrganisasiRepository.uploadFoto(foto)
    }

    fun saveAnggota(): LiveData<Int> {
        return anggotaOrganisasiRepository.saveAnggota(anggota)
    }

    fun showAnggota(): LiveData<HashMap<String, Any>> {
        return anggotaOrganisasiRepository.showAnggota()
    }

    fun deleteImage(): LiveData<Boolean> {
        return anggotaOrganisasiRepository.deleteImage(anggota)
    }

    fun delete(): LiveData<Boolean> {
        return anggotaOrganisasiRepository.delete(anggota)
    }

    fun updateIdAdmin(): LiveData<Boolean>{
        return anggotaOrganisasiRepository.updateIdAdmin(anggota)
    }
}