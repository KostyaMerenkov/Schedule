package ru.students.dvfu.mvp.view

import com.google.firebase.auth.FirebaseAuth
import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.StateStrategyType

@StateStrategyType(AddToEndSingleStrategy::class)
interface LoginView: MvpView {

    fun setProgressVisibility(visibility: Boolean)
    fun startGoogleLogin()
    fun startGuestLogin()
    fun startMainActivity()
    fun firebaseAuthWithGoogle(idToken: String)

}