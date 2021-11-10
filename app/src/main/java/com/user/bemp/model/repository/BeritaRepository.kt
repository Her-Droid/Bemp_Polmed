package com.user.bemp.model.repository

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.google.firebase.FirebaseApp
import com.google.firebase.database.*
import com.user.bemp.helper.ISIBERITA
import com.user.bemp.helper.KATEGORI
import com.user.bemp.helper.TB_ADMIN
import com.user.bemp.helper.TB_BERITA
import com.user.bemp.model.Admin
import com.user.bemp.model.Berita
import com.user.bemp.model.Kategori

class BeritaRepository(context: Context) {

    private val databaseReference: DatabaseReference
    private val databaseReferenceKategori: DatabaseReference
    private val databaseReferenceAdmin: DatabaseReference

    init {
        FirebaseApp.initializeApp(context)
        databaseReferenceKategori =
            FirebaseDatabase.getInstance().reference.child(TB_BERITA).child(KATEGORI)
        databaseReferenceAdmin = FirebaseDatabase.getInstance().reference.child(TB_ADMIN)
        databaseReference =
            FirebaseDatabase.getInstance().reference.child(TB_BERITA).child(ISIBERITA)
    }

    fun showBerita(): MutableLiveData<HashMap<String, Any>> {
        val mutableLiveData = MutableLiveData<HashMap<String, Any>>()
        val hashMap = HashMap<String, Any>()

        databaseReferenceKategori.addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val hashMapKategori = HashMap<String, String>()
                    for (kategori in snapshot.children) {
                        val kategoriValue = kategori.getValue(Kategori::class.java)
                        if (kategoriValue != null) {
                            hashMapKategori[kategoriValue.id] = kategoriValue.nama
                        }
                    }
                    databaseReferenceAdmin.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {
                                val hashMapAdmin = HashMap<String, String>()
                                for (admin in snapshot.children) {
                                    val adminValue = admin.getValue(Admin::class.java)
                                    if (adminValue != null) {
                                        hashMapAdmin[adminValue.id] =
                                            adminValue.namaDepan + adminValue.namaBelakang
                                    }
                                }
                                databaseReference.addValueEventListener(object :
                                    ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        if (snapshot.exists()) {
                                            hashMap["code"] = 200
                                            val beritaList = ArrayList<Berita>()
                                            for (value in snapshot.children) {
                                                val berita = value.getValue(Berita::class.java)
                                                if (berita != null) {

                                                    //get kategori
                                                    var namaKategori =
                                                        hashMapKategori[berita.idKategori].toString()
                                                    if (namaKategori == "null") {
                                                        namaKategori = "-"
                                                    }

                                                    //getAdmin
                                                    var namaAdmin =
                                                        hashMapAdmin[berita.postingAdmin].toString()
                                                    if (namaAdmin == "null") {
                                                        namaAdmin = "-"
                                                    }

                                                    //get edit admin
                                                    var namaAdminEdit =
                                                        hashMapAdmin[berita.postingUpdateAdmin].toString()
                                                    if (namaAdminEdit == "null") {
                                                        namaAdminEdit = "-"
                                                    }

                                                    beritaList.add(
                                                        Berita(
                                                            berita.id,
                                                            berita.judul,
                                                            berita.imageUrl,
                                                            berita.isi,
                                                            berita.date,
                                                            namaKategori,
                                                            namaAdmin,
                                                            namaAdminEdit
                                                        )
                                                    )
                                                }
                                            }
                                            hashMap[ISIBERITA] = beritaList
                                        } else {
                                            hashMap["code"] = 404
                                        }
                                        mutableLiveData.postValue(hashMap)
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                    }
                                })
                            } else {
                                hashMap["code"] = 404
                                mutableLiveData.postValue(hashMap)
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                        }
                    })

                } else {
                    hashMap["code"] = 404
                    mutableLiveData.postValue(hashMap)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
        return mutableLiveData
    }

}