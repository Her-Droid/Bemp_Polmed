package com.admin.bempadmin.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Anggota(
    val id: String = "",
    val nama: String = "",
    val idDivisi: String = "",
    val motto: String = "",
    var imageUrl: String = "",
    var idAdmin: String = ""
) : Parcelable
