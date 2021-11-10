package com.admin.bempadmin.model.repository

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.admin.bempadmin.helper.ANGGOTA
import com.admin.bempadmin.helper.DIVISI
import com.admin.bempadmin.helper.TB_ADMIN
import com.admin.bempadmin.helper.TB_STRUKTUR_ORGANISASI
import com.admin.bempadmin.model.*
import com.google.firebase.FirebaseApp
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class AnggotaOrganisasiRepository(context: Context) {

    private val databaseReference: DatabaseReference
    private val databaseReferenceDivisi: DatabaseReference
    private val storageReference: StorageReference

    init {
        FirebaseApp.initializeApp(context)
        databaseReferenceDivisi =
            FirebaseDatabase.getInstance().reference.child(TB_STRUKTUR_ORGANISASI).child(DIVISI)
        databaseReference =
            FirebaseDatabase.getInstance().reference.child(TB_STRUKTUR_ORGANISASI).child(ANGGOTA)
        storageReference = FirebaseStorage.getInstance().reference.child("image").child(ANGGOTA)
    }

    fun getMaxIdAnggota(): MutableLiveData<Int> {
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

    fun saveAnggota(anggota: Anggota): MutableLiveData<Int> {
        val mutableLiveData = MutableLiveData<Int>()
        databaseReference.child(anggota.id).setValue(anggota)
            .addOnSuccessListener {
                mutableLiveData.postValue(1)
            }.addOnFailureListener {
                mutableLiveData.postValue(0)
            }
        return mutableLiveData
    }

    fun showAnggota(): MutableLiveData<HashMap<String, Any>> {
        val mutableLiveData = MutableLiveData<HashMap<String, Any>>()
        val hashMap = HashMap<String, Any>()

        databaseReferenceDivisi.addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val hashMapDivisi = HashMap<String, String>()
                    for (divisi in snapshot.children) {
                        val divisiValue = divisi.getValue(Divisi::class.java)
                        if (divisiValue != null) {
                            hashMapDivisi[divisiValue.id] = divisiValue.nama
                        }
                    }

                    FirebaseDatabase.getInstance().reference.child(TB_ADMIN).addValueEventListener(
                        object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.exists()) {
                                    val hashMapStatusAdmin = HashMap<String, String>()
                                    val hashMapEmail = HashMap<String, String>()
                                    val hashMapPassword = HashMap<String, String>()
                                    for (admin in snapshot.children) {
                                        val adminValue = admin.getValue(Admin::class.java)
                                        if (adminValue != null) {
                                            hashMapStatusAdmin[adminValue.id] = adminValue.status
                                            hashMapEmail[adminValue.id] = adminValue.email
                                            hashMapPassword[adminValue.id] = adminValue.password
                                        }
                                    }

                                    databaseReference.addValueEventListener(object : ValueEventListener {
                                        override fun onDataChange(snapshot: DataSnapshot) {
                                            if (snapshot.exists()) {
                                                hashMap["code"] = 200
                                                val anggotaList = ArrayList<AnggotaAdmin>()
                                                for (value in snapshot.children) {
                                                    val anggota = value.getValue(Anggota::class.java)
                                                    if (anggota != null) {
                                                        var namaDivisi = hashMapDivisi[anggota.idDivisi].toString()
                                                        if (namaDivisi == "null") {
                                                            namaDivisi = "-"
                                                        }

                                                        var statusAdmin = hashMapStatusAdmin[anggota.idAdmin].toString()
                                                        if (statusAdmin == "null"){
                                                            statusAdmin = ""
                                                        }

                                                        var password = hashMapPassword[anggota.idAdmin].toString()
                                                        if (password == "null"){
                                                            password = ""
                                                        }

                                                        var email = hashMapEmail[anggota.idAdmin].toString()
                                                        if (email == "null") {
                                                            email = ""
                                                        }

                                                        anggotaList.add(
                                                            AnggotaAdmin(
                                                                anggota.id,
                                                                anggota.nama,
                                                                email,
                                                                namaDivisi,
                                                                anggota.motto,
                                                                anggota.imageUrl,
                                                                anggota.idAdmin,
                                                                statusAdmin,
                                                                password
                                                            )
                                                        )
                                                    }
                                                }
                                                hashMap[ANGGOTA] = anggotaList
                                            } else {
                                                hashMap["code"] = 404
                                            }
                                            mutableLiveData.postValue(hashMap)
                                        }

                                        override fun onCancelled(error: DatabaseError) {
                                        }
                                    })
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

    fun deleteImage(anggota: Anggota): MutableLiveData<Boolean> {
        val mutableLiveData = MutableLiveData<Boolean>()
        FirebaseStorage.getInstance().getReferenceFromUrl(anggota.imageUrl).delete()
            .addOnSuccessListener {
                mutableLiveData.postValue(true)
            }.addOnFailureListener {
                mutableLiveData.postValue(false)
            }
        return mutableLiveData
    }

    fun delete(anggota: Anggota): MutableLiveData<Boolean> {
        val mutableLiveData = MutableLiveData<Boolean>()
        databaseReference.child(anggota.id).removeValue().addOnSuccessListener {
            FirebaseStorage.getInstance().getReferenceFromUrl(anggota.imageUrl).delete()
                .addOnSuccessListener {
                    mutableLiveData.postValue(true)
                }.addOnFailureListener {
                    mutableLiveData.postValue(false)
                }
        }.addOnFailureListener {
            mutableLiveData.postValue(false)
        }
        return mutableLiveData
    }

    fun updateIdAdmin(anggota: Anggota): MutableLiveData<Boolean> {
        val mutableLiveData = MutableLiveData<Boolean>()
        databaseReference.child(anggota.id).child("idAdmin").setValue(anggota.idAdmin)
            .addOnSuccessListener {
                mutableLiveData.postValue(true)
            }.addOnFailureListener {
            mutableLiveData.postValue(false)
        }
        return mutableLiveData
    }
}