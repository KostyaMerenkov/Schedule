package ru.students.dvfu.mvp.view

import com.google.android.material.circularreveal.CircularRevealHelper
import com.google.firebase.auth.FirebaseUser
import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.StateStrategyType

@StateStrategyType(AddToEndSingleStrategy::class)
interface MainView: MvpView {
}