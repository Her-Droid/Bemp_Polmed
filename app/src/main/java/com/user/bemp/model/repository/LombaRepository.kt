package com.user.bemp.model.repository

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.google.firebase.FirebaseApp
import com.google.firebase.database.*
import com.user.bemp.helper.CAMPAIGN
import com.user.bemp.helper.PENDAFTAR
import com.user.bemp.helper.TB_LOMBA
import com.user.bemp.model.DaftarLomba
import com.user.bemp.model.Lomba

class LombaRepository(context: Context) {
    private val databaseReference: DatabaseReference

    init {
        FirebaseApp.initializeApp(context)
        databaseReference =
            FirebaseDatabase.getInstance().reference.child(TB_LOMBA)
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

    fun checkDaftarLomba(daftarLomba: DaftarLomba): MutableLiveData<Boolean> {
        val mutableLiveData = MutableLiveData<Boolean>()
        databaseReference.child(PENDAFTAR).addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (value in snapshot.children) {
                            val daftarLombaValue = value.getValue(DaftarLomba::class.java)
                            if (daftarLombaValue != null) {
                                if (daftarLomba.idLomba == daftarLombaValue.idLomba && daftarLomba.idUser == daftarLombaValue.idUser) {
                                    mutableLiveData.postValue(true)
                                    break
                                } else {
                                    mutableLiveData.postValue(false)
                                }
                            } else {
                                mutableLiveData.postValue(false)
                            }
                        }
                    } else {
                        mutableLiveData.postValue(false)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    mutableLiveData.postValue(false)
                }
            })
        return mutableLiveData
    }

    fun getMaxId(): MutableLiveData<Int> {
        val mutableLiveData = MutableLiveData<Int>()
        databaseReference.child(PENDAFTAR).limitToLast(1)
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

    fun saveDaftarLomba(daftarLomba: DaftarLomba): MutableLiveData<Boolean> {
        val mutableLiveData = MutableLiveData<Boolean>()
        databaseReference.child(PENDAFTAR).child(daftarLomba.id).setValue(daftarLomba)
            .addOnSuccessListener {
                mutableLiveData.postValue(true)
            }.addOnFailureListener {
            mutableLiveData.postValue(false)
        }
        return mutableLiveData
    }
}