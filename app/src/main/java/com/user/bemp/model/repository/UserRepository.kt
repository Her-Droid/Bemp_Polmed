package com.user.bemp.model.repository

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.google.firebase.FirebaseApp
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.user.bemp.helper.FOTO_PROFIL
import com.user.bemp.helper.TB_USER
import com.user.bemp.model.FotoProfil
import com.user.bemp.model.User

class UserRepository(context: Context) {

    private val databaseReference: DatabaseReference
    private val storageReference: StorageReference

    init {
        FirebaseApp.initializeApp(context)
        databaseReference = FirebaseDatabase.getInstance().reference.child(TB_USER)
        storageReference = FirebaseStorage.getInstance().reference.child("image").child(FOTO_PROFIL)
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

    fun checkNim(user: User): MutableLiveData<Int> {
        val mutableLiveData = MutableLiveData<Int>()
        databaseReference.addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (value in snapshot.children) {
                        val userValue = value.getValue(User::class.java)
                        if (userValue != null) {
                            if (user.nim == userValue.nim) {
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

    fun registerUser(user: User): MutableLiveData<Boolean> {
        val mutableLiveData = MutableLiveData<Boolean>()
        databaseReference.child(user.id).setValue(user).addOnSuccessListener {
            mutableLiveData.postValue(true)
        }.addOnFailureListener {
            mutableLiveData.postValue(false)
        }
        return mutableLiveData
    }

    fun loginUser(user: User): MutableLiveData<HashMap<String, Any>> {
        val mutableLiveData = MutableLiveData<HashMap<String, Any>>()
        val hashMap = HashMap<String, Any>()
        databaseReference.addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (value in snapshot.children) {
                        val userLogin = value.getValue(User::class.java)
                        if (userLogin != null) {
                            if (user.nim == userLogin.nim && user.password == userLogin.password) {
                                hashMap["code"] = 1
                                hashMap["user"] = userLogin
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

    fun ubahPassword(user: User): MutableLiveData<Boolean> {
        val mutableLiveData = MutableLiveData<Boolean>()
        databaseReference.child(user.id).child("password").setValue(user.password).addOnSuccessListener {
            mutableLiveData.postValue(true)
        }.addOnFailureListener {
            mutableLiveData.postValue(false)
        }
        return mutableLiveData
    }

    fun uploadFoto(fotoProfil: FotoProfil): MutableLiveData<String> {
        val mutableLiveData = MutableLiveData<String>()
        val filepath = storageReference.child(fotoProfil.imageName + "." + fotoProfil.extension)
        filepath.putFile(fotoProfil.uri)
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

    fun deleteImage(user: User): MutableLiveData<Boolean> {
        val mutableLiveData = MutableLiveData<Boolean>()
        FirebaseStorage.getInstance().getReferenceFromUrl(user.imageUrl).delete().addOnSuccessListener {
            mutableLiveData.postValue(true)
        }.addOnFailureListener {
            mutableLiveData.postValue(false)
        }
        return mutableLiveData
    }

    fun updateFotoUrl(user: User): MutableLiveData<Boolean> {
        val mutableLiveData = MutableLiveData<Boolean>()
        databaseReference.child(user.id).child("imageUrl").setValue(user.imageUrl).addOnSuccessListener {
            mutableLiveData.postValue(true)
        }.addOnFailureListener {
            mutableLiveData.postValue(false)
        }
        return mutableLiveData
    }

}