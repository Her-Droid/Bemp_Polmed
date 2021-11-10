package com.admin.bempadmin.model

import android.net.Uri

data class Foto(
    val imageName: String = "",
    val extension: String = "",
    val uri: Uri = Uri.EMPTY
)