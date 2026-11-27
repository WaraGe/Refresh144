package com.ukbuild.refresh144

import android.content.Context
import android.provider.Settings

object RefreshSettings {
    data class Result(val system: Boolean, val global: Boolean, val detail: String)

    fun apply(context: Context): Result {
        val messages = mutableListOf<String>()
        var systemOk = false

        // Attempt the actual writes instead of gating them only on canWrite().
        // This also records the real result on ColorOS builds with inconsistent permission reporting.
        try {
            val peakWrite = Settings.System.putString(
                context.contentResolver, "peak_refresh_rate", "144.00002"
            )
            val minWrite = Settings.System.putString(
                context.contentResolver, "min_refresh_rate", "120.00001"
            )
            val peakRead = Settings.System.getString(context.contentResolver, "peak_refresh_rate")
            val minRead = Settings.System.getString(context.contentResolver, "min_refresh_rate")
            systemOk = peakWrite && minWrite &&
                peakRead == "144.00002" && minRead == "120.00001"

            if (!systemOk) {
                messages += "System 쓰기 결과: peak=" + peakWrite + ", min=" + minWrite +
                    "; 읽기값: peak=" + peakRead + ", min=" + minRead +
                    "; canWrite=" + Settings.System.canWrite(context)
            }
        } catch (e: SecurityException) {
            messages += "System 권한 예외: " + e.message +
                "; canWrite=" + Settings.System.canWrite(context)
        } catch (e: Exception) {
            messages += "System 설정 쓰기 실패: " + e.message
        }

        var globalOk = false
        try {
            val globalWrite = Settings.Global.putString(
                context.contentResolver, "force_pad_floor_fps", "120"
            )
            val globalRead = Settings.Global.getString(context.contentResolver, "force_pad_floor_fps")
            globalOk = globalWrite && globalRead == "120"
            if (!globalOk) {
                messages += "Global 쓰기 결과: " + globalWrite + "; 읽기값: " + globalRead
            }
        } catch (e: SecurityException) {
            messages += "Global 권한 예외: " + e.message
        } catch (e: Exception) {
            messages += "Global 설정 쓰기 실패: " + e.message
        }

        return Result(systemOk, globalOk, messages.joinToString("\n"))
    }

    fun current(context: Context): String {
        val peak = Settings.System.getString(context.contentResolver, "peak_refresh_rate") ?: "미설정"
        val min = Settings.System.getString(context.contentResolver, "min_refresh_rate") ?: "미설정"
        val floor = Settings.Global.getString(context.contentResolver, "force_pad_floor_fps") ?: "미설정"
        return "peak_refresh_rate = " + peak + "\nmin_refresh_rate = " + min +
            "\nforce_pad_floor_fps = " + floor
    }
}
