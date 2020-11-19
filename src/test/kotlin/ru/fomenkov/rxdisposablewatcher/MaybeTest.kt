package ru.fomenkov.rxdisposablewatcher

import io.reactivex.Maybe
import org.junit.Assert.assertEquals
import org.junit.Test

class MaybeTest : BaseTest() {

    private val maybe = Maybe.create<Int> {}

    @Test
    fun test() {
        val className = javaClass.name
        val methodName = "test"

        maybe.subscribe()
        for (i in 0 .. 1) { maybe.subscribe() }
        for (i in 0 .. 2) { maybe.subscribe() }
        maybe.subscribe().dispose() // Disposed
        for (i in 0 .. 10) { maybe.subscribe().dispose() } // Disposed

        val result = RxDisposableWatcher.probe().sortByFirstElementLineNumber()
        assertEquals(3, result.size)
        result[0].assertProbeEntry(
            expectedEntries = 1,
            expectedSourceType = SourceType.MAYBE,
            expectedClassName = className,
            expectedMethod = methodName,
            expectedLineNumber = 16
        )
        result[1].assertProbeEntry(
            expectedEntries = 2,
            expectedSourceType = SourceType.MAYBE,
            expectedClassName = className,
            expectedMethod = methodName,
            expectedLineNumber = 17
        )
        result[2].assertProbeEntry(
            expectedEntries = 3,
            expectedSourceType = SourceType.MAYBE,
            expectedClassName = className,
            expectedMethod = methodName,
            expectedLineNumber = 18
        )
    }
}