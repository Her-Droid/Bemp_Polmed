package com.admin.bempadmin.view.utils
import com.admin.bempadmin.model.AnggotaAdmin

interface OnClickAnggotaEventListener {
    fun updateDivisi(anggotaAdmin: AnggotaAdmin)
    fun deleteDivisi(anggotaAdmin: AnggotaAdmin)
}