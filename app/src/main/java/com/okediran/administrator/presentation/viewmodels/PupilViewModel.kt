package com.okediran.administrator.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.okediran.administrator.data.models.Pupil
import com.okediran.administrator.data.models.PupilRequest
import com.okediran.administrator.data.models.PupilResponse
import com.okediran.administrator.data.models.ResultState
import com.okediran.administrator.data.repositories.IPupilRepository
import com.okediran.administrator.data.repositories.PupilRepository
import com.okediran.administrator.extensions.handleException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PupilViewModel @Inject constructor(private val repository: PupilRepository) : ViewModel() {

    private val _pupilsState = MutableStateFlow<ResultState<PupilResponse>>(ResultState.Idle())
    val pupilsState: StateFlow<ResultState<PupilResponse>> = _pupilsState

    private val _creationState = MutableStateFlow<ResultState<Unit>>(ResultState.Idle())
    val creationState: StateFlow<ResultState<Unit>> = _creationState

    private val _pupilState = MutableStateFlow<ResultState<Pupil>>(ResultState.Loading())
    val pupilState: StateFlow<ResultState<Pupil>> = _pupilState

    private val _updateState = MutableStateFlow<ResultState<Unit>>(ResultState.Idle())
    val updateState: StateFlow<ResultState<Unit>> = _updateState

    init {
        fetchPupils()
    }

    fun fetchPupils() {
        val handler = handleException { error ->
            _pupilsState.value = ResultState.Error(error)
        }
        viewModelScope.launch(handler) {
            _pupilsState.value = ResultState.Loading()
            val pupils = repository.fetchPupils()
            when {
                pupils.items.isEmpty() -> {
                    _pupilsState.value = ResultState.Empty(pupils)
                }
                else -> {
                    _pupilsState.value = ResultState.Success(pupils)
                }
            }
        }
    }

    fun createPupil(name: String, country: String, imageUri: String) {
        val handler = handleException { error ->
            _creationState.value = ResultState.Error(error)
        }
        viewModelScope.launch(handler) {
            _creationState.value = ResultState.Loading()
            try {

                val pupil = PupilRequest(
                    country = country,
                    name = name,
                    image = imageUri,
                    latitude = 0.0,
                    longitude = 0.0
                )
                repository.addPupil(pupil)
                _creationState.value = ResultState.Success(Unit)
            } catch (e: Exception) {
                _creationState.value = ResultState.Error(e.message ?: "Error creating pupil")
            }
        }
    }

    fun getPupilById(pupilId: Int) {
        val handler = handleException { error ->
            _pupilState.value = ResultState.Error(error)
        }
        viewModelScope.launch(handler) {
            _pupilState.value = ResultState.Loading()
            val pupil = repository.getPupil(pupilId)
            _pupilState.value = ResultState.Success(pupil!!)
        }
    }

    fun updatePupil(pupilId: Int, pupil: PupilRequest) {
        val handler = handleException {
            _updateState.value = ResultState.Error(it)
        }
        viewModelScope.launch(handler) {
            _updateState.value = ResultState.Loading()
            repository.updatePupil(pupilId, pupil)
            _updateState.value = ResultState.Success(Unit)

        }
    }

    fun deletePupil(pupilId: Int, onDeleted: () -> Unit) {
        val handler = handleException {
            _updateState.value = ResultState.Error(it)
        }
        viewModelScope.launch(handler) {
            _updateState.value = ResultState.Loading()
            repository.deletePupil(pupilId)
            _updateState.value = ResultState.Success(Unit)
            onDeleted()
        }
    }
}