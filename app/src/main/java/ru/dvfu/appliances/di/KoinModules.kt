package ru.dvfu.appliances.di

import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.dvfu.appliances.compose.viewmodels.ProfileViewModel
import ru.dvfu.appliances.model.auth.AuthManager
import ru.dvfu.appliances.model.auth.FirebaseAuthManagerImpl
import ru.dvfu.appliances.model.repository.*
import ru.dvfu.appliances.model.viewmodels.LoginViewModel
import ru.dvfu.appliances.model.viewmodels.MainViewModel
import ru.dvfu.appliances.ui.activity.AppliancesViewModel
import ru.dvfu.appliances.ui.activity.UsersViewModel

val application = module {
    viewModel { MainViewModel() }
    single<DatabaseProvider> { CloudFirestoreDatabaseImpl() }
    single<AuthManager> { FirebaseAuthManagerImpl(androidContext()) }
    single<UserRepository> { UserRepositoryImpl(get(), get()) }
    //single<UserContentRepository> { UserContentRepositoryImpl(get()) }
}

val loginScreen = module {
    viewModel {LoginViewModel(get(), get())}
    single { UserRepositoryImpl(get(), get()) }
}
val usersScreen = module {
    viewModel { UsersViewModel(get()) }
}

val appliancesScreen = module {
    viewModel { AppliancesViewModel(get()) }
}
val profileScreen = module {
    viewModel { ProfileViewModel(get()) }
}