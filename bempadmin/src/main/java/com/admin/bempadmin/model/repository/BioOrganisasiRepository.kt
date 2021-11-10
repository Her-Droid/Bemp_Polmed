package com.admin.bempadmin.model.repository

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.admin.bempadmin.helper.BIO
import com.admin.bempadmin.helper.TB_STRUKTUR_ORGANISASI
import com.google.firebase.FirebaseApp
import com.google.firebase.database.*


class BioOrganisasiRepository(context: Context) {
    private val databaseReference: DatabaseReference

    init {
        FirebaseApp.initializeApp(context)
        databaseReference =
            FirebaseDatabase.getInstance().reference.child(TB_STRUKTUR_ORGANISASI).child(BIO)
    }

    fun showBioStrukturOrgnisasi(): MutableLiveData<String> {
        val mutableLiveData = MutableLiveData<String>()
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    mutableLiveData.postValue(snapshot.value.toString())
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
        return mutableLiveData
    }

    fun saveBio(bio: String): MutableLiveData<Int> {
        val mutableLiveData = MutableLiveData<Int>()
        databaseReference.setValue(bio).addOnCompleteListener {
            mutableLiveData.postValue(1)
        }.addOnFailureListener {
            mutableLiveData.postValue(0)
        }
        return mutableLiveData
    }


}