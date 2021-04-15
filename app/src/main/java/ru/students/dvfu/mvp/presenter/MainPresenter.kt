package ru.students.dvfu.mvp.presenter

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import moxy.MvpPresenter
import ru.students.dvfu.mvp.view.MainView

class MainPresenter(private val mainView: MainView): MvpPresenter<MainView>() {
    fun logOutClicked(mAuth: FirebaseAuth) {
        mainView.signOut()
        //TODO("Проверка пользователя. Если гость, то удалить профиль и выйти")
    }

}