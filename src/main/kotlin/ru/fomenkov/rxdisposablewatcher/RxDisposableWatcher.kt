package ru.fomenkov.rxdisposablewatcher

import io.reactivex.*
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.internal.observers.*
import io.reactivex.internal.operators.maybe.MaybeCallbackObserver
import io.reactivex.internal.subscribers.LambdaSubscriber
import io.reactivex.plugins.RxJavaPlugins
import org.reactivestreams.Subscriber
import java.lang.ref.WeakReference

object RxDisposableWatcher {

    private val sources = HashMap<Int, SourceType>()
    private val traces = HashMap<Int, StackTrace>()
    private val records = HashMap<Int, MutableList<WeakDisposable>>()
    private var isInitialized = false

    private val observableFunction = BiFunction { _: Observable<Any>, observer: Observer<Any> ->
        if (observer is LambdaObserver) {
            addDisposableRecord(SourceType.OBSERVABLE, observer)
        }
        observer
    }
    private val singleFunction = BiFunction { _: Single<Any>, observer: SingleObserver<Any> ->
        when (observer) {
            is ConsumerSingleObserver -> addDisposableRecord(SourceType.SINGLE, observer)
            is BiConsumerSingleObserver -> addDisposableRecord(SourceType.SINGLE, observer)
        }
        observer
    }
    private val completableFunction = BiFunction { _: Completable, observer: CompletableObserver ->
        when (observer) {
            is EmptyCompletableObserver -> addDisposableRecord(SourceType.COMPLETABLE, observer)
            is CallbackCompletableObserver -> addDisposableRecord(SourceType.COMPLETABLE, observer)
        }
        observer
    }
    private val maybeFunction = BiFunction { _: Maybe<Any>, observer: MaybeObserver<Any> ->
        if (observer is MaybeCallbackObserver) {
            addDisposableRecord(SourceType.MAYBE, observer)
        }
        observer
    }
    private val flowableFunction = BiFunction { _: Flowable<Any>, observer: Subscriber<Any> ->
        if (observer is LambdaSubscriber) {
            addDisposableRecord(SourceType.FLOWABLE, observer)
        }
        observer
    }

    /**
     * Init plugin
     */
    fun init() {
        if (!isInitialized) {
            setupListeners()
            isInitialized = true
        }
    }

    private fun setupListeners() {
        check(!RxJavaPlugins.isLockdown()) { LOCKDOWN_ERROR_MESSAGE }
        RxJavaPlugins.setOnObservableSubscribe(observableFunction)
        RxJavaPlugins.setOnSingleSubscribe(singleFunction)
        RxJavaPlugins.setOnCompletableSubscribe(completableFunction)
        RxJavaPlugins.setOnMaybeSubscribe(maybeFunction)
        RxJavaPlugins.setOnFlowableSubscribe(flowableFunction)
        RxJavaPlugins.lockdown()
        checkIntegrity()
    }

    private fun checkIntegrity() {
        check(RxJavaPlugins.getOnObservableSubscribe() == observableFunction) { LOCKDOWN_ERROR_MESSAGE }
        check(RxJavaPlugins.getOnSingleSubscribe() == singleFunction) { LOCKDOWN_ERROR_MESSAGE }
        check(RxJavaPlugins.getOnCompletableSubscribe() == completableFunction) { LOCKDOWN_ERROR_MESSAGE }
        check(RxJavaPlugins.getOnMaybeSubscribe() == maybeFunction) { LOCKDOWN_ERROR_MESSAGE }
        check(RxJavaPlugins.getOnFlowableSubscribe() == flowableFunction) { LOCKDOWN_ERROR_MESSAGE }
    }

    @Synchronized
    private fun addDisposableRecord(source: SourceType, disposable: Disposable) {
        val trace = StackTrace(Thread.currentThread().stackTrace)
        val hashCode = trace.hashCode()
        val list = records[hashCode] ?: mutableListOf()
        list += WeakDisposable(disposable)
        sources[hashCode] = source
        traces[hashCode] = trace
        records[hashCode] = list
    }

    /**
     * Get a table with all currently alive Rx subscriptions. Includes the next parameters:
     *  - final observable type;
     *  - entry location in a code (stacktrace);
     *  - number of recorded entries for each location.
     *
     * @return a list with probes about active Rx subscriptions (Disposables)
     */
    @Synchronized
    fun probe(): List<ProbeEntry> {
        check(isInitialized) { "Plugin not initialized. Call init() first" }
        val probes = mutableListOf<ProbeEntry>()

        traces.entries.forEach { (hashCode, trace) ->
            val sourceType = checkNotNull(sources[hashCode]) { "Source type is missing" }
            val croppedTrace = crop(trace, sourceType)
            val record = records[hashCode]?.toList() ?: emptyList()
            val entries = record.count { ref ->
                val disposable = ref.get()
                when (disposable != null) {
                    true -> !disposable.isDisposed
                    else -> false
                }
            }
            if (entries > 0) {
                probes += ProbeEntry(sourceType, croppedTrace, entries)
            }
        }
        return probes.sortedByDescending { it.entries }
    }

    private fun crop(trace: StackTrace, source: SourceType) = trace.crop { element ->
        element.method == RX_SUBSCRIBE_METHOD_NAME &&
                element.className.startsWith(RX_PACKAGE_PREFIX) &&
                element.className.endsWith(".${source.className}")
    }

    /**
     * Cleanup subscriptions table
     */
    @Synchronized
    fun clear() {
        sources.clear()
        records.clear()
        traces.clear()
    }
}

/**
 * Probe result for a particular Rx subscription
 * [source]: final observable type
 * [stackTrace]: location in the code
 * [entries]: total number of recorded entries
 */
data class ProbeEntry(val source: SourceType, val stackTrace: StackTrace, val entries: Int)

enum class SourceType(val className: String) {
    OBSERVABLE("Observable"),
    SINGLE("Single"),
    COMPLETABLE("Completable"),
    MAYBE("Maybe"),
    FLOWABLE("Flowable")
}

private typealias WeakDisposable = WeakReference<Disposable>
private const val LOCKDOWN_ERROR_MESSAGE = "Unable to setup. RxJavaPlugins are already in use"
private const val RX_SUBSCRIBE_METHOD_NAME = "subscribe"
private const val RX_PACKAGE_PREFIX = "io.reactivex"