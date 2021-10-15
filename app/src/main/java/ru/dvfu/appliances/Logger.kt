package ru.dvfu.appliances

import android.util.Log

class Logger {
    fun log(message: String?) {
        Log.d("Fishing", message ?: "No error message")
    }
}