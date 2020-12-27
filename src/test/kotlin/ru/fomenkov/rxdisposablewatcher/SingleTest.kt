package ru.fomenkov.rxdisposablewatcher

import io.reactivex.rxjava3.core.Single
import org.junit.Assert.assertEquals
import org.junit.Test

class SingleTest : BaseTest() {

    private val single = Single.create<Int> {}

    @Test
    fun test() {
        val className = javaClass.name
        val methodName = "test"

        single.subscribe()
        for (i in 0 .. 1) { single.subscribe() }
        for (i in 0 .. 2) { single.subscribe() }
        single.subscribe().dispose() // Disposed
        for (i in 0 .. 10) { single.subscribe().dispose() } // Disposed

        val result = RxDisposableWatcher.probe().sortByFirstElementLineNumber()
        assertEquals(3, result.size)
        result[0].assertProbeEntry(
            expectedEntries = 1,
            expectedSourceType = SourceType.SINGLE,
            expectedClassName = className,
            expectedMethod = methodName,
            expectedLineNumber = 16
        )
        result[1].assertProbeEntry(
            expectedEntries = 2,
            expectedSourceType = SourceType.SINGLE,
            expectedClassName = className,
            expectedMethod = methodName,
            expectedLineNumber = 17
        )
        result[2].assertProbeEntry(
            expectedEntries = 3,
            expectedSourceType = SourceType.SINGLE,
            expectedClassName = className,
            expectedMethod = methodName,
            expectedLineNumber = 18
        )
    }
}