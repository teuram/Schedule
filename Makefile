
all: run

run: install
	adb shell am start -n com.teeura.schedule/.MainActivity

build:
	gradle assembleDebug

install: build
	adb install `find . -name 'app-debug.apk'`

log:
	adb logcat | grep " E " | grep com.teeura.schedule

