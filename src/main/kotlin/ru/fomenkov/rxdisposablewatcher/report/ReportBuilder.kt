package ru.fomenkov.rxdisposablewatcher.report

interface ReportBuilder<T> {

    fun build(): T
}