@echo off
echo Uninstalling...
C:\Users\umm194\AppData\Local\Android\android-sdk\platform-tools\adb uninstall whatsapp.client
echo Installing...
C:\Users\umm194\AppData\Local\Android\android-sdk\platform-tools\adb install .\bin\client-debug.apk
echo READY