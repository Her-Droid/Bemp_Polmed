package com.user.bemp.helper

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.os.StrictMode
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class PermissionHelper {
    private var mActivity: Activity? = null
    private val REQUEST_PERMISSION = 99

    fun PermissionHelper(activity: Activity?) {
        mActivity = activity
    }

    fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val permissionReadStorage = ContextCompat.checkSelfPermission(
                mActivity!!,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            val permissionWriteStorage = ContextCompat.checkSelfPermission(
                mActivity!!,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            val listPermissionNeeded: MutableList<String> = ArrayList()
            if (permissionWriteStorage != PackageManager.PERMISSION_GRANTED) {
                listPermissionNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
            if (permissionReadStorage != PackageManager.PERMISSION_GRANTED) {
                listPermissionNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
            if (!listPermissionNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(
                    mActivity!!,
                    listPermissionNeeded.toTypedArray(),
                    REQUEST_PERMISSION
                )
            }
        }
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
    }
}