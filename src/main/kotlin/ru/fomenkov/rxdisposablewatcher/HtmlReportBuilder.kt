package ru.fomenkov.rxdisposablewatcher

import java.io.File

class HtmlReportBuilder(
    private val probe: List<ProbeEntry>,
    private val reducedBlockSize: Int = DEFAULT_REDUCED_BLOCK_SIZE
) : ReportBuilder<String> {

    private val template: Template

    init {
        template = Template(
            line = File(TEMPLATE_LINE).readText(),
            block = File(TEMPLATE_BLOCK).readLines(),
            report = File(TEMPLATE_REPORT).readLines()
        )
    }

    override fun build(): String {
        val blocks = mutableListOf<String>()
        val version = File("version").readText().trim()
        val total = probe.sumBy { it.entries }

        probe.forEach { item ->
            val lines = mutableListOf<String>()
            val color = getColor(item.source)
            val details = getDetails(item.source, item.entries)

            item.stackTrace.elements().forEach { element ->
                lines += createLine("${element.className}.${element.method}:${element.line}")
            }
            blocks += createBlock(lines, reducedBlockSize, color, details)
        }
        return createReport(blocks, version, total)
    }

    private fun getDetails(sourceType: SourceType, entries: Int) =
        "${sourceType.className}, $entries subscription(s) alive"

    private fun getColor(sourceType: SourceType) = when (sourceType) {
        SourceType.OBSERVABLE -> "#ffff00"
        SourceType.SINGLE -> "#33ee33"
        SourceType.COMPLETABLE -> "#ffffff"
        SourceType.MAYBE -> "#5555ff"
        SourceType.FLOWABLE -> "#ff0000"
    }

    private fun createReport(blocks: List<String>, version: String, total: Int): String {
        val builder = StringBuilder()
        val itemsPlaceholderIndex = template.report.indexOfFirst { it.contains(PLACEHOLDER_VALUE_ITEMS) }

        check(itemsPlaceholderIndex != -1) { "Placeholder $PLACEHOLDER_VALUE_ITEMS not found" }

        template.report.forEachIndexed { position, item ->
            when (position) {
                itemsPlaceholderIndex -> blocks.forEach { block ->
                    builder.append("$block\n\n")
                }
                else -> builder.append("$item\n")
            }
        }
        return builder
            .toString()
            .replace(PLACEHOLDER_VALUE_VERSION, version)
            .replace(PLACEHOLDER_VALUE_TOTAL, total.toString())
            .trim()
    }

    private fun createBlock(lines: List<String>, reducedSize: Int, color: String, details: String): String {
        val reducedPlaceholderIndex = template.block.indexOfFirst { it.contains(PLACEHOLDER_VALUE_REDUCED) }
        val fullPlaceholderIndex = template.block.indexOfFirst { it.contains(PLACEHOLDER_VALUE_FULL) }

        require(color.startsWith("#")) { "Color value must start with #" }
        check(lines.isNotEmpty()) { "No lines provided" }
        check(reducedSize > 0) { "Value for reduced size must be > 0" }
        check(reducedPlaceholderIndex > 0) { "Placeholder $PLACEHOLDER_VALUE_REDUCED not found" }
        check(fullPlaceholderIndex > 0) { "Placeholder $PLACEHOLDER_VALUE_FULL not found" }

        val reducedList = when (reducedSize >= lines.size) {
            true -> lines
            else -> lines.subList(0, reducedSize)
        }
        val indentation = template.block[reducedPlaceholderIndex].replace(PLACEHOLDER_VALUE_REDUCED, "")
        val builder = StringBuilder()

        template.block.forEachIndexed { position, item ->
            when (position) {
                reducedPlaceholderIndex -> reducedList.forEach { line ->
                    builder.append("$indentation$line\n")
                }
                fullPlaceholderIndex -> lines.forEach { line ->
                    builder.append("$indentation$line\n")
                }
                else -> builder.append("$item\n")
            }
        }
        return builder
            .toString()
            .replace(PLACEHOLDER_VALUE_COLOR, color)
            .replace(PLACEHOLDER_VALUE_DETAILS, details)
            .trim()
    }

    private fun createLine(value: String) = template.line.replace(PLACEHOLDER_VALUE_LINE, value)

    private companion object {
        const val TEMPLATE_DIR = "template"
        const val TEMPLATE_LINE = "$TEMPLATE_DIR/line.html"
        const val TEMPLATE_BLOCK = "$TEMPLATE_DIR/block.html"
        const val TEMPLATE_REPORT = "$TEMPLATE_DIR/report.html"
        const val PLACEHOLDER_VALUE_LINE = "#value-line"
        const val PLACEHOLDER_VALUE_COLOR = "#value-color"
        const val PLACEHOLDER_VALUE_DETAILS = "#value-details"
        const val PLACEHOLDER_VALUE_REDUCED = "#value-reduced"
        const val PLACEHOLDER_VALUE_FULL = "#value-full"
        const val PLACEHOLDER_VALUE_VERSION = "#value-version"
        const val PLACEHOLDER_VALUE_TOTAL = "#value-total"
        const val PLACEHOLDER_VALUE_ITEMS = "#value-items"
        const val DEFAULT_REDUCED_BLOCK_SIZE = 3
    }

    private data class Template(val line: String, val block: List<String>, val report: List<String>)
}