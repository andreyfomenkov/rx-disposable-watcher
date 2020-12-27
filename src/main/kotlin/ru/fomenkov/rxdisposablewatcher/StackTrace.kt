package ru.fomenkov.rxdisposablewatcher

class StackTrace {

    private val elements: List<Element>
    private var string = ""
    private var hashCode = 0

    constructor(trace: Array<StackTraceElement>) {
        elements = trace.map {
            Element(className = it.className, method = it.methodName, line = it.lineNumber)
        }
        init()
    }

    constructor(trace: List<Element>) {
        elements = trace
        init()
    }

    private fun init() {
        val builder = StringBuilder()
        filterStackTrace(elements).forEach { element -> builder.append(element).append("\n") }
        string = builder.toString()
        hashCode = string.hashCode()
    }

    fun crop(predicate: (Element) -> Boolean): StackTrace {
        val start = elements.indexOfLast(predicate) + 1
        val end = elements.size
        check(start >= 0) { "Required element not found in stack trace" }
        return StackTrace(elements.subList(start, end))
    }

    fun elements() = elements

    private fun filterStackTrace(list: List<Element>) = list
        .filterNot { element ->
            EXCLUDED_PACKAGE_NAMES.forEach { packageName ->
                if (element.className.startsWith(packageName)) {
                    return@filterNot true
                }
            }
            false
        }

    override fun equals(other: Any?) = when (other is StackTrace) {
        true -> {
            val otherList = filterStackTrace(other.elements)
            val currList = filterStackTrace(elements)

            when (otherList.size == currList.size) {
                true -> otherList == currList
                else -> false
            }
        }
        else -> false
    }

    override fun hashCode() = hashCode

    override fun toString() = string

    private companion object {
        val EXCLUDED_PACKAGE_NAMES = setOf(
            "android.",
            "androidx.",
            "java.lang.",
            "com.android."
        )
    }
}