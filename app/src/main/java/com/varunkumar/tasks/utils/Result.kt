package com.varunkumar.tasks.utils

sealed class Result<T>(val data: T? = null, val message: String? = null) {
    class Success<T>(data: T?): Result<T>(data = data)
    class Loading<T>(): Result<T>()
    class Error<T>(message: String?): Result<T>(message = message)
}