package ru.fomenkov.rxdisposablewatcher

data class Element(val className: String, val method: String, val line: Int) {

    override fun toString() = "$className.$method:$line"
}

fun parse(line: String): Element {
    val classEndIndex = line.lastIndexOf('.')
    val methodEndIndex = line.indexOf(':')
    check(classEndIndex > 0 && methodEndIndex > 0) { "Failed to parse Element" }
    return Element(
        className = line.substring(0, classEndIndex),
        method = line.substring(classEndIndex + 1, methodEndIndex),
        line = line.substring(methodEndIndex + 1, line.length).toInt()
    )
}

fun parse(vararg lines: String) = lines.map { line -> parse(line) }