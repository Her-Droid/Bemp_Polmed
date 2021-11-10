package com.admin.bempadmin.model.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.admin.bempadmin.helper.*
import com.admin.bempadmin.model.*
import com.google.firebase.FirebaseApp
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class BeritaRepository(context: Context) {
    private val databaseReference: DatabaseReference
    private val databaseReferenceKategori: DatabaseReference
    private val databaseReferenceAdmin: DatabaseReference
    private val storageReference: StorageReference

    init {
        FirebaseApp.initializeApp(context)
        databaseReferenceKategori =
            FirebaseDatabase.getInstance().reference.child(TB_BERITA).child(KATEGORI)
        databaseReferenceAdmin = FirebaseDatabase.getInstance().reference.child(TB_ADMIN)
        databaseReference =
            FirebaseDatabase.getInstance().reference.child(TB_BERITA).child(ISIBERITA)
        storageReference = FirebaseStorage.getInstance().reference.child("image").child(ISIBERITA)
    }

    fun getMaxIdBerita(): MutableLiveData<Int> {
        val mutableLiveData = MutableLiveData<Int>()
        databaseReference.limitToLast(1)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val value: DataSnapshot = snapshot.children.elementAt(0)
                        val maxId = value.child("id").value.toString().toInt()
                        mutableLiveData.postValue(maxId)
                    } else {
                        mutableLiveData.postValue(0)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    mutableLiveData.postValue(-1)
                }
            })
        return mutableLiveData
    }

    fun uploadFoto(foto: Foto): MutableLiveData<String> {
        val mutableLiveData = MutableLiveData<String>()
        val filepath = storageReference.child(foto.imageName + "." + foto.extension)
        filepath.putFile(foto.uri)
            .addOnSuccessListener {
                filepath.downloadUrl.addOnSuccessListener {
                    mutableLiveData.postValue(it.toString())

                }.addOnFailureListener {
                    mutableLiveData.postValue("0")
                }

            }.addOnFailureListener {
                mutableLiveData.postValue("0")
            }
        return mutableLiveData
    }

    fun saveBerita(berita: Berita): MutableLiveData<Int> {
        val mutableLiveData = MutableLiveData<Int>()
        databaseReference.child(berita.id).setValue(berita)
            .addOnSuccessListener {
                mutableLiveData.postValue(1)
            }.addOnFailureListener {
                mutableLiveData.postValue(0)
            }
        return mutableLiveData
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
                                        hashMapAdmin[adminValue.id] = adminValue.nama
                                    }
                                }
                                databaseReference.addValueEventListener(object :
                                    ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        if (snapshot.exists()) {
                                            hashMap["code"] = 200
                                            val beritaList = ArrayList<BeritaModel>()
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
                                                    var namaAdminEdit = hashMapAdmin[berita.postingUpdateAdmin].toString()
                                                    if (namaAdminEdit =="null"){
                                                        namaAdminEdit = "-"
                                                    }

                                                    beritaList.add(
                                                        BeritaModel(
                                                            berita.id,
                                                            berita.judul,
                                                            berita.imageUrl,
                                                            berita.isi,
                                                            berita.date,
                                                            namaKategori,
                                                            berita.postingAdmin,
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

    fun deleteImage(berita: Berita): MutableLiveData<Boolean> {
        val mutableLiveData = MutableLiveData<Boolean>()
        FirebaseStorage.getInstance().getReferenceFromUrl(berita.imageUrl).delete().addOnSuccessListener {
            mutableLiveData.postValue(true)
        }.addOnFailureListener {
            mutableLiveData.postValue(false)
        }
        return mutableLiveData
    }

    fun delete(berita: Berita): MutableLiveData<Boolean> {
        val mutableLiveData = MutableLiveData<Boolean>()
        databaseReference.child(berita.id).removeValue().addOnSuccessListener {
            FirebaseStorage.getInstance().getReferenceFromUrl(berita.imageUrl).delete().addOnSuccessListener {
                mutableLiveData.postValue(true)
            }.addOnFailureListener {
                mutableLiveData.postValue(false)
            }
        }.addOnFailureListener {
            mutableLiveData.postValue(false)
        }
        return mutableLiveData
    }
}