package com.admin.bempadmin.model.repository

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.admin.bempadmin.helper.DIVISI
import com.admin.bempadmin.helper.TB_STRUKTUR_ORGANISASI
import com.admin.bempadmin.model.Divisi
import com.google.firebase.FirebaseApp
import com.google.firebase.database.*

class DivisiRepository(context: Context) {
    private val databaseReference: DatabaseReference

    init {
        FirebaseApp.initializeApp(context)
        databaseReference =
            FirebaseDatabase.getInstance().reference.child(TB_STRUKTUR_ORGANISASI).child(DIVISI)
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

    fun checkNamaDivisi(divisi: Divisi): MutableLiveData<Int> {
        val mutableLiveData = MutableLiveData<Int>()
        databaseReference.addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (value in snapshot.children) {
                        val divisiValue = value.getValue(Divisi::class.java)
                        if (divisiValue != null) {
                            if (divisi.nama == divisiValue.nama) {
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

    fun simpanDivisi(divisi: Divisi): MutableLiveData<Boolean> {
        val mutableLiveData = MutableLiveData<Boolean>()
        databaseReference.child(divisi.id).setValue(divisi).addOnSuccessListener {
            mutableLiveData.postValue(true)
        }.addOnFailureListener {
            mutableLiveData.postValue(false)
        }
        return mutableLiveData
    }

    fun showDivisi(): MutableLiveData<HashMap<String, Any>> {
        val mutableLiveData = MutableLiveData<HashMap<String, Any>>()
        val hashMap = HashMap<String, Any>()
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    hashMap["code"] = 200
                    val divisiList = ArrayList<Divisi>()
                    for (value in snapshot.children) {
                        val divisi = value.getValue(Divisi::class.java)
                        divisi?.let { divisiList.add(it) }
                    }
                    hashMap[DIVISI] = divisiList
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

    fun delete(divisi: Divisi): MutableLiveData<Boolean> {
        val mutableLiveData = MutableLiveData<Boolean>()
        databaseReference.child(divisi.id).removeValue().addOnSuccessListener {
            mutableLiveData.postValue(true)
        }.addOnFailureListener {
            mutableLiveData.postValue(false)
        }
        return mutableLiveData
    }
}