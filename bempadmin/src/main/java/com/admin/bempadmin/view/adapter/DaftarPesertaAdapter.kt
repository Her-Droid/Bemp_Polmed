package com.admin.bempadmin.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.admin.bempadmin.R
import com.admin.bempadmin.view.viewholder.DaftarPesertaViewHolder

class DaftarPesertaAdapter(private val context: Context):
    RecyclerView.Adapter<DaftarPesertaViewHolder>() {
    lateinit var listPeserta: ArrayList<String>
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DaftarPesertaViewHolder = DaftarPesertaViewHolder(LayoutInflater.from(context).inflate(
        R.layout.list_peserta_lomba, parent, false))

    override fun onBindViewHolder(holder: DaftarPesertaViewHolder, position: Int) {
        holder.binding.tvPeserta.text = listPeserta[position]
    }

    override fun getItemCount(): Int = listPeserta.size
}