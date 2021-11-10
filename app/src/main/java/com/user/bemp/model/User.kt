package com.user.bemp.model

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
data class User(
    val id: String = "",
    val nim: String = "",
    val namaDepan: String = "",
    val namaBelakang: String = "",
    val prodi: String = "",
    val email: String = "",
    val password: String = "",
    val imageUrl: String = ""
)