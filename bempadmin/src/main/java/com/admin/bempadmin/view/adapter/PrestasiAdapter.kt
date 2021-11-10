package com.admin.bempadmin.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.admin.bempadmin.R
import com.admin.bempadmin.helper.SUPER_ADMIN
import com.admin.bempadmin.model.Prestasi
import com.admin.bempadmin.view.utils.OnClickPrestasiEventListener
import com.admin.bempadmin.view.viewholder.PrestasiViewHolder
import com.bumptech.glide.Glide

class PrestasiAdapter(
    private val context: Context,
    private val onClickPrestasiEventListener: OnClickPrestasiEventListener,
    private val superAdmin: String
) : RecyclerView.Adapter<PrestasiViewHolder>() {
    lateinit var listPrestasi: ArrayList<Prestasi>
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrestasiViewHolder =
        PrestasiViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.list_prestasi, parent, false
            )
        )

    override fun onBindViewHolder(holder: PrestasiViewHolder, position: Int) {
        val prestasi = listPrestasi[position]
        Glide.with(context)
            .load(prestasi.imageUrl)
            .into(holder.binding.imgFoto)
        holder.binding.tvNim.text = prestasi.nim
        holder.binding.tvNama.text = prestasi.nama
        holder.binding.tvPrestasi.text = prestasi.prestasi

        if (superAdmin == SUPER_ADMIN) {
            holder.binding.imgBtnHapus.visibility = View.VISIBLE
        } else {
            holder.binding.imgBtnHapus.visibility = View.GONE
        }

        holder.binding.imgBtnHapus.setOnClickListener {
            onClickPrestasiEventListener.deletePrestasi(
                prestasi
            )
        }
        holder.binding.imgBtnUpdate.setOnClickListener {
            onClickPrestasiEventListener.updatePrestasi(
                prestasi
            )
        }
    }

    override fun getItemCount(): Int = listPrestasi.size
}