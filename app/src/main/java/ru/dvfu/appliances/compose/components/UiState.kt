package ru.dvfu.appliances.compose.components

sealed class UiState {
    object InProgress : UiState()
    object Error : UiState()
    object Success : UiState()
}