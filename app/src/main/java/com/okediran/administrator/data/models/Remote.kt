package com.okediran.administrator.data.models

import com.google.gson.annotations.SerializedName

data class PupilResponse(val items: List<Pupil>)
data class Pupil(val pupilId: Int?, val country: String, val name: String, val image: String, val latitude: Double, val longitude: Double)
data class PupilRequest(val country: String, val name: String, val image: String, val latitude: Double, val longitude: Double)

sealed class ResultState<T> {
    data class Loading<T>(val message: String = "Please wait") : ResultState<T>()
    data class Error<T>(val error: String) : ResultState<T>()
    data class Success<T>(val data: T) : ResultState<T>()
    data class Empty<T>(val data: T) : ResultState<T>()
    class Idle<T>: ResultState<T>()
}

data class Error(
    @SerializedName("message")
    val message: String?
) {
    override fun toString(): String = message.orEmpty()
}