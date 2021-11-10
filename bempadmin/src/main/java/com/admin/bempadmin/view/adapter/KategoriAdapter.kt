package com.admin.bempadmin.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.admin.bempadmin.R
import com.admin.bempadmin.helper.SUPER_ADMIN
import com.admin.bempadmin.model.Kategori
import com.admin.bempadmin.view.utils.OnClickKategoriEventListener
import com.admin.bempadmin.view.viewholder.DivisiKategoriViewHolder

class KategoriAdapter(
    private val context: Context,
    private val onClickKategoriEventListener: OnClickKategoriEventListener,
    private val superAdmin: String
) : RecyclerView.Adapter<DivisiKategoriViewHolder>() {
    lateinit var listKategori: ArrayList<Kategori>
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DivisiKategoriViewHolder =
        DivisiKategoriViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.list_divisi_kategori, parent, false
            )
        )

    override fun onBindViewHolder(holder: DivisiKategoriViewHolder, position: Int) {
        holder.binding.tvDivisi.text = listKategori[position].nama

        if (superAdmin == SUPER_ADMIN) {
            holder.binding.imgBtnHapus.visibility = View.VISIBLE
        } else {
            holder.binding.imgBtnHapus.visibility = View.GONE
        }

        holder.binding.imgBtnUpdate.setOnClickListener {
            onClickKategoriEventListener.updateKategori(
                listKategori[position]
            )
        }
        holder.binding.imgBtnHapus.setOnClickListener {
            onClickKategoriEventListener.updateKategori(
                listKategori[position]
            )
        }
    }

    override fun getItemCount(): Int = listKategori.size
}