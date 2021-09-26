package ru.dvfu.appliances.ui

sealed class Progress {
    class Loading(val percents: Int = 0) : Progress()
    object Complete: Progress()
    class Error(val error: Throwable): Progress()
}