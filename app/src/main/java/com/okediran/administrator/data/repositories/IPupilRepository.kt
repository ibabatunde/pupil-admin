package com.okediran.administrator.data.repositories

import com.okediran.administrator.data.models.Pupil
import com.okediran.administrator.data.models.PupilRequest
import com.okediran.administrator.data.models.PupilResponse

interface IPupilRepository {

    suspend fun fetchPupils(): PupilResponse
    suspend fun addPupil(pupil: PupilRequest): Pupil?
    suspend fun getPupil(id: Int): Pupil?
    suspend fun updatePupil(id: Int, pupil: PupilRequest): Pupil?
    suspend fun deletePupil(id: Int): Boolean
}