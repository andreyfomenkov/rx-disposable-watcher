package ru.fomenkov.rxdisposablewatcher

import io.reactivex.*
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

    @Test
    fun testSortedReport() {
        val observable = Observable.create<Int> {}
        val single = Single.create<Int> {}
        val completable = Completable.create {}
        val maybe = Maybe.create<Int> {}
        val flowable = observable.toFlowable(BackpressureStrategy.LATEST)

        for (i in 0 until 11) { completable.subscribe() }
        for (i in 0 until 22) { single.subscribe() }
        observable.subscribe()
        for (i in 0 until 44) { maybe.subscribe() }
        for (i in 0 until 33) { flowable.subscribe() }

        val probe = RxDisposableWatcher.probe()

        assertEquals(5, probe.size)
        probe[0].apply {
            assertEquals(SourceType.MAYBE, source)
            assertEquals(44, entries)
        }
        probe[1].apply {
            assertEquals(SourceType.FLOWABLE, source)
            assertEquals(33, entries)
        }
        probe[2].apply {
            assertEquals(SourceType.SINGLE, source)
            assertEquals(22, entries)
        }
        probe[3].apply {
            assertEquals(SourceType.COMPLETABLE, source)
            assertEquals(11, entries)
        }
        probe[4].apply {
            assertEquals(SourceType.OBSERVABLE, source)
            assertEquals(1, entries)
        }
    }
}