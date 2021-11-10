package com.user.bemp.view.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.user.bemp.R
import com.user.bemp.helper.CAMPAIGN
import com.user.bemp.model.Lomba
import com.user.bemp.view.activity.DetailLombaActivity
import com.user.bemp.view.viewholder.LombaViewHolder

class LombaAdapter (
    private val context: Context
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

        holder.itemView.setOnClickListener{
            val intent = Intent(context, DetailLombaActivity::class.java)
            intent.putExtra(CAMPAIGN, lomba)
            context.startActivity(intent)
        }
        holder.binding.tvReadMore.setOnClickListener{
            val intent = Intent(context, DetailLombaActivity::class.java)
            intent.putExtra(CAMPAIGN, lomba)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = listLomba.size
}