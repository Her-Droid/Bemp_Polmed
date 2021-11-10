package com.admin.bempadmin.view.utils

import com.admin.bempadmin.model.Berita
import com.admin.bempadmin.model.BeritaModel

interface OnClickBeritaEventListener {
    fun updateBerita(beritaModel: BeritaModel)
    fun deleteBerita(beritaModel: BeritaModel)
}