package ru.dvfu.appliances.ui

sealed class BaseViewState {
    class Success<T>(val data: T) : BaseViewState()
    class Error(val error: Throwable) : BaseViewState()
    class Loading(val progress: Int? = null) : BaseViewState()
}

sealed class ViewState<out T> {
    class Success<T>(val data: T) : ViewState<T>()
    class Error(val error: Throwable) : ViewState<Nothing>()
    object Loading : ViewState<Nothing>()
}
