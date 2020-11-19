package ru.fomenkov.rxdisposablewatcher

import junit.framework.Assert.assertEquals
import org.junit.After
import org.junit.Before

abstract class BaseTest {

    private val comparator = Comparator<ProbeEntry> { itemA, itemB ->
        val elementA = itemA.stackTrace.elements().first()
        val elementB = itemB.stackTrace.elements().first()
        elementA.line.compareTo(elementB.line)
    }

    @Before
    fun setup() = RxDisposableWatcher.init()

    fun List<ProbeEntry>.sortByFirstElementLineNumber() = sortedWith(comparator)

    fun ProbeEntry.assertProbeEntry(
        expectedEntries: Int,
        expectedSourceType: SourceType,
        expectedClassName: String,
        expectedMethod: String,
        expectedLineNumber: Int
    ) {
        val firstElement = stackTrace.elements().first()
        assertEquals(expectedEntries, entries)
        assertEquals(expectedSourceType, source)
        assertEquals(expectedClassName, firstElement.className)
        assertEquals(expectedMethod, firstElement.method)
        assertEquals(expectedLineNumber, firstElement.line)
    }

    @After
    fun teardown() = RxDisposableWatcher.clear()
}