package com.admin.bempadmin.model.repository

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.admin.bempadmin.helper.CAMPAIGN
import com.admin.bempadmin.helper.PENDAFTAR
import com.admin.bempadmin.helper.TB_LOMBA
import com.admin.bempadmin.helper.TB_USER
import com.admin.bempadmin.model.DaftarLomba
import com.admin.bempadmin.model.Foto
import com.admin.bempadmin.model.Lomba
import com.admin.bempadmin.model.User
import com.google.firebase.FirebaseApp
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class LombaRepository(context: Context) {
    private val databaseReference: DatabaseReference
    private val databaseReferenceUser: DatabaseReference
    private val storageReference: StorageReference

    init {
        FirebaseApp.initializeApp(context)
        databaseReference =
            FirebaseDatabase.getInstance().reference.child(TB_LOMBA)
        databaseReferenceUser =
            FirebaseDatabase.getInstance().reference.child(TB_USER)
        storageReference = FirebaseStorage.getInstance().reference.child("image").child(TB_LOMBA)
    }

    fun getMaxId(): MutableLiveData<Int> {
        val mutableLiveData = MutableLiveData<Int>()
        databaseReference.child(CAMPAIGN).limitToLast(1)
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
        val filepath =
            storageReference.child(foto.imageName + "." + foto.extension)
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

    fun save(lomba: Lomba): MutableLiveData<Int> {
        val mutableLiveData = MutableLiveData<Int>()
        databaseReference.child(CAMPAIGN).child(lomba.id).setValue(lomba)
            .addOnSuccessListener {
                mutableLiveData.postValue(1)
            }.addOnFailureListener {
                mutableLiveData.postValue(0)
            }
        return mutableLiveData
    }

    fun showLomba(): MutableLiveData<HashMap<String, Any>> {
        val mutableLiveData = MutableLiveData<HashMap<String, Any>>()
        val hashMap = HashMap<String, Any>()
        databaseReference.child(CAMPAIGN).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    hashMap["code"] = 200
                    val lombaList = ArrayList<Lomba>()
                    for (value in snapshot.children) {
                        val lomba = value.getValue(Lomba::class.java)
                        lomba?.let { lombaList.add(it) }
                    }
                    hashMap[TB_LOMBA] = lombaList
                } else {
                    hashMap["code"] = 404
                }
                mutableLiveData.postValue(hashMap)
            }

            override fun onCancelled(error: DatabaseError) {
                hashMap["code"] = 403
                mutableLiveData.postValue(hashMap)
            }
        })

        return mutableLiveData
    }

    fun deleteImage(lomba: Lomba): MutableLiveData<Boolean> {
        val mutableLiveData = MutableLiveData<Boolean>()
        FirebaseStorage.getInstance().getReferenceFromUrl(lomba.imageUrl).delete()
            .addOnSuccessListener {
                mutableLiveData.postValue(true)
            }.addOnFailureListener {
                mutableLiveData.postValue(false)
            }
        return mutableLiveData
    }

    fun delete(lomba: Lomba): MutableLiveData<Boolean> {
        val mutableLiveData = MutableLiveData<Boolean>()
        databaseReference.child(CAMPAIGN).child(lomba.id).removeValue().addOnSuccessListener {
            FirebaseStorage.getInstance().getReferenceFromUrl(lomba.imageUrl).delete()
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

    fun showPeserta(lomba: Lomba): MutableLiveData<HashMap<String, Any>> {
        val mutableLiveData = MutableLiveData<HashMap<String, Any>>()
        val hashMap = HashMap<String, Any>()
        databaseReferenceUser.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val hashMapUser = HashMap<String, String>()
                    for (admin in snapshot.children) {
                        val userValue = admin.getValue(User::class.java)
                        if (userValue != null) {
                            hashMapUser[userValue.id] =
                                userValue.namaDepan + " " + userValue.namaBelakang
                        }
                    }

                    databaseReference.child(PENDAFTAR)
                        .addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.exists()) {
                                    val listPeserta = ArrayList<String>()
                                    for (value in snapshot.children) {
                                        val daftarLomba = value.getValue(DaftarLomba::class.java)
                                        if (daftarLomba != null) {
                                            hashMap["code"] = 200
                                            if (lomba.id == daftarLomba.idLomba) {
                                                val namaPeserta =
                                                    hashMapUser[daftarLomba.idUser].toString()
                                                listPeserta.add(namaPeserta)
                                            }
                                        } else {
                                            hashMap["code"] = 404
                                        }
                                    }
                                    hashMap[PENDAFTAR] = listPeserta
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
        return mutableLiveData
    }
}