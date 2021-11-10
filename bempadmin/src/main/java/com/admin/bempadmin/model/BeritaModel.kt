package com.admin.bempadmin.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class BeritaModel(
    val id: String = "",
    val judul: String = "",
    val imageUrl: String = "",
    val isi: String = "",
    val date: String = "",
    val idKategori: String = "",
    val idAdmin: String = "",
    val namaAdmin: String = "",
    val namaAdminEdit: String = ""
) : Parcelable
