package ru.fomenkov.rxdisposablewatcher

data class Element(val className: String, val method: String, val line: Int) {

    override fun toString() = "$className.$method:$line"
}