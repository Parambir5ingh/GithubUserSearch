package com.example.daggermvvmdemo.repositories

import com.example.daggermvvmdemo.retrofit.GithubUsersApi
import com.example.daggermvvmdemo.utils.Constants
import com.prm.speerandroidassessment.models.GitUserModel
import com.prm.speerandroidassessment.models.UserProfileModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/*
* Created by Parambir Singh ON 2025-02-04
*/
class ProfileRepository @Inject constructor(val githubUsersApi: GithubUsersApi) {

    private val _userProfile = MutableStateFlow<UserProfileModel?>(null)
    var userProfile: StateFlow<UserProfileModel?>? = null
        get() = _userProfile

    private val _followers = MutableStateFlow<List<GitUserModel>>(emptyList())
    var followers: StateFlow<List<GitUserModel>>? = null
        get() = _followers

    private val _followings = MutableStateFlow<List<GitUserModel>>(emptyList())
    var followings: StateFlow<List<GitUserModel>>? = null
        get() = _followings


    suspend fun getUserProfile(userName: String) {
        var url = "${Constants.baseUrl}users/$userName"

        val result = githubUsersApi.getUserProfile(url)
        if (result.isSuccessful && result.body() != null) {
            _userProfile.emit(result.body()!!)
        }
    }

    suspend fun getUserFollowers(userName: String) {
        var url = "${Constants.baseUrl}users/$userName/followers"

        val result = githubUsersApi.getFollowers(url)
        if (result.isSuccessful && result.body() != null) {
            _followers.emit(result.body()!!)
        }
    }

    suspend fun getUserFollowings(userName: String) {
        var url = "${Constants.baseUrl}users/$userName/following"

        val result = githubUsersApi.getFollowings(url)
        if (result.isSuccessful && result.body() != null) {
            _followings.emit(result.body()!!)
        }
    }
}