
run: all
all:
	gradle assembleDebug
	adb install `find . -name 'app-debug.apk'`
	adb shell am start -n com.teeura.schedule/.MainActivity

