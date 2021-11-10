package com.user.bemp.model.repository

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.google.firebase.FirebaseApp
import com.google.firebase.database.*
import com.user.bemp.helper.TB_PRESTASI
import com.user.bemp.model.Prestasi

class PrestasiRepository(context: Context) {

    private val databaseReference: DatabaseReference

    init {
        FirebaseApp.initializeApp(context)
        databaseReference =
            FirebaseDatabase.getInstance().reference.child(TB_PRESTASI)
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