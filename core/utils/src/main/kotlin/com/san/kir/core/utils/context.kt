package com.san.kir.core.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import android.widget.Toast

public fun Context.isOnWifi(): Boolean {
    val activeNetworkInfo = connectivityManager.activeNetworkInfo
    return if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
        activeNetworkInfo.type == ConnectivityManager.TYPE_WIFI
    } else {
        false
    }
}

public fun Context.isNetworkAvailable(): Boolean {
    val activeNetworkInfo = connectivityManager.activeNetworkInfo
    return activeNetworkInfo != null && activeNetworkInfo.isConnected
}

public val Context.connectivityManager: ConnectivityManager
    get() = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager


public fun Context.quantitySimple(id: Int, quantity: Int): String {
    return resources.getQuantityString(id, quantity, quantity)
}

public fun Context.longToast(resId: Int, vararg formatArgs: Any?) {
    longToast(getString(resId, *formatArgs))
}

public fun Context.longToast(text: String) {
    Toast.makeText(this, text, Toast.LENGTH_LONG).show()
}

public fun Context.toast(resId: Int, vararg formatArgs: Any?) {
    toast(getString(resId, *formatArgs))
}

public fun Context.toast(text: String) {
    Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
}


public fun Context.browse(url: String, newTask: Boolean = false): Boolean {
    return try {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        if (newTask) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(intent)
        true
    } catch (e: ActivityNotFoundException) {
        e.printStackTrace()
        false
    }
}
