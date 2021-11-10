package com.user.bemp.model.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.FirebaseApp
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.user.bemp.helper.ANGGOTA
import com.user.bemp.helper.BIO
import com.user.bemp.helper.DIVISI
import com.user.bemp.helper.TB_STRUKTUR_ORGANISASI
import com.user.bemp.model.Divisi
import com.user.bemp.model.StrukturOrganisasi

class StrukturOrganisasiRepository(private val context: Context) {

    private val databaseReference: DatabaseReference
    private val databaseReferenceAnggota: DatabaseReference
    private val databaseReferenceDivisi: DatabaseReference
    private val databaseReferenceBio: DatabaseReference
    private val storageReference: StorageReference

    init {
        FirebaseApp.initializeApp(context)
        databaseReference = FirebaseDatabase.getInstance().reference.child(TB_STRUKTUR_ORGANISASI)
        databaseReferenceAnggota = databaseReference.child(ANGGOTA)
        databaseReferenceDivisi = databaseReference.child(DIVISI)
        databaseReferenceBio = databaseReference.child(BIO)
        storageReference = FirebaseStorage.getInstance().reference.child("image").child(ANGGOTA)
    }

    fun showBioStrukturOrgnisasi(): MutableLiveData<String> {
        val mutableLiveData = MutableLiveData<String>()
        databaseReferenceBio.addValueEventListener(object : ValueEventListener {
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

                    databaseReferenceAnggota.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {
                                hashMap["code"] = 200
                                val anggotaList = ArrayList<StrukturOrganisasi>()
                                for (value in snapshot.children) {
                                    val anggota = value.getValue(StrukturOrganisasi::class.java)
                                    if (anggota != null) {
                                        var namaDivisi = hashMapDivisi[anggota.idDivisi].toString()
                                        Log.v("jajal", namaDivisi)
                                        if (namaDivisi == "null"){
                                            namaDivisi = "-"
                                        }

                                        anggotaList.add(
                                            StrukturOrganisasi(
                                                anggota.id,
                                                anggota.nama,
                                                namaDivisi,
                                                anggota.motto,
                                                anggota.imageUrl
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

}