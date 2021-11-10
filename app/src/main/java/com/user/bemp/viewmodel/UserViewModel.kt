package com.user.bemp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.user.bemp.model.FotoProfil
import com.user.bemp.model.User
import com.user.bemp.model.repository.UserRepository

class UserViewModel(private val userRepository: UserRepository) : ViewModel() {

    lateinit var user: User
    lateinit var fotoProfil: FotoProfil

    fun registerUser(): LiveData<Boolean> {
        return userRepository.registerUser(user)
    }

    fun getMaxId(): LiveData<Int> {
        return userRepository.getMaxId()
    }

    fun checkNim(): LiveData<Int> {
        return userRepository.checkNim(user)
    }

    fun loginUser(): LiveData<HashMap<String, Any>> {
        return userRepository.loginUser(user)
    }

    fun ubahPassword(): LiveData<Boolean> {
        return userRepository.ubahPassword(user)
    }

    fun uploadFoto(): LiveData<String> {
        return userRepository.uploadFoto(fotoProfil)
    }

    fun deleteImage(): MutableLiveData<Boolean> {
        return userRepository.deleteImage(user)
    }

    fun updateFotoUrl(): MutableLiveData<Boolean> {
        return userRepository.updateFotoUrl(user)
    }
}