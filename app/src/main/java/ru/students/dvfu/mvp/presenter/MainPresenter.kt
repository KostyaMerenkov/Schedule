package ru.students.dvfu.mvp.presenter

import com.google.firebase.auth.FirebaseUser
import moxy.MvpPresenter
import ru.students.dvfu.mvp.view.MainView

class MainPresenter(private val mainView: MainView): MvpPresenter<MainView>() {
    fun logOutClicked() {
        TODO("Not yet implemented")
    }

}