package ru.dvfu.appliances.di

import ru.dvfu.appliances.model.datastore.UserDatastore
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.dvfu.appliances.Logger
import ru.dvfu.appliances.application.SnackbarManager
import ru.dvfu.appliances.compose.home.MainScreenViewModel
import ru.dvfu.appliances.compose.use_cases.GetApplianceUseCase
import ru.dvfu.appliances.compose.use_cases.GetAppliancesUseCase
import ru.dvfu.appliances.compose.use_cases.GetUserUseCase
import ru.dvfu.appliances.compose.viewmodels.*
import ru.dvfu.appliances.compose.viewmodels.ApplianceDetailsViewModel
import ru.dvfu.appliances.compose.viewmodels.LoginViewModel
import ru.dvfu.appliances.compose.viewmodels.MainViewModel
import ru.dvfu.appliances.compose.viewmodels.UserDetailsViewModel
import ru.dvfu.appliances.model.datasource.*
import ru.dvfu.appliances.model.datastore.UserPreferencesImpl
import ru.dvfu.appliances.model.repository.*
import ru.dvfu.appliances.model.repository_offline.OfflineRepository
import ru.dvfu.appliances.model.utils.RepositoryCollections

val application = module {
    single { RepositoryCollections() }
    single<UserDatastore> { UserPreferencesImpl(androidContext()) }
    viewModel { MainViewModel() }

    single<OfflineRepository> { OfflineRepositoryImpl(dbCollections = get()) }

    single<Repository> { CloudFirestoreDatabaseImpl(dbCollections = get()) }
    single<EventsRepository> { EventsRepositoryImpl(dbCollections = get()) }
    single<AppliancesRepository> { AppliancesRepositoryImpl(dbCollections = get()) }
    single<BookingRepository> { BookingRepositoryImpl(dbCollections = get()) }
    single<UsersRepository> { FirebaseUsersRepositoryImpl(androidContext(), dbCollections = get(), userDatastore = get()) }

    single { Logger() }
    single { SnackbarManager }



    factory { GetApplianceUseCase(offlineRepository = get(), appliancesRepository = get()) }
    factory { GetAppliancesUseCase(offlineRepository = get(), appliancesRepository = get()) }
    factory { GetUserUseCase(get(), get()) }
}

val mainActivity = module {
    viewModel { UsersViewModel(get()) }

    viewModel { LoginViewModel(get(), get()) }

    viewModel { MainScreenViewModel(get(), get(), get()) }

    viewModel { UserDetailsViewModel(get(), get()) }


    viewModel { ProfileViewModel(get(), get()) }

    //Appliances
    viewModel { ApplianceDetailsViewModel(get(), get()) }
    viewModel { NewApplianceViewModel(get()) }
    //viewModel { ApplianceUsersViewModel(get()) }
    viewModel { AppliancesViewModel(get(), get()) }
    viewModel { AddUserViewModel(it.get(), it.get(), get(), get()) }
    viewModel {
        AddEventViewModel(
            usersRepository = get(),
            appliancesRepository = get(),
            eventsRepository = get(),
            offlineRepository = get()
        )
    }
    viewModel {
        AddBookingViewModel(
            usersRepository = get(),
            appliancesRepository = get(),
            bookingRepository = get(),
            offlineRepository = get(),
            userDatastore = get()
        )
    }
    viewModel {
        EventInfoViewModel(
            eventArg = it.get(),
            usersRepository = get(),
            appliancesRepository = get(),
            eventsRepository = get(),
            offlineRepository = get(),
            getApplianceUseCase = get(),
            getUserUseCase = get(),
        )
    }
}

