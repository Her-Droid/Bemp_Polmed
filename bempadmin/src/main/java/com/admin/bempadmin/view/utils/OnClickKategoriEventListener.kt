package com.admin.bempadmin.view.utils

import com.admin.bempadmin.model.Kategori

interface OnClickKategoriEventListener {
    fun updateKategori(kategori: Kategori)
    fun deleteKategori(kategori: Kategori)
}