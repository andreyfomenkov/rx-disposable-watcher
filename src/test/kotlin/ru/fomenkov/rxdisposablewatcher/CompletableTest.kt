package ru.fomenkov.rxdisposablewatcher

import io.reactivex.rxjava3.core.Completable
import org.junit.Assert.assertEquals
import org.junit.Test

class CompletableTest : BaseTest() {

    private val completable = Completable.create {}

    @Test
    fun test() {
        val className = javaClass.name
        val methodName = "test"

        completable.subscribe()
        for (i in 0 .. 1) { completable.subscribe() }
        for (i in 0 .. 2) { completable.subscribe() }
        completable.subscribe().dispose() // Disposed
        for (i in 0 .. 10) { completable.subscribe().dispose() } // Disposed

        val result = RxDisposableWatcher.probe().sortByFirstElementLineNumber()
        assertEquals(3, result.size)
        result[0].assertProbeEntry(
            expectedEntries = 1,
            expectedSourceType = SourceType.COMPLETABLE,
            expectedClassName = className,
            expectedMethod = methodName,
            expectedLineNumber = 16
        )
        result[1].assertProbeEntry(
            expectedEntries = 2,
            expectedSourceType = SourceType.COMPLETABLE,
            expectedClassName = className,
            expectedMethod = methodName,
            expectedLineNumber = 17
        )
        result[2].assertProbeEntry(
            expectedEntries = 3,
            expectedSourceType = SourceType.COMPLETABLE,
            expectedClassName = className,
            expectedMethod = methodName,
            expectedLineNumber = 18
        )
    }
}