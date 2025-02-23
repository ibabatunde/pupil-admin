package com.okediran.administrator.extensions

import retrofit2.HttpException
import java.net.ConnectException
import java.net.MalformedURLException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeoutException

object ErrorHandler {
    inline fun <reified ModelClass> parse(throwable: Throwable): String {
        return when (throwable) {
            is TimeoutException, is SocketTimeoutException -> "Connection timed out. Please check your network and try again"
            is ConnectException -> "Couldn't connect, please check your internet"
            is UnknownHostException -> "Could not connect. Please check if your internet is enabled"
            is MalformedURLException -> "Url seem to be invalid. Please check and try again"
            is HttpException -> {
                val errorString = throwable.response()?.errorBody()?.string()
                if (errorString != null) parseErrorString<ModelClass>(errorString) else "${throwable.code()} Unknown exception returned from server"
            }
            else -> throwable.localizedMessage ?: "Unknown error"
        }
    }

     inline fun <reified T> parseErrorString(json: String): String {
        return kotlin.runCatching {
            val error = json.toObject<T>()
            error?.toString() ?: "Unknown error occurred"
        }.getOrElse {
            if (json.length < 500) json else "Couldn't decipher error returned from server. Please try again"
        }
    }
}