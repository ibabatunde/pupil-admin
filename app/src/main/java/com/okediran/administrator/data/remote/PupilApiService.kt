package com.okediran.administrator.data.remote

import com.okediran.administrator.data.models.Pupil
import com.okediran.administrator.data.models.PupilRequest
import com.okediran.administrator.data.models.PupilResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path


interface PupilApiService {
    @GET("pupils")
    suspend fun getPupils(): Response<PupilResponse>

    @POST("pupils")
    suspend fun createPupil(@Body pupil: PupilRequest): Response<Pupil>

    @GET("pupils/{id}")
    suspend fun getPupil(@Path("id") id: Int): Response<Pupil>

    @PUT("pupils/{id}")
    suspend fun updatePupil(@Path("id") id: Int, @Body pupil: PupilRequest): Response<Pupil>

    @DELETE("pupils/{id}")
    suspend fun deletePupil(@Path("id") id: Int): Response<Unit>
}