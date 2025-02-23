package com.okediran.administrator.data.repositories

import com.okediran.administrator.data.models.Pupil
import com.okediran.administrator.data.models.PupilRequest
import com.okediran.administrator.data.models.PupilResponse
import com.okediran.administrator.data.remote.PupilApiService
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class PupilRepository @Inject constructor(private val api: PupilApiService) : IPupilRepository {

    override suspend fun fetchPupils(): PupilResponse {
        return try {
            val response = api.getPupils()
            if (response.isSuccessful) response.body()
                ?: PupilResponse(emptyList()) else PupilResponse(emptyList())
        } catch (e: Exception) {
            PupilResponse(emptyList())
        }
    }


    override suspend fun addPupil(pupil: PupilRequest): Pupil? {
        return try {
            val response = api.createPupil(pupil)
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun getPupil(id: Int): Pupil? {
        return try {
            val response = api.getPupil(id)
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun updatePupil(id: Int, pupil: PupilRequest): Pupil? {
        return try {
            val response = api.updatePupil(id, pupil)
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun deletePupil(id: Int): Boolean {
        return try {
            val response = api.deletePupil(id)
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }
}