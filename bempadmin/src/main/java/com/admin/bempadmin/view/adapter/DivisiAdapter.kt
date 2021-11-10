package com.admin.bempadmin.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.admin.bempadmin.R
import com.admin.bempadmin.helper.SUPER_ADMIN
import com.admin.bempadmin.model.Divisi
import com.admin.bempadmin.view.utils.OnClickDivisiEventListener
import com.admin.bempadmin.view.viewholder.DivisiKategoriViewHolder

class DivisiAdapter(
    private val context: Context,
    private val onClickDivisiEventListener: OnClickDivisiEventListener,
    private val superAdmin: String
) :
    RecyclerView.Adapter<DivisiKategoriViewHolder>() {
    var divisiList = ArrayList<Divisi>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DivisiKategoriViewHolder =
        DivisiKategoriViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.list_divisi_kategori, parent, false
            )
        )

    override fun onBindViewHolder(holderKategori: DivisiKategoriViewHolder, position: Int) {
        holderKategori.binding.tvDivisi.text = divisiList[position].nama

        if (superAdmin == SUPER_ADMIN) {
            holderKategori.binding.imgBtnHapus.visibility = View.VISIBLE
        } else {
            holderKategori.binding.imgBtnHapus.visibility = View.GONE
        }

        holderKategori.binding.imgBtnUpdate.setOnClickListener {
            onClickDivisiEventListener.updateDivisi(
                divisiList[position]
            )
        }
        holderKategori.binding.imgBtnHapus.setOnClickListener {
            onClickDivisiEventListener.deleteDivisi(
                divisiList[position]
            )
        }
    }

    override fun getItemCount(): Int = divisiList.size
}