package com.admin.bempadmin.model.repository

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.admin.bempadmin.helper.KATEGORI
import com.admin.bempadmin.helper.TB_BERITA
import com.admin.bempadmin.model.Kategori
import com.google.firebase.FirebaseApp
import com.google.firebase.database.*

class KategoriBeritaRepository(context: Context) {
    private val databaseReference: DatabaseReference

    init {
        FirebaseApp.initializeApp(context)
        databaseReference =
            FirebaseDatabase.getInstance().reference.child(TB_BERITA).child(KATEGORI)
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

    fun checkNamaKategori(kategori: Kategori): MutableLiveData<Int> {
        val mutableLiveData = MutableLiveData<Int>()
        databaseReference.addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (value in snapshot.children) {
                        val kategoriValue = value.getValue(Kategori::class.java)
                        if (kategoriValue != null) {
                            if (kategori.nama == kategoriValue.nama) {
                                mutableLiveData.postValue(1)
                                break
                            } else {
                                mutableLiveData.postValue(0)
                            }
                        } else {
                            mutableLiveData.postValue(0)
                        }
                    }
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

    fun simpanKategori(kategori: Kategori): MutableLiveData<Boolean> {
        val mutableLiveData = MutableLiveData<Boolean>()
        databaseReference.child(kategori.id).setValue(kategori).addOnSuccessListener {
            mutableLiveData.postValue(true)
        }.addOnFailureListener {
            mutableLiveData.postValue(false)
        }
        return mutableLiveData
    }

    fun showKategori(): MutableLiveData<HashMap<String, Any>> {
        val mutableLiveData = MutableLiveData<HashMap<String, Any>>()
        val hashMap = HashMap<String, Any>()
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    hashMap["code"] = 200
                    val kategoriList = ArrayList<Kategori>()
                    for (value in snapshot.children) {
                        val divisi = value.getValue(Kategori::class.java)
                        divisi?.let { kategoriList.add(it) }
                    }
                    hashMap[KATEGORI] = kategoriList
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

    fun delete(kategori: Kategori): MutableLiveData<Boolean> {
        val mutableLiveData = MutableLiveData<Boolean>()
        databaseReference.child(kategori.id).removeValue().addOnSuccessListener {
            mutableLiveData.postValue(true)
        }.addOnFailureListener {
            mutableLiveData.postValue(false)
        }
        return mutableLiveData
    }
}