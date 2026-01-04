package com.example.projectmanagement.datageneral.domain.utilities

inline fun <T> Result<T>.mapFailure(mapper: (Throwable) -> Throwable): Result<T> =
    fold(
        onSuccess = { Result.success(it) },
        onFailure = { Result.failure(mapper(it))}
    )