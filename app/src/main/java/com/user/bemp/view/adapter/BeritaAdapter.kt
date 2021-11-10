package com.user.bemp.view.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.user.bemp.R
import com.user.bemp.helper.ISIBERITA
import com.user.bemp.model.Berita
import com.user.bemp.view.activity.DetailBeritaActivity
import com.user.bemp.view.viewholder.BeritaViewHolder

class BeritaAdapter(private val context: Context) : RecyclerView.Adapter<BeritaViewHolder>() {
    var listBerita = ArrayList<Berita>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BeritaViewHolder =
        BeritaViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.list_berita, parent, false
            )
        )

    override fun onBindViewHolder(holder: BeritaViewHolder, position: Int) {
        val beritaModel = listBerita[position]
        holder.binding.tvTitle.text = beritaModel.judul
        holder.binding.tvKategori.text = beritaModel.idKategori
        holder.binding.tvIsiBerita.text = beritaModel.isi
        holder.binding.tvReadMore.setOnClickListener{
            val intent = Intent(context, DetailBeritaActivity::class.java)
            intent.putExtra(ISIBERITA, beritaModel)
            context.startActivity(intent)
        }
        holder.itemView.setOnClickListener{
            val intent = Intent(context, DetailBeritaActivity::class.java)
            intent.putExtra(ISIBERITA, beritaModel)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = listBerita.size
}