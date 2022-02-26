package ru.dvfu.appliances.di

import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.dvfu.appliances.Logger
import ru.dvfu.appliances.application.SnackbarManager
import ru.dvfu.appliances.compose.home.MainScreenViewModel
import ru.dvfu.appliances.compose.viewmodels.*
import ru.dvfu.appliances.compose.viewmodels.ApplianceDetailsViewModel
import ru.dvfu.appliances.compose.viewmodels.LoginViewModel
import ru.dvfu.appliances.compose.viewmodels.MainViewModel
import ru.dvfu.appliances.compose.viewmodels.UserDetailsViewModel
import ru.dvfu.appliances.model.datasource.*
import ru.dvfu.appliances.model.repository.*
import ru.dvfu.appliances.model.repository_offline.OfflineRepository
import ru.dvfu.appliances.model.utils.RepositoryCollections

val application = module {
    single { RepositoryCollections() }
    viewModel { MainViewModel() }

    single<OfflineRepository> { OfflineRepositoryImpl(dbCollections = get()) }

    single<Repository> { CloudFirestoreDatabaseImpl(dbCollections = get()) }
    single<EventsRepository> { EventsRepositoryImpl(dbCollections = get()) }
    single<AppliancesRepository> { AppliancesRepositoryImpl(dbCollections = get()) }
    single<UsersRepository> { FirebaseUsersRepositoryImpl(androidContext(), dbCollections = get()) }

    single { Logger() }
    single { SnackbarManager }
}

val mainActivity = module {
    viewModel { LoginViewModel(get(), get()) }

    viewModel { MainScreenViewModel(get()) }

    viewModel { UserDetailsViewModel(get(), get()) }
    viewModel { UsersViewModel(get()) }

    viewModel { ProfileViewModel(get()) }

    //Appliances
    viewModel { ApplianceDetailsViewModel(get(),get()) }
    viewModel { NewApplianceViewModel(get()) }
    //viewModel { ApplianceUsersViewModel(get()) }
    viewModel { AppliancesViewModel(get(), get()) }
    viewModel { AddUserViewModel(it.get(), it.get(), get(), get()) }
    viewModel { AddEventViewModel(get(), get(), get()) }
}

/*
Process: ru.dvfu.appliances, PID: 19101
java.lang.RuntimeException: Could not deserialize object. Class ru.dvfu.appliances.model.repository.entity.Event does not define a no-argument constructor. If you are using ProGuard, make sure these constructors are not stripped
at com.google.firebase.firestore.util.CustomClassMapper.deserializeError(CustomClassMapper.java:563)
at com.google.firebase.firestore.util.CustomClassMapper.access$200(CustomClassMapper.java:54)
at com.google.firebase.firestore.util.CustomClassMapper$BeanMapper.deserialize(CustomClassMapper.java:749)
at com.google.firebase.firestore.util.CustomClassMapper$BeanMapper.deserialize(CustomClassMapper.java:741)
at com.google.firebase.firestore.util.CustomClassMapper.convertBean(CustomClassMapper.java:542)
at com.google.firebase.firestore.util.CustomClassMapper.deserializeToClass(CustomClassMapper.java:253)
at com.google.firebase.firestore.util.CustomClassMapper.convertToCustomClass(CustomClassMapper.java:100)
at com.google.firebase.firestore.DocumentSnapshot.toObject(DocumentSnapshot.java:183)
at com.google.firebase.firestore.QueryDocumentSnapshot.toObject(QueryDocumentSnapshot.java:116)
at com.google.firebase.firestore.QuerySnapshot.toObjects(QuerySnapshot.java:184)
at com.google.firebase.firestore.QuerySnapshot.toObjects(QuerySnapshot.java:166)
at ru.dvfu.appliances.model.datasource.EventsRepositoryImpl.getEventsSuccessListener$lambda-1(EventsRepositoryImpl.kt:64)
at ru.dvfu.appliances.model.datasource.EventsRepositoryImpl.$r8$lambda$uAWxH3pApi2reRc2I3fqZIIUwcA(Unknown Source:0)
at ru.dvfu.appliances.model.datasource.EventsRepositoryImpl$$ExternalSyntheticLambda1.onEvent(Unknown Source:4)
at com.google.firebase.firestore.Query.lambda$addSnapshotListenerInternal$2(Query.java:1133)
at com.google.firebase.firestore.Query$$Lambda$3.onEvent(Unknown Source:6)
at com.google.firebase.firestore.core.AsyncEventListener.lambda$onEvent$0(AsyncEventListener.java:42)
at com.google.firebase.firestore.core.AsyncEventListener$$Lambda$1.run(Unknown Source:6)
at android.os.Handler.handleCallback(Handler.java:938)
at android.os.Handler.dispatchMessage(Handler.java:99)
at android.os.Looper.loopOnce(Looper.java:233)
at android.os.Looper.loop(Looper.java:344)
at android.app.ActivityThread.main(ActivityThread.java:8184)
at java.lang.reflect.Method.invoke(Native Method)
at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:584)
at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:1034)
2022-02-20 13:52:05.134 19101-19101/? I/Process: Sending signal. PID: 19101 SIG: 9*/
