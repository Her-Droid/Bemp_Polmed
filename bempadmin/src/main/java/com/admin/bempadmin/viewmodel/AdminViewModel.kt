package com.admin.bempadmin.viewmodel

import androidx.lifecycle.*
import com.admin.bempadmin.model.Admin
import com.admin.bempadmin.model.repository.AdminRepository

class AdminViewModel(
    private val adminRepository: AdminRepository
) : ViewModel() {

    lateinit var admin: Admin

    fun registerAdmin(): LiveData<Boolean> {
        return adminRepository.registerAdminAnggota(admin)
    }

    fun getMaxId(): LiveData<Int> {
        return adminRepository.getMaxId()
    }

    fun checkEmail(): LiveData<Int> {
        return adminRepository.checkEmail(admin)
    }

    fun loginAdmin(): LiveData<HashMap<String, Any>> {
        return  adminRepository.loginAdmin(admin)
    }

    fun ubahPassword(): LiveData<Boolean> {
        return adminRepository.ubahPassword(admin)
    }

    fun deleteAdmin(): LiveData<Boolean> {
        return adminRepository.delete(admin)
    }

    fun updateStatusAdmin(): LiveData<Boolean> {
        return adminRepository.updateStatusAdmin(admin)
    }

    fun showAdmin(): LiveData<HashMap<String, Any>> {
        return adminRepository.showAdmin()
    }
}