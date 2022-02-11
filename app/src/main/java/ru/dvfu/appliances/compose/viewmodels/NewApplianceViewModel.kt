package ru.dvfu.appliances.compose.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ru.dvfu.appliances.compose.ui.theme.pickerColors

import ru.dvfu.appliances.model.repository.Repository
import ru.dvfu.appliances.model.repository.entity.Appliance
import ru.dvfu.appliances.ui.BaseViewState
import ru.dvfu.appliances.ui.Progress

class NewApplianceViewModel(private val repository: Repository): ViewModel() {

    private val _uiState = MutableStateFlow<BaseViewState>(BaseViewState.Success(null))
    val uiState: StateFlow<BaseViewState>
        get() = _uiState

    val noErrors = mutableStateOf<Boolean>(true)

    val title = mutableStateOf("")
    val description = mutableStateOf("")
    val selectedColor = mutableStateOf(pickerColors[0])

    private fun saveNewAppliance(appliance: Appliance) {
        _uiState.value = BaseViewState.Loading()

        viewModelScope.launch {
                repository.addAppliance(appliance).collect { progress ->
                    when (progress) {
                        is Progress.Complete -> {
                            _uiState.value = BaseViewState.Success(progress)
                        }
                        is Progress.Loading -> {
                            _uiState.value = BaseViewState.Loading(progress.percents)
                        }
                        is Progress.Error -> {
                            _uiState.value = BaseViewState.Error(progress.error)
                        }
                    }
            }
        }
    }

    fun isInputCorrect() = title.value.isNotBlank()

    fun createNewAppliance(): Boolean {
        return if (isInputCorrect()) {
            saveNewAppliance(
                Appliance(
                    name = title.value,
                    description = description.value,
                    color = selectedColor.value.hashCode(),
                )
            )
            true
        } else false
    }
}
