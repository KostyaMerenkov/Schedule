package ru.dvfu.appliances.model.datasource

import com.google.android.gms.tasks.OnCompleteListener
import kotlinx.coroutines.CancellableContinuation
import kotlin.coroutines.resume

fun simpleOnCompleteListener(continuation: CancellableContinuation<Result<Unit>>) =
    OnCompleteListener<Void> {
        if (it.isSuccessful) continuation.resume(Result.success(Unit))
        else continuation.resume(Result.failure(it.exception ?: Throwable()))
    }