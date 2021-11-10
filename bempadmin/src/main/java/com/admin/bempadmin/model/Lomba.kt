package com.admin.bempadmin.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Lomba(
    val id: String = "",
    val judul: String = "",
    val deskripsi: String = "",
    val imageUrl: String = ""
) : Parcelable
