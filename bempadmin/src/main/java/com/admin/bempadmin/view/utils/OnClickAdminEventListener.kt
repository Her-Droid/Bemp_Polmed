package com.admin.bempadmin.view.utils

import com.admin.bempadmin.model.Admin

interface OnClickAdminEventListener {
    fun updateDivisi(admin: Admin)
    fun deleteDivisi(admin: Admin)
}