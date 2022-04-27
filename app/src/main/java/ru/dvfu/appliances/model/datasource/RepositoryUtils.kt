package ru.dvfu.appliances.model.datasource

import com.google.android.gms.tasks.OnCompleteListener
import kotlinx.coroutines.CancellableContinuation
import ru.dvfu.appliances.model.repository.entity.User
import kotlin.coroutines.resume

fun simpleOnCompleteListener(continuation: CancellableContinuation<Result<Unit>>, onSuccess: () -> Unit = {}) =
    OnCompleteListener<Void> {
        if (it.isSuccessful) {
            onSuccess()
            continuation.resume(Result.success(Unit))
        }
        else continuation.resume(Result.failure(it.exception ?: Throwable()))
    }