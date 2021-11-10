package com.admin.bempadmin.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.admin.bempadmin.model.repository.BioOrganisasiRepository

class BioOrganisasiViewModel(private val bioOrganisasiRepository: BioOrganisasiRepository) :
    ViewModel() {

    fun showBioStrukturOrgnisasi(): LiveData<String> {
        return bioOrganisasiRepository.showBioStrukturOrgnisasi()
    }

    fun saveBio(bio: String): LiveData<Int> {
        return bioOrganisasiRepository.saveBio(bio)
    }


}