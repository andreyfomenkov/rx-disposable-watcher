package ru.fomenkov.rxdisposablewatcher

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AndroidSpecificTest : BaseTest() {

    @Test
    fun testParseElement() {
        assertEquals(
            Element(className = "ru.fomenkov.testrxapplication.SettingsActivity", method = "init", line = 19),
            parse("ru.fomenkov.testrxapplication.SettingsActivity.init:19")
        )
        assertEquals(
            Element(className = "com.android.internal.os.RuntimeInit\$MethodAndArgsCaller", method = "run", line = 441),
            parse("com.android.internal.os.RuntimeInit\$MethodAndArgsCaller.run:441")
        )
        assertEquals(
            Element(className = "java.lang.reflect.Method", method = "invoke", line = -2),
            parse("java.lang.reflect.Method.invoke:-2")
        )
    }

    @Test
    fun testEqualStackTracesForActivityRotation() {
        val traceA = StackTrace(
            trace = parse(
                "ru.fomenkov.testrxapplication.SettingsActivity.init:19",
                "ru.fomenkov.testrxapplication.SettingsActivity.onCreate:14",
                "android.app.Activity.performCreate:7258",
                "android.app.Activity.performCreate:7249",
                "android.app.Instrumentation.callActivityOnCreate:1222",
                "android.app.ActivityThread.performLaunchActivity:2941", // Launch only
                "android.app.ActivityThread.handleLaunchActivity:3073",
                "android.app.ActivityThread.-wrap11:0",
                "android.app.ActivityThread\$H.handleMessage:1734",
                "android.os.Handler.dispatchMessage:106",
                "android.os.Looper.loop:164",
                "android.app.ActivityThread.main:7025",
                "java.lang.reflect.Method.invoke:-2",
                "com.android.internal.os.RuntimeInit\$MethodAndArgsCaller.run:441",
                "com.android.internal.os.ZygoteInit.main:1408"
            )
        )
        val traceB = StackTrace(
            trace = parse(
                "ru.fomenkov.testrxapplication.SettingsActivity.init:19",
                "ru.fomenkov.testrxapplication.SettingsActivity.onCreate:14",
                "android.app.Activity.performCreate:7258",
                "android.app.Activity.performCreate:7249",
                "android.app.Instrumentation.callActivityOnCreate:1222",
                "android.app.ActivityThread.performLaunchActivity:2941",
                "android.app.ActivityThread.handleLaunchActivity:3073", // Launch
                "android.app.ActivityThread.handleRelaunchActivity:4964", // Relaunch
                "android.app.ActivityThread.-wrap19:0",
                "android.app.ActivityThread\$H.handleMessage:1740",
                "android.os.Handler.dispatchMessage:106",
                "android.os.Looper.loop:164",
                "android.app.ActivityThread.main:7025",
                "java.lang.reflect.Method.invoke:-2",
                "com.android.internal.os.RuntimeInit\$MethodAndArgsCaller.run:441",
                "com.android.internal.os.ZygoteInit.main:1408"
            )
        )
        assertTrue("Stack traces must be equal", traceA == traceB)
        assertTrue("Stack trace hash codes must be equal", traceA.hashCode() == traceB.hashCode())
    }
}