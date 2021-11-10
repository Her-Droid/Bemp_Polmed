package com.user.bemp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Berita(
    val id: String = "",
    val judul: String = "",
    val imageUrl: String = "",
    val isi: String = "",
    val date: String = "",
    val idKategori: String = "",
    val postingAdmin: String = "",
    val postingUpdateAdmin: String = ""
) : Parcelable
