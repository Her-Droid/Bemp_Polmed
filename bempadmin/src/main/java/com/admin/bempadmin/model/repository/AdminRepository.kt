package com.admin.bempadmin.model.repository

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.admin.bempadmin.helper.TB_ADMIN
import com.admin.bempadmin.model.Admin
import com.google.firebase.FirebaseApp
import com.google.firebase.database.*

class AdminRepository(context: Context) {
    private val databaseReference: DatabaseReference

    init {
        FirebaseApp.initializeApp(context)
        databaseReference = FirebaseDatabase.getInstance().reference.child(TB_ADMIN)
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

    fun checkEmail(admin: Admin): MutableLiveData<Int> {
        val mutableLiveData = MutableLiveData<Int>()
        databaseReference.addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (value in snapshot.children) {
                        val adminValue = value.getValue(Admin::class.java)
                        if (adminValue != null) {
                            if (admin.email == adminValue.email) {
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

    fun registerAdminAnggota(admin: Admin): MutableLiveData<Boolean> {
        val mutableLiveData = MutableLiveData<Boolean>()
        databaseReference.child(admin.id).setValue(admin).addOnSuccessListener {
            mutableLiveData.postValue(true)
        }.addOnFailureListener {
            mutableLiveData.postValue(false)
        }
        return mutableLiveData
    }

    fun loginAdmin(admin: Admin): MutableLiveData<HashMap<String, Any>> {
        val mutableLiveData = MutableLiveData<HashMap<String, Any>>()
        val hashMap = HashMap<String, Any>()
        databaseReference.addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (value in snapshot.children) {
                        val adminLogin = value.getValue(Admin::class.java)
                        if (adminLogin != null) {
                            if (admin.email == adminLogin.email && admin.password == adminLogin.password) {
                                hashMap["code"] = 1
                                hashMap["admin"] = adminLogin
                                break
                            } else {
                                hashMap["code"] = 0
                            }
                        } else {
                            hashMap["code"] = 0
                        }
                    }
                } else {
                    hashMap["code"] = 0
                }
                mutableLiveData.postValue(hashMap)
            }

            override fun onCancelled(error: DatabaseError) {
                hashMap["code"] = -1
                mutableLiveData.postValue(hashMap)
            }
        })
        return mutableLiveData
    }

    fun ubahPassword(admin: Admin): MutableLiveData<Boolean> {
        val mutableLiveData = MutableLiveData<Boolean>()
        databaseReference.child(admin.id).child("password").setValue(admin.password)
            .addOnSuccessListener {
                mutableLiveData.postValue(true)
            }.addOnFailureListener {
                mutableLiveData.postValue(false)
            }
        return mutableLiveData
    }

    fun delete(admin: Admin): MutableLiveData<Boolean> {
        val mutableLiveData = MutableLiveData<Boolean>()
        databaseReference.child(admin.id).removeValue().addOnSuccessListener {
            mutableLiveData.postValue(true)
        }.addOnFailureListener {
            mutableLiveData.postValue(false)
        }
        return mutableLiveData
    }

    fun updateStatusAdmin(admin: Admin): MutableLiveData<Boolean> {
        val mutableLiveData = MutableLiveData<Boolean>()
        val hashMapUpdate = HashMap<String, Any>()
        hashMapUpdate["status"] = admin.status
        hashMapUpdate["email"] = admin.email
        databaseReference.child(admin.id).updateChildren(hashMapUpdate).addOnSuccessListener {
            mutableLiveData.postValue(true)
        }.addOnFailureListener {
            mutableLiveData.postValue(false)
        }
        return mutableLiveData
    }

    fun showAdmin(): MutableLiveData<HashMap<String, Any>> {
        val mutableLiveData = MutableLiveData<HashMap<String, Any>>()
        val hashMap = HashMap<String, Any>()
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    hashMap["code"] = 200
                    val adminList = ArrayList<Admin>()
                    for (value in snapshot.children) {
                        val admin = value.getValue(Admin::class.java)
                        admin?.let { adminList.add(it) }
                    }
                    hashMap[TB_ADMIN] = adminList
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