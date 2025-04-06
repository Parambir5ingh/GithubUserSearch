package com.example.daggermvvmdemo.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.daggermvvmdemo.repositories.SearchRepository
import com.prm.speerandroidassessment.models.GitUserModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/*
* Created by Parambir Singh ON 2025-04-06
*/

@HiltViewModel
class MainViewModel @Inject constructor(val repository: SearchRepository) : ViewModel() {

    var _progress = MutableLiveData<Boolean>()
    var progress: LiveData<Boolean> = _progress

    var searchQuery by mutableStateOf("")
        private set

    fun updateQuery(newQuery: String) {
        searchQuery = newQuery
    }

    fun clearQuery() {
        searchQuery = ""
    }

    val userLiveData: StateFlow<List<GitUserModel>>?
        get() = repository.users


    init {
        viewModelScope.launch {
            _progress.postValue(true)
            repository.searchUsers("")
            _progress.postValue(false)
        }
    }

    suspend fun searchGitUsers(){
        _progress.postValue(true)
        repository.searchUsers(searchQuery)
        _progress.postValue(false)
    }
}