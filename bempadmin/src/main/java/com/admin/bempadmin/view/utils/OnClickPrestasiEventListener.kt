package com.admin.bempadmin.view.utils

import com.admin.bempadmin.model.Prestasi

interface OnClickPrestasiEventListener {
    fun updatePrestasi(prestasi: Prestasi)
    fun deletePrestasi(prestasi: Prestasi)
}