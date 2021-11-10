package com.admin.bempadmin.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.admin.bempadmin.model.repository.*

class FirebaseViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AdminViewModel::class.java) -> {
                AdminViewModel(AdminRepository(context)) as T
            }
            modelClass.isAssignableFrom(DivisiViewModel::class.java) -> {
                DivisiViewModel(DivisiRepository(context)) as T
            }
            modelClass.isAssignableFrom(BioOrganisasiViewModel::class.java) -> {
                BioOrganisasiViewModel(BioOrganisasiRepository(context)) as T
            }
            modelClass.isAssignableFrom(AnggotaOrganisasiViewModel::class.java) -> {
                AnggotaOrganisasiViewModel(AnggotaOrganisasiRepository(context)) as T
            }
            modelClass.isAssignableFrom(KategoriBeritaViewModel::class.java) -> {
                KategoriBeritaViewModel(KategoriBeritaRepository(context)) as T
            }
            modelClass.isAssignableFrom(BeritaViewModel::class.java) -> {
                BeritaViewModel(BeritaRepository(context)) as T
            }
            modelClass.isAssignableFrom(PrestasiViewModel::class.java) -> {
                PrestasiViewModel(PrestasiRepository(context)) as T
            }
            modelClass.isAssignableFrom(LombaViewModel::class.java) -> {
                LombaViewModel(LombaRepository(context)) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}