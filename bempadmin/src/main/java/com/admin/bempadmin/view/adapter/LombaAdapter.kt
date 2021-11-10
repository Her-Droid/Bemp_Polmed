package com.admin.bempadmin.view.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.admin.bempadmin.R
import com.admin.bempadmin.helper.CAMPAIGN
import com.admin.bempadmin.helper.SUPER_ADMIN
import com.admin.bempadmin.model.Lomba
import com.admin.bempadmin.view.activity.DetailLombaActivity
import com.admin.bempadmin.view.utils.OnClickLombaEventListener
import com.admin.bempadmin.view.viewholder.LombaViewHolder
import com.bumptech.glide.Glide

class LombaAdapter(
    private val context: Context,
    private val onClickLombaEventListener: OnClickLombaEventListener,
    private val superAdmin: String
) :
    RecyclerView.Adapter<LombaViewHolder>() {
    lateinit var listLomba: ArrayList<Lomba>
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LombaViewHolder =
        LombaViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.list_lomba, parent, false
            )
        )

    override fun onBindViewHolder(holder: LombaViewHolder, position: Int) {
        val lomba = listLomba[position]
        holder.binding.tvJudulLomba.text = lomba.judul
        holder.binding.tvDeskripsi.text = lomba.deskripsi
        Glide.with(context)
            .load(lomba.imageUrl)
            .into(holder.binding.imgFoto)

        if (superAdmin == SUPER_ADMIN) {
            holder.binding.imgBtnHapus.visibility = View.VISIBLE
        } else {
            holder.binding.imgBtnHapus.visibility = View.GONE
        }
        holder.binding.imgBtnHapus.setOnClickListener { onClickLombaEventListener.deleteLomba(lomba) }
        holder.binding.imgBtnUpdate.setOnClickListener { onClickLombaEventListener.updateLomba(lomba) }
        holder.binding.tvReadMore.setOnClickListener {
            val intent = Intent(context, DetailLombaActivity::class.java)
            intent.putExtra(CAMPAIGN, lomba)
            context.startActivity(intent)
        }
        holder.itemView.setOnClickListener {
            val intent = Intent(context, DetailLombaActivity::class.java)
            intent.putExtra(CAMPAIGN, lomba)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = listLomba.size
}