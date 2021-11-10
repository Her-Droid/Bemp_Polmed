package com.admin.bempadmin.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.admin.bempadmin.R
import com.admin.bempadmin.model.Admin
import com.admin.bempadmin.view.utils.OnClickAdminEventListener
import com.admin.bempadmin.view.viewholder.AdminViewHolder

class AdminAdapter(
    private val context: Context,
    private val onClickAdminEventListener: OnClickAdminEventListener
) : RecyclerView.Adapter<AdminViewHolder>() {
    lateinit var listAdmin: ArrayList<Admin>
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminViewHolder =
        AdminViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.list_admin, parent, false
            )
        )

    override fun onBindViewHolder(holder: AdminViewHolder, position: Int) {
        val admin = listAdmin[position]
        holder.binding.tvnama.text = admin.nama
        holder.binding.tvEmail.text = admin.email
        holder.binding.tvStatus.text = admin.status
        holder.binding.imgBtnHapus.setOnClickListener { onClickAdminEventListener.deleteDivisi(admin) }
        holder.binding.imgBtnUpdate.setOnClickListener {
            onClickAdminEventListener.updateDivisi(
                admin
            )
        }
    }

    override fun getItemCount(): Int = listAdmin.size
}