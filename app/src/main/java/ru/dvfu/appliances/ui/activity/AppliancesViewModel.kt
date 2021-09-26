package ru.dvfu.appliances.ui.activity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import ru.dvfu.appliances.model.repository.DatabaseProvider
import ru.dvfu.appliances.model.userdata.entities.Appliance

class AppliancesViewModel(private val databaseProvider: DatabaseProvider) : ViewModel() {

    private val _appliancesMutableLiveData: MutableLiveData<List<Appliance>> = MutableLiveData()
    private val _loadingMutableLiveData: MutableLiveData<Boolean> = MutableLiveData()

//    private val viewModelCoroutineScope = CoroutineScope(
//        Dispatchers.Main
//                + SupervisorJob()
//                + CoroutineExceptionHandler { _, throwable ->
//            handleError(throwable)
//        })

    private fun handleError(throwable: Throwable) {
        TODO()
    }

    fun subscribeAppliances(): LiveData<List<Appliance>> { return _appliancesMutableLiveData }
    fun subscribeLoading(): LiveData<Boolean> { return _loadingMutableLiveData }


    fun getData() {
        _loadingMutableLiveData.postValue(true)
        viewModelScope.coroutineContext.cancelChildren()
        viewModelScope.launch(Dispatchers.Default) {
            _appliancesMutableLiveData.postValue(listOf(
                Appliance("Ultra"),
                Appliance("Evo")
            ))
            _loadingMutableLiveData.postValue(false)
        }
    }
}