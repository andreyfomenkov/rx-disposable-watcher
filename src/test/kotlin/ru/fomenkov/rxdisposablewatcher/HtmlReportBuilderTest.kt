package ru.fomenkov.rxdisposablewatcher

import io.reactivex.*
import org.junit.Assert.*
import org.junit.Test
import ru.fomenkov.rxdisposablewatcher.report.HtmlReportBuilder

class HtmlReportBuilderTest : BaseTest() {

    @Test
    fun testTemplatePlaceholders() {
        val line = javaClass.getResource(TEMPLATE_LINE).readText()
        val block = javaClass.getResource(TEMPLATE_BLOCK).readText()
        val report = javaClass.getResource(TEMPLATE_REPORT).readText()

        assertTrue("Expecting $PLACEHOLDER_VALUE_LINE in $TEMPLATE_LINE", line.contains(PLACEHOLDER_VALUE_LINE))
        assertTrue("Expecting $PLACEHOLDER_VALUE_COLOR in $TEMPLATE_BLOCK", block.contains(PLACEHOLDER_VALUE_COLOR))
        assertTrue("Expecting $PLACEHOLDER_VALUE_DETAILS in $TEMPLATE_BLOCK", block.contains(PLACEHOLDER_VALUE_DETAILS))
        assertTrue("Expecting $PLACEHOLDER_VALUE_REDUCED in $TEMPLATE_BLOCK", block.contains(PLACEHOLDER_VALUE_REDUCED))
        assertTrue("Expecting $PLACEHOLDER_VALUE_FULL in $TEMPLATE_BLOCK", block.contains(PLACEHOLDER_VALUE_FULL))
        assertTrue("Expecting $PLACEHOLDER_VALUE_TOTAL in $TEMPLATE_REPORT", report.contains(PLACEHOLDER_VALUE_TOTAL))
        assertTrue("Expecting $PLACEHOLDER_VALUE_ITEMS in $TEMPLATE_REPORT", report.contains(PLACEHOLDER_VALUE_ITEMS))
    }

    @Test
    fun testTemplateChecksum() {
        val lineChecksum = javaClass.getResource(TEMPLATE_LINE).readText().hashCode()
        val blockChecksum = javaClass.getResource(TEMPLATE_BLOCK).readText().hashCode()
        val reportChecksum = javaClass.getResource(TEMPLATE_REPORT).readText().hashCode()

        assertChecksumEquals(TEMPLATE_LINE, TEMPLATE_LINE_CHECKSUM, lineChecksum)
        assertChecksumEquals(TEMPLATE_BLOCK, TEMPLATE_BLOCK_CHECKSUM, blockChecksum)
        assertChecksumEquals(TEMPLATE_REPORT, TEMPLATE_REPORT_CHECKSUM, reportChecksum)
    }

    @Test
    fun testOutputHtmlReport() {
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
        val builder = HtmlReportBuilder(probe)
        val report = builder.build()

        assertFalse("Placeholder $PLACEHOLDER_VALUE_LINE not replaced", report.contains(PLACEHOLDER_VALUE_LINE))
        assertFalse("Placeholder $PLACEHOLDER_VALUE_COLOR not replaced", report.contains(PLACEHOLDER_VALUE_COLOR))
        assertFalse("Placeholder $PLACEHOLDER_VALUE_DETAILS not replaced", report.contains(PLACEHOLDER_VALUE_DETAILS))
        assertFalse("Placeholder $PLACEHOLDER_VALUE_REDUCED not replaced", report.contains(PLACEHOLDER_VALUE_REDUCED))
        assertFalse("Placeholder $PLACEHOLDER_VALUE_FULL not replaced", report.contains(PLACEHOLDER_VALUE_FULL))
        assertFalse("Placeholder $PLACEHOLDER_VALUE_TOTAL not replaced", report.contains(PLACEHOLDER_VALUE_TOTAL))
        assertFalse("Placeholder $PLACEHOLDER_VALUE_ITEMS not replaced", report.contains(PLACEHOLDER_VALUE_ITEMS))
        println(report)
    }

    private fun assertChecksumEquals(subject: String, expected: Int, actual: Int) {
        assertEquals("$subject has been modified. Add necessary changes, test them manually and update checksum value", expected, actual)
    }

    private companion object {
        const val TEMPLATE_DIR = "/template"
        const val TEMPLATE_LINE = "$TEMPLATE_DIR/line.html"
        const val TEMPLATE_BLOCK = "$TEMPLATE_DIR/block.html"
        const val TEMPLATE_REPORT = "$TEMPLATE_DIR/report.html"
        const val TEMPLATE_LINE_CHECKSUM = -376419563
        const val TEMPLATE_BLOCK_CHECKSUM = 2088613418
        const val TEMPLATE_REPORT_CHECKSUM = -425549768
        const val PLACEHOLDER_VALUE_LINE = "#value-line"
        const val PLACEHOLDER_VALUE_COLOR = "#value-color"
        const val PLACEHOLDER_VALUE_DETAILS = "#value-details"
        const val PLACEHOLDER_VALUE_REDUCED = "#value-reduced"
        const val PLACEHOLDER_VALUE_FULL = "#value-full"
        const val PLACEHOLDER_VALUE_TOTAL = "#value-total"
        const val PLACEHOLDER_VALUE_ITEMS = "#value-items"
    }
}