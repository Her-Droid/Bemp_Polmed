package com.admin.bempadmin.view.utils

import com.admin.bempadmin.model.Lomba


interface OnClickLombaEventListener {
    fun updateLomba(lomba: Lomba)
    fun deleteLomba(lomba: Lomba)
}