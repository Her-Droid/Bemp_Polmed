package com.admin.bempadmin.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.admin.bempadmin.R
import com.admin.bempadmin.helper.SUPER_ADMIN
import com.admin.bempadmin.model.Anggota
import com.admin.bempadmin.model.AnggotaAdmin
import com.admin.bempadmin.view.utils.OnClickAnggotaEventListener
import com.admin.bempadmin.view.viewholder.AnggotaOrganisasiViewHolder
import com.bumptech.glide.Glide

class AnggotaOrganisasiAdapter(
    private val context: Context,
    private val onClickAnggotaEventListener: OnClickAnggotaEventListener,
    private val superAdmin: String
) :
    RecyclerView.Adapter<AnggotaOrganisasiViewHolder>() {
    var listAnggota = ArrayList<AnggotaAdmin>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnggotaOrganisasiViewHolder =
        AnggotaOrganisasiViewHolder(
            LayoutInflater.from(context).inflate(R.layout.list_anggota, parent, false)
        )

    override fun onBindViewHolder(holder: AnggotaOrganisasiViewHolder, position: Int) {
        val anggota = listAnggota[position]
        Glide.with(context)
            .load(anggota.imageUrl)
            .into(holder.binding.imgFoto)
        holder.binding.tvNama.text = anggota.nama
        holder.binding.tvDivisi.text = anggota.idDivisi
        holder.binding.tvBio.text = anggota.motto

        if (superAdmin == SUPER_ADMIN) {
            holder.binding.imgBtnHapus.visibility = View.VISIBLE
        } else {
            holder.binding.imgBtnHapus.visibility = View.GONE
        }

        holder.binding.imgBtnHapus.setOnClickListener {
            onClickAnggotaEventListener.deleteDivisi(
                anggota
            )
        }
        holder.binding.imgBtnUpdate.setOnClickListener {
            onClickAnggotaEventListener.updateDivisi(
                anggota
            )
        }
    }

    override fun getItemCount(): Int = listAnggota.size
}