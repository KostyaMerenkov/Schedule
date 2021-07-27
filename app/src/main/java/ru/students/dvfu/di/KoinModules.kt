package ru.students.dvfu.di

import androidx.core.app.AppLaunchChecker
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.students.dvfu.model.repository.Repository
import ru.students.dvfu.model.repository.FirestoreDatabase
import ru.students.dvfu.ui.activity.AppliancesViewModel
import ru.students.dvfu.ui.activity.UsersViewModel

//fun injectDependencies() = loadModules

//private val loadModules by lazy {
//    // Функция библиотеки Koin
//    loadKoinModules(listOf(application, usersScreen))
//}

val application = module {
    single<Repository> { FirestoreDatabase() }
}

val usersScreen = module {
    viewModel { UsersViewModel(get()) }
}

val appliancesScreen = module {
    viewModel { AppliancesViewModel(get()) }
}