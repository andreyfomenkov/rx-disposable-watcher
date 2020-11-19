package ru.fomenkov.rxdisposablewatcher

interface ReportBuilder<T> {

    fun build(): T
}