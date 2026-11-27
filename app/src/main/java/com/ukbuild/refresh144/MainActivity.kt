package com.ukbuild.refresh144

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity() {
    private lateinit var status: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(28, 36, 28, 28)
        }

        val title = TextView(this).apply {
            text = "Refresh 144"
            textSize = 26f
        }
        status = TextView(this).apply {
            textSize = 15f
            setPadding(0, 24, 0, 24)
        }

        val permissionButton = Button(this).apply {
            text = "권한 설정 열기"
            setOnClickListener {
                startActivity(Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS,
                    Uri.parse("package:$packageName")))
            }
        }
        val applyButton = Button(this).apply {
            text = "144Hz 설정 적용"
            setOnClickListener {
                val r = RefreshSettings.apply(this@MainActivity)
                Toast.makeText(this@MainActivity,
                    "시스템: ${r.system}, Global: ${r.global}", Toast.LENGTH_LONG).show()
                updateStatus()
            }
        }
        val disableContactsButton = Button(this).apply {
            text = "전화/메시지 비활성화 시도"
            setOnClickListener { changeContactsState(false) }
        }
        val enableContactsButton = Button(this).apply {
            text = "전화/메시지 기본 상태 복구"
            setOnClickListener { changeContactsState(true) }
        }
        val refreshButton = Button(this).apply {
            text = "현재 설정 새로고침"
            setOnClickListener { updateStatus() }
        }

        layout.addView(title)
        layout.addView(status)
        layout.addView(permissionButton)
        layout.addView(applyButton)
        layout.addView(disableContactsButton)
        layout.addView(enableContactsButton)
        layout.addView(refreshButton)
        setContentView(layout)
        updateStatus()
    }

    override fun onResume() {
        super.onResume()
        if (::status.isInitialized) updateStatus()
    }

    private fun changeContactsState(enable: Boolean) {
        val target = "com.android.contacts"
        try {
            packageManager.setApplicationEnabledSetting(
                target,
                if (enable) android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_ENABLED
                else android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER,
                0
            )
            Toast.makeText(
                this,
                if (enable) "연락처 앱 복구 요청 완료" else "연락처 앱 비활성화 요청 완료",
                Toast.LENGTH_LONG
            ).show()
        } catch (e: SecurityException) {
            Toast.makeText(
                this,
                "순정 앱 권한으로는 직접 변경할 수 없어 앱 정보 화면을 엽니다. 여기서 비활성화/사용 설정이 가능한지 확인하세요.",
                Toast.LENGTH_LONG
            ).show()
            openContactsAppInfo(target)
        } catch (e: Exception) {
            Toast.makeText(this, "변경 실패: ${e.message}", Toast.LENGTH_LONG).show()
            openContactsAppInfo(target)
        }
    }

    private fun openContactsAppInfo(target: String) {
        try {
            startActivity(
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    .setData(Uri.parse("package:$target"))
            )
        } catch (_: Exception) {
            startActivity(Intent(Settings.ACTION_SETTINGS))
        }
    }

    private fun updateStatus() {
        val write = Settings.System.canWrite(this)
        val secure = checkSelfPermission(android.Manifest.permission.WRITE_SECURE_SETTINGS) ==
            android.content.pm.PackageManager.PERMISSION_GRANTED
        status.text = "WRITE_SETTINGS: ${if (write) "허용" else "미허용"}\n" +
            "WRITE_SECURE_SETTINGS: ${if (secure) "허용" else "미허용"}\n\n" +
            RefreshSettings.current(this) +
            "\n\n부팅 완료 후 144Hz 설정을 자동 적용합니다.\n" +
            "전화/메시지 버튼은 순정 권한으로 직접 변경이 차단되면 앱 정보 화면을 엽니다."
    }
}
