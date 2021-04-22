package ru.students.dvfu.mvp.presenter

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import moxy.MvpPresenter
import ru.students.dvfu.mvp.model.FirebaseUsersRepo
import ru.students.dvfu.mvp.model.IFirebaseUsersRepo
import ru.students.dvfu.mvp.view.LoginView


class LoginPresenter(
    private val loginView: LoginView,
    private val firebaseUsersRepo: IFirebaseUsersRepo
) : MvpPresenter<LoginView>() {

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

    fun successfulSignIn(user: FirebaseUser) {
        if (user.isAnonymous) {
            loginView.showSuccessToast("Guest")
        } else if (firebaseUsersRepo.isUserInDatabase(user.uid)) {
            loginView.showSuccessToast(user.email!!)
        } else {
            firebaseUsersRepo.putUserToDatabase(user)
            loginView.showSuccessToast(user.email!!)
        }
    }
}