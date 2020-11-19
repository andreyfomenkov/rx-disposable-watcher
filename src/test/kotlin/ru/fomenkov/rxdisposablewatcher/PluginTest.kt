package ru.fomenkov.rxdisposablewatcher

import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.concurrent.TimeUnit

class PluginTest : BaseTest() {

    private val observable = Observable.create<Int> {}

    @Test
    fun testRxOperators() {
        val className = javaClass.name
        val methodName = "testRxOperators"

        observable
            .map {}
            .flatMap { observable }
            .filter { true }
            .delay(7, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.computation())
            .distinctUntilChanged()
            .skip(10)
            .subscribe()

        assertEquals(1, RxDisposableWatcher.probe().size)
        RxDisposableWatcher.probe().first().assertProbeEntry(
            expectedEntries = 1,
            expectedSourceType = SourceType.OBSERVABLE,
            expectedClassName = className,
            expectedMethod = methodName,
            expectedLineNumber = 27
        )
    }

    @Test
    fun testManualDispose() {
        observable.subscribe().apply {
            assertEquals(1, RxDisposableWatcher.probe().size)
            dispose()
            assertEquals(0, RxDisposableWatcher.probe().size)
        }
    }
}