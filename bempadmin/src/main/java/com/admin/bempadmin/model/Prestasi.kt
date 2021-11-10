package com.admin.bempadmin.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Prestasi(
    val id: String = "",
    val nim: String = "",
    val nama: String = "",
    val prestasi: String = "",
    val imageUrl: String = ""
) : Parcelable
