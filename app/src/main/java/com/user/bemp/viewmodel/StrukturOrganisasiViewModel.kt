package com.user.bemp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.user.bemp.model.repository.StrukturOrganisasiRepository

class StrukturOrganisasiViewModel(private val strukturOrganisasiRepository: StrukturOrganisasiRepository): ViewModel() {

    fun showBioStrukturOrgnisasi(): LiveData<String> {
        return strukturOrganisasiRepository.showBioStrukturOrgnisasi()
    }

    fun showAnggota(): LiveData<HashMap<String, Any>> {
        return strukturOrganisasiRepository.showAnggota()
    }

}