package com.example.daggermvvmdemo.repositories

import com.example.daggermvvmdemo.retrofit.GithubUsersApi
import com.prm.speerandroidassessment.models.GitUserModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/*
* Created by Parambir Singh ON 2025-02-04
*/
class SearchRepository @Inject constructor(val githubUsersApi: GithubUsersApi) {

    private val _users = MutableStateFlow<List<GitUserModel>>(emptyList())
    var users: StateFlow<List<GitUserModel>>? = null
        get() = _users

    private val _followers = MutableStateFlow<List<GitUserModel>>(emptyList())
    var followers: StateFlow<List<GitUserModel>>? = null
        get() = _followers

    private val _followings = MutableStateFlow<List<GitUserModel>>(emptyList())
    var followings: StateFlow<List<GitUserModel>>? = null
        get() = _followings

    suspend fun searchUsers(query: String) {
        val result = githubUsersApi.searchUsers(query)
        if (result.isSuccessful && result.body() != null) {
            _users.emit(result.body()!!.gitUserModels)
        } else {
            _users.emit(emptyList())
        }
    }
}