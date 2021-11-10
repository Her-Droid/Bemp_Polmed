package com.admin.bempadmin.view.utils

import com.admin.bempadmin.model.Divisi

interface OnClickDivisiEventListener {
    fun updateDivisi(divisi: Divisi)
    fun deleteDivisi(divisi: Divisi)
}