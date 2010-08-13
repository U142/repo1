@echo off


	REM - Parameter %1 = Jarfile
	@Echo on

	jarsigner -keystore mapapplet -storepass B76mmT %1 svein

	@Echo off
	goto end

ECHO terminated...

goto :end

:end
ECHO Done
pause