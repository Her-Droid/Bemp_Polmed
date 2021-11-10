package com.user.bemp.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.user.bemp.model.repository.*

/**
 * ViewModel provider factory to instantiate LoginViewModel.
 * Required given LoginViewModel has a non-empty constructor
 */
class FirebaseViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(UserViewModel::class.java) -> {
                UserViewModel(UserRepository(context)) as T
            }
            modelClass.isAssignableFrom(StrukturOrganisasiViewModel::class.java) -> {
                StrukturOrganisasiViewModel(StrukturOrganisasiRepository(context)) as T
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