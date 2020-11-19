package ru.fomenkov.rxdisposablewatcher

import io.reactivex.*
import io.reactivex.subjects.BehaviorSubject
import org.junit.Assert.*
import org.junit.Test
import java.lang.ref.WeakReference

/**
 * This test may fail because System.gc() is only a suggestion for JVM to run garbage
 * collector. Unused references may not be recycled in time when asserting null value
 */
class MemoryLeakTest : BaseTest() {

    private val observable = Observable.create<Int> {}
    private val single = Single.create<Int> {}
    private val completable = Completable.create {}
    private val maybe = Maybe.create<Int> {}
    private val flowable = observable.toFlowable(BackpressureStrategy.LATEST)

    @Test
    fun testWeakDisposableForObservable() {
        WeakReference(observable.subscribe()).testReferenceCollected()
    }

    @Test
    fun testWeakDisposableForSingle() {
        WeakReference(single.subscribe()).testReferenceCollected()
    }

    @Test
    fun testWeakDisposableForCompletable() {
        WeakReference(completable.subscribe()).testReferenceCollected()
    }

    @Test
    fun testWeakDisposableForMaybe() {
        WeakReference(maybe.subscribe()).testReferenceCollected()
    }

    @Test
    fun testWeakDisposableForFlowable() {
        WeakReference(flowable.subscribe()).testReferenceCollected()
    }

    @Test
    fun testWeakDisposableForWrapperClass() {
        Wrapper().subscribe()

        System.gc()

        assertEquals(0, RxDisposableWatcher.probe().size)
    }

    private class Wrapper {

        private val subject = BehaviorSubject.create<Int>()

        fun subscribe() {
            subject.subscribe()
        }
    }

    private fun <T> WeakReference<T>.testReferenceCollected() {
        assertNotNull("Reference is null", get())
        System.gc()
        assertNull("Reference not recycled. Check detailed comment above", get())
    }
}