package com.admin.bempadmin.model.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.admin.bempadmin.helper.TB_PRESTASI
import com.admin.bempadmin.model.Foto
import com.admin.bempadmin.model.Prestasi
import com.google.firebase.FirebaseApp
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class PrestasiRepository(context: Context) {

    private val databaseReference: DatabaseReference
    private val storageReference: StorageReference

    init {
        FirebaseApp.initializeApp(context)
        databaseReference =
            FirebaseDatabase.getInstance().reference.child(TB_PRESTASI)
        storageReference = FirebaseStorage.getInstance().reference.child("image").child(TB_PRESTASI)
    }

    fun getMaxId(): MutableLiveData<Int> {
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

    fun uploadFoto(prestasi: Prestasi, foto: Foto): MutableLiveData<String> {
        val mutableLiveData = MutableLiveData<String>()
        val filepath =
            storageReference.child(prestasi.nim + "_" + prestasi.nama + "_" + foto.imageName + "." + foto.extension)
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

    fun save(prestasi: Prestasi): MutableLiveData<Int> {
        val mutableLiveData = MutableLiveData<Int>()
        databaseReference.child(prestasi.id).setValue(prestasi)
            .addOnSuccessListener {
                mutableLiveData.postValue(1)
            }.addOnFailureListener {
                mutableLiveData.postValue(0)
            }
        return mutableLiveData
    }

    fun showPrestasi(): MutableLiveData<HashMap<String, Any>> {
        val mutableLiveData = MutableLiveData<HashMap<String, Any>>()
        val hashMap = HashMap<String, Any>()
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    hashMap["code"] = 200
                    val prestasiList = ArrayList<Prestasi>()
                    for (value in snapshot.children) {
                        val prestasi = value.getValue(Prestasi::class.java)
                        prestasi?.let { prestasiList.add(it) }
                    }
                    hashMap[TB_PRESTASI] = prestasiList
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

    fun deleteImage(prestasi: Prestasi): MutableLiveData<Boolean> {
        val mutableLiveData = MutableLiveData<Boolean>()
        FirebaseStorage.getInstance().getReferenceFromUrl(prestasi.imageUrl).delete()
            .addOnSuccessListener {
                mutableLiveData.postValue(true)
            }.addOnFailureListener {
            mutableLiveData.postValue(false)
        }
        return mutableLiveData
    }

    fun delete(prestasi: Prestasi): MutableLiveData<Boolean> {
        val mutableLiveData = MutableLiveData<Boolean>()
        databaseReference.child(prestasi.id).removeValue().addOnSuccessListener {
            FirebaseStorage.getInstance().getReferenceFromUrl(prestasi.imageUrl).delete()
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

    fun searchPrestasi(search: String): MutableLiveData<HashMap<String, Any>> {
        val mutableLiveData = MutableLiveData<HashMap<String, Any>>()
        val hashMap = HashMap<String, Any>()
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    hashMap["code"] = 200
                    val prestasiList = ArrayList<Prestasi>()
                    for (value in snapshot.children) {
                        val prestasi = value.getValue(Prestasi::class.java)
                        prestasi?.let {
                            if (prestasi.nama.lowercase().contains(search.lowercase())) {
                                prestasiList.add(it)
                            }
                        }
                    }
                    hashMap[TB_PRESTASI] = prestasiList
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

}