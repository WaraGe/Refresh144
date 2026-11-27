@echo off
set PACKAGE=com.ukbuild.refresh144
echo [1/2] WRITE_SECURE_SETTINGS 권한 부여 시도...
adb shell pm grant %PACKAGE% android.permission.WRITE_SECURE_SETTINGS
echo [2/2] WRITE_SETTINGS AppOp 허용...
adb shell appops set %PACKAGE% WRITE_SETTINGS allow
echo.
echo 완료. 태블릿에서 앱을 열고 권한 상태를 확인하세요.
pause
