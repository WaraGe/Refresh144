package com.ukbuild.refresh144

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED ||
            intent.action == Intent.ACTION_MY_PACKAGE_REPLACED) {
            val result = RefreshSettings.apply(context)
            Log.i("Refresh144", "Boot apply: system=${result.system}, global=${result.global}, ${result.detail}")
        }
    }
}
