package ru.fomenkov.rxdisposablewatcher

import io.reactivex.BackpressureStrategy
import io.reactivex.Observable
import org.junit.Assert.assertEquals
import org.junit.Test

class FlowableTest : BaseTest() {

    private val observable = Observable.create<Int> {}
    private val flowable = observable.toFlowable(BackpressureStrategy.LATEST)

    @Test
    fun test() {
        val className = javaClass.name
        val methodName = "test"

        flowable.subscribe()
        for (i in 0 .. 1) { flowable.subscribe() }
        for (i in 0 .. 2) { flowable.subscribe() }
        flowable.subscribe().dispose() // Disposed
        for (i in 0 .. 10) { flowable.subscribe().dispose() } // Disposed

        val result = RxDisposableWatcher.probe().sortByFirstElementLineNumber()
        assertEquals(3, result.size)
        result[0].assertProbeEntry(
            expectedEntries = 1,
            expectedSourceType = SourceType.FLOWABLE,
            expectedClassName = className,
            expectedMethod = methodName,
            expectedLineNumber = 18
        )
        result[1].assertProbeEntry(
            expectedEntries = 2,
            expectedSourceType = SourceType.FLOWABLE,
            expectedClassName = className,
            expectedMethod = methodName,
            expectedLineNumber = 19
        )
        result[2].assertProbeEntry(
            expectedEntries = 3,
            expectedSourceType = SourceType.FLOWABLE,
            expectedClassName = className,
            expectedMethod = methodName,
            expectedLineNumber = 20
        )
    }
}