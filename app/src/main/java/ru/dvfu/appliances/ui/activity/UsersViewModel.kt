package ru.dvfu.appliances.ui.activity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import ru.dvfu.appliances.model.repository.Repository
import ru.dvfu.appliances.model.repository.entity.User

class UsersViewModel(private val repository: Repository) : ViewModel() {

    private val _usersMutableLiveData: MutableLiveData<List<User>> = MutableLiveData()
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

    fun subscribeUsers(): LiveData<List<User>> { return _usersMutableLiveData }
    fun subscribeLoading(): LiveData<Boolean> { return _loadingMutableLiveData }


    fun getData() {
        _loadingMutableLiveData.postValue(true)
        viewModelScope.coroutineContext.cancelChildren()
        viewModelScope.launch(Dispatchers.Default) {
            //_usersMutableLiveData.postValue(databaseProvider.getUsers())
            _loadingMutableLiveData.postValue(false)
        }
    }

//    private fun loadData() {
//        setProgress(true)
//        firebaseUsersRepo.getUsers()
//            .observeOn(Schedulers.io())
//            .subscribe({ users ->
//                usersListPresenter.users.clear()
//                usersListPresenter.users.addAll(users)
//                viewState.updateList()
//                viewState.setProgress(false)
//            }, {
//                println("Error: ${it.message}")
//                viewState.setProgress(false)
//            })
//    }
}