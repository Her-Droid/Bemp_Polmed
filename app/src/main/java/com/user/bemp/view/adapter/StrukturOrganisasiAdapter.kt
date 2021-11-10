package com.user.bemp.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.user.bemp.R
import com.user.bemp.model.StrukturOrganisasi
import com.user.bemp.view.viewholder.StrukturOrganisasiViewHolder

class StrukturOrganisasiAdapter(private val context: Context) :
    RecyclerView.Adapter<StrukturOrganisasiViewHolder>() {
    lateinit var listAnggota: ArrayList<StrukturOrganisasi>
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): StrukturOrganisasiViewHolder = StrukturOrganisasiViewHolder(
        LayoutInflater.from(context).inflate(
            R.layout.list_struktur_organisasi, parent, false
        )
    )

    override fun onBindViewHolder(holder: StrukturOrganisasiViewHolder, position: Int) {
        val anggota = listAnggota[position]
        Glide.with(context)
            .load(anggota.imageUrl)
            .into(holder.binding.imgFoto)
        holder.binding.tvNama.text = anggota.nama
        holder.binding.tvDivisi.text = anggota.idDivisi
        holder.binding.tvBio.text = anggota.motto
    }

    override fun getItemCount(): Int = listAnggota.size
}