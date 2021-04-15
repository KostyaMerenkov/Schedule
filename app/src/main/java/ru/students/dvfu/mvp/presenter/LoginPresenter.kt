package ru.students.dvfu.mvp.presenter

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

    fun successfulSignIn(email: String?) {
        email?.let {
            loginView.showSuccessToast(email)
        } ?: loginView.showSuccessToast("Guest")
    }
}