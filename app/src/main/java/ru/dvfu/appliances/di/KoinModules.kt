package ru.dvfu.appliances.di

import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.dvfu.appliances.Logger
import ru.dvfu.appliances.compose.viewmodels.AppliancesViewModel
import ru.dvfu.appliances.compose.viewmodels.ProfileViewModel
import ru.dvfu.appliances.compose.viewmodels.UsersViewModel
import ru.dvfu.appliances.model.repository.CloudFirestoreDatabaseImpl
import ru.dvfu.appliances.model.repository.DatabaseProvider
import ru.dvfu.appliances.model.repository.FirebaseUserRepositoryImpl
import ru.dvfu.appliances.model.repository.UserRepository
import ru.dvfu.appliances.model.viewmodels.LoginViewModel
import ru.dvfu.appliances.model.viewmodels.MainViewModel

val application = module {
    viewModel { MainViewModel() }

    single<DatabaseProvider> { CloudFirestoreDatabaseImpl() }
    single<UserRepository> { FirebaseUserRepositoryImpl(androidContext()) }

    single { Logger() }
}

val mainActivity = module {
    viewModel { LoginViewModel(get(), get()) }
    viewModel { UsersViewModel(get()) }
    viewModel { AppliancesViewModel(get(), get()) }
    viewModel { ProfileViewModel(get()) }
}