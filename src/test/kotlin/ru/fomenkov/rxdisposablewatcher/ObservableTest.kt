package ru.fomenkov.rxdisposablewatcher

import io.reactivex.rxjava3.core.Observable
import org.junit.Assert.assertEquals
import org.junit.Test

class ObservableTest : BaseTest() {

    private val observable = Observable.create<Int> {}

    @Test
    fun test() {
        val className = javaClass.name
        val methodName = "test"

        observable.subscribe()
        for (i in 0 .. 1) { observable.subscribe() }
        for (i in 0 .. 2) { observable.subscribe() }
        observable.subscribe().dispose() // Disposed
        for (i in 0 .. 10) { observable.subscribe().dispose() } // Disposed

        val result = RxDisposableWatcher.probe().sortByFirstElementLineNumber()
        assertEquals(3, result.size)
        result[0].assertProbeEntry(
            expectedEntries = 1,
            expectedSourceType = SourceType.OBSERVABLE,
            expectedClassName = className,
            expectedMethod = methodName,
            expectedLineNumber = 16
        )
        result[1].assertProbeEntry(
            expectedEntries = 2,
            expectedSourceType = SourceType.OBSERVABLE,
            expectedClassName = className,
            expectedMethod = methodName,
            expectedLineNumber = 17
        )
        result[2].assertProbeEntry(
            expectedEntries = 3,
            expectedSourceType = SourceType.OBSERVABLE,
            expectedClassName = className,
            expectedMethod = methodName,
            expectedLineNumber = 18
        )
    }
}