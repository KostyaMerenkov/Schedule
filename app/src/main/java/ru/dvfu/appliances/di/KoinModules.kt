package ru.dvfu.appliances.di

import ru.dvfu.appliances.model.datastore.UserDatastore
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.dvfu.appliances.Logger
import ru.dvfu.appliances.compose.utils.EventMapper
import ru.dvfu.appliances.application.SnackbarManager
import ru.dvfu.appliances.compose.viewmodels.BookingListViewModel
import ru.dvfu.appliances.compose.home.MainScreenViewModel
import ru.dvfu.appliances.compose.use_cases.*
import ru.dvfu.appliances.compose.viewmodels.*
import ru.dvfu.appliances.compose.viewmodels.ApplianceDetailsViewModel
import ru.dvfu.appliances.compose.viewmodels.LoginViewModel
import ru.dvfu.appliances.compose.viewmodels.MainViewModel
import ru.dvfu.appliances.compose.viewmodels.UserDetailsViewModel
import ru.dvfu.appliances.model.FirebaseMessagingViewModel
import ru.dvfu.appliances.model.datasource.*
import ru.dvfu.appliances.model.datasource.deprecated.CloudFirestoreDatabaseImpl
import ru.dvfu.appliances.model.datastore.UserDatastoreImpl
import ru.dvfu.appliances.model.repository.*
import ru.dvfu.appliances.model.repository_offline.OfflineRepository
import ru.dvfu.appliances.model.utils.RepositoryCollections

val repositoryModule = module {
    single<OfflineRepository> { OfflineRepositoryImpl(dbCollections = get()) }
    single<RepositoryCollections> { RepositoryCollections() }

    single<Repository> { CloudFirestoreDatabaseImpl(dbCollections = get()) }
    single<EventsRepository> { EventsRepositoryImpl(dbCollections = get()) }
    single<AppliancesRepository> { AppliancesRepositoryImpl(dbCollections = get()) }
    single<BookingRepository> { BookingRepositoryImpl(dbCollections = get()) }
    single<UsersRepository> {
        FirebaseUsersRepositoryImpl(
            androidContext(),
            dbCollections = get(),
            userDatastore = get()
        )
    }
}

val application = module {
    single<UserDatastore> { UserDatastoreImpl(androidContext()) }
    viewModel { MainViewModel() }
    single {
        FirebaseMessagingViewModel(
            getUserUseCase = get(),
            usersRepository = get(),
            userDatastore = get(),
            appliancesRepository = get()
        )
    }

    single { Logger() }
    single { SnackbarManager }



    factory { GetApplianceUseCase(offlineRepository = get(), appliancesRepository = get()) }
    factory { GetAppliancesUseCase(offlineRepository = get(), appliancesRepository = get()) }
    factory { GetUserUseCase(get(), get()) }
    factory { GetEventNewTimeEndAvailabilityUseCase(get()) }
    factory { GetNewEventTimeAvailabilityUseCase(get()) }
    factory { GetDateEventsUseCase(get()) }
    factory { GetEventsFromDateUseCase(get()) }
    factory { GetPeriodEventsUseCase(get()) }
    factory { UpdateEventStatusUseCase(get()) }
    factory { UpdateEventUseCase(get()) }

    single { EventMapper(get(), get()) }
}

val mainActivity = module {
    viewModel { UsersViewModel(get()) }
    viewModel {
        BookingListViewModel(
            getUserUseCase = get(),
            getApplianceUseCase = get(),
            userDatastore = get(),
            updateEvent = get(),
            eventsRepository = get()
        )
    }

    viewModel { LoginViewModel(get(), get()) }

    viewModel {
        MainScreenViewModel(
            usersRepository = get(),
            eventsRepository = get(),
            offlineRepository = get(),
            userDatastore = get(),
            getDateEventsUseCase = get(),
            getUserUseCase = get(),
            getApplianceUseCase = get()
        )
    }
    viewModel {
        WeekCalendarViewModel(
            usersRepository = get(),
            eventsRepository = get(),
            offlineRepository = get(),
            userDatastore = get(),
            getDateEventsUseCase = get(),
            getEventsFromDateUseCase = get(),
            getPeriodEventsUseCase = get(),
            eventMapper = get()
        )
    }

    viewModel {
        UserDetailsViewModel(
            detUser = it[0],
            usersRepository = get(),
            repository = get(),
            userDatastore = get()
        )
    }


    viewModel { ProfileViewModel(get(), get()) }

    //Appliances
    viewModel { ApplianceDetailsViewModel(get(), get(), get()) }
    viewModel { NewApplianceViewModel(get(), get()) }
    //viewModel { ApplianceUsersViewModel(get()) }
    viewModel { AppliancesViewModel(get(), get(), get()) }
    viewModel { AddUserViewModel(it.get(), it.get(), get(), get()) }
    viewModel {
        AddEventViewModel(
            selectedDate = it[0],
            eventsRepository = get(),
            getAppliancesUseCase = get(),
            userDatastore = get(),
            getNewEventTimeAvailabilityUseCase = get()
        )
    }
    /*viewModel {
        AddBookingViewModel(
            selectedDate = it[0],
            bookingRepository = get(),
            getAppliancesUseCase = get(),
            userDatastore = get()
        )
    }*/
    viewModel {
        EventInfoViewModel(
            eventArg = it.get(),
            userDatastore = get(),
            eventsRepository = get(),
            getApplianceUseCase = get(),
            getUserUseCase = get(),
            getEventNewTimeEndAvailabilityUseCase = get()
        )
    }
    viewModel {
        EditProfileViewModel(userDatastore = get(), userRepository = get())
    }
}

