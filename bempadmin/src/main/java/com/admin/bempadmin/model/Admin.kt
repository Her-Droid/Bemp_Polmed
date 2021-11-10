package com.admin.bempadmin.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Admin(
    val id: String = "",
    val idAnggota: String = "",
    val nama: String = "",
    val email: String = "",
    val password: String = "",
    val status: String = ""
) : Parcelable