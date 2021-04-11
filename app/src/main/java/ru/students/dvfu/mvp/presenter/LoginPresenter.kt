package ru.students.dvfu.mvp.presenter

import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import moxy.MvpPresenter
import ru.students.dvfu.mvp.view.LoginView


class LoginPresenter(private val loginView: LoginView) : MvpPresenter<LoginView>() {

    private var TAG = "LoginPresenter"

    fun googleAuthClicked() {
        loginView.startGoogleLogin()
    }

    fun microsoftSignInClicked() {
        TODO("Not yet implemented")
    }

    fun guestAuthClicked(auth: FirebaseAuth) {
        loginView.startGuestLogin()
        loginView.setProgressVisibility(true)
    }
}