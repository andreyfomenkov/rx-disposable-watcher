#!/bin/bash
# Replace APPLICATION_ID, REPORT_DEVICE_LOCATION and REPORT_DESKTOP_LOCATION with your values

BROADCAST_ACTION="build-rx-report" # Intent filter action for BroadcastReceiver
APPLICATION_ID=com.myapplication
SLEEP_BEFORE_PULL=3 # Give a time (in seconds) to generate & save HTML report
REPORT_DEVICE_LOCATION=/storage/emulated/0/Android/data/$APPLICATION_ID/files/report.html # Location in Android device SD card
REPORT_DESKTOP_LOCATION=~/report.html # Location on desktop

adb shell am broadcast -a $BROADCAST_ACTION
sleep $SLEEP_BEFORE_PULL
adb pull $REPORT_DEVICE_LOCATION $REPORT_DESKTOP_LOCATION
adb shell rm $REPORT_DEVICE_LOCATION
open $REPORT_DESKTOP_LOCATION
