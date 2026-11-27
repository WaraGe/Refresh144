package com.ukbuild.refresh144

import android.content.Context
import android.provider.Settings

object RefreshSettings {
    data class Result(val system: Boolean, val global: Boolean, val detail: String)

    fun apply(context: Context): Result {
        val systemAllowed = Settings.System.canWrite(context)
        val secureAllowed = context.checkSelfPermission(
            android.Manifest.permission.WRITE_SECURE_SETTINGS
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED

        var systemOk = false
        var globalOk = false
        val messages = mutableListOf<String>()

        if (systemAllowed) {
            try {
                Settings.System.putFloat(context.contentResolver, "peak_refresh_rate", 144.00002f)
                Settings.System.putFloat(context.contentResolver, "min_refresh_rate", 120.00001f)
                systemOk = true
            } catch (e: Exception) {
                messages += "시스템 설정 쓰기 실패: ${e.message}"
            }
        } else {
            messages += "WRITE_SETTINGS 권한이 없습니다."
        }

        if (secureAllowed) {
            try {
                Settings.Global.putInt(context.contentResolver, "force_pad_floor_fps", 120)
                globalOk = true
            } catch (e: Exception) {
                messages += "Global 설정 쓰기 실패: ${e.message}"
            }
        } else {
            messages += "WRITE_SECURE_SETTINGS 권한이 없습니다."
        }

        return Result(systemOk, globalOk, messages.joinToString("\n"))
    }

    fun current(context: Context): String {
        val peak = Settings.System.getString(context.contentResolver, "peak_refresh_rate") ?: "미설정"
        val min = Settings.System.getString(context.contentResolver, "min_refresh_rate") ?: "미설정"
        val floor = Settings.Global.getString(context.contentResolver, "force_pad_floor_fps") ?: "미설정"
        return "peak_refresh_rate = $peak\nmin_refresh_rate = $min\nforce_pad_floor_fps = $floor"
    }
}
