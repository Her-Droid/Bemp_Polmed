package com.user.bemp.model

import android.net.Uri

data class FotoProfil(
    val imageName: String = "",
    val extension: String = "",
    val uri: Uri = Uri.EMPTY
)
