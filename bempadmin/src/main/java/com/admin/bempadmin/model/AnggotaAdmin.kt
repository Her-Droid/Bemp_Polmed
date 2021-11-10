package com.admin.bempadmin.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AnggotaAdmin(
    val id: String = "",
    val nama: String = "",
    val email: String = "",
    val idDivisi: String = "",
    val motto: String = "",
    var imageUrl: String = "",
    var idAdmin: String = "",
    var statusAdmin: String = "",
    var password: String = ""
) : Parcelable
