package ru.dvfu.appliances.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.dvfu.appliances.model.repository.Repository
import ru.dvfu.appliances.model.repository.FirestoreDatabase
import ru.dvfu.appliances.ui.activity.AppliancesViewModel
import ru.dvfu.appliances.ui.activity.UsersViewModel

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