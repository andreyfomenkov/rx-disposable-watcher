#!/bin/bash

# Script has the next steps:
# - send broadcast event to Android device;
# - wait a little bit;
# - pull HTML report & display in a browser.
# REPLACE! placeholders <PATH_ON_SDCARD> and <PATH_ON_DESKTOP>

BROADCAST_ACTION="com.testapp.rxreport"
SLEEP_BEFORE_PULL=3 # Delay in seconds
# Source location in Android device SD card
REPORT_DEVICE_LOCATION=<PATH_ON_SDCARD>/report.html
# Target location on a desktop
REPORT_DESKTOP_LOCATION=<PATH_ON_DESKTOP>/report.html

adb shell am broadcast -a $BROADCAST_ACTION
sleep $SLEEP_BEFORE_PULL
adb pull $REPORT_DEVICE_LOCATION $REPORT_DESKTOP_LOCATION
adb shell rm $REPORT_DEVICE_LOCATION
open $REPORT_DESKTOP_LOCATION