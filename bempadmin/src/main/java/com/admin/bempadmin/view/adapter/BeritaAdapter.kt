package com.admin.bempadmin.view.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.admin.bempadmin.R
import com.admin.bempadmin.helper.ISIBERITA
import com.admin.bempadmin.helper.SUPER_ADMIN
import com.admin.bempadmin.model.BeritaModel
import com.admin.bempadmin.view.activity.DetailBeritaActivity
import com.admin.bempadmin.view.utils.OnClickBeritaEventListener
import com.admin.bempadmin.view.viewholder.BeritaViewHolder


class BeritaAdapter(
    private val context: Context,
    private val onClickBeritaEventListener: OnClickBeritaEventListener,
    private val superAdmin: String
) :
    RecyclerView.Adapter<BeritaViewHolder>() {
    lateinit var listBerita: ArrayList<BeritaModel>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BeritaViewHolder = BeritaViewHolder(LayoutInflater.from(context).inflate(
        R.layout.list_berita, parent, false))

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

        if (superAdmin == SUPER_ADMIN){
            holder.binding.imgBtnHapus.visibility = View.VISIBLE
        } else {
            holder.binding.imgBtnHapus.visibility = View.GONE
        }
        holder.binding.imgBtnHapus.setOnClickListener{onClickBeritaEventListener.deleteBerita(beritaModel)}
        holder.binding.imgBtnUpdate.setOnClickListener{onClickBeritaEventListener.updateBerita(beritaModel)}
    }

    override fun getItemCount(): Int {
      return listBerita.size
    }
}