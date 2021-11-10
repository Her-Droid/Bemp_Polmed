package com.user.bemp.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.user.bemp.R
import com.user.bemp.model.Prestasi
import com.user.bemp.view.viewholder.PrestasiViewHolder

class PrestasiAdapter(private val context: Context): RecyclerView.Adapter<PrestasiViewHolder>() {
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
    }

    override fun getItemCount(): Int = listPrestasi.size
}