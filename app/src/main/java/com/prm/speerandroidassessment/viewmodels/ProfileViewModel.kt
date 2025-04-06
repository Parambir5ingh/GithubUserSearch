package com.prm.speerandroidassessment.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.daggermvvmdemo.repositories.ProfileRepository
import com.prm.speerandroidassessment.models.GitUserModel
import com.prm.speerandroidassessment.models.UserProfileModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(val repository: ProfileRepository) : ViewModel() {

    val userProfile: StateFlow<UserProfileModel?>?
        get() = repository.userProfile

    val followersLiveData: StateFlow<List<GitUserModel>>?
        get() = repository.followers

    val followingsLiveData: StateFlow<List<GitUserModel>>?
        get() = repository.followings

    init {

    }

    suspend fun getUserProfile(username: String): Any {
        return repository.getUserProfile(username)
    }

    suspend fun getFollowersList() {
        repository.getUserFollowers(userProfile?.value?.login!!)
    }

    suspend fun getFollowingsList() {
        repository.getUserFollowings(userProfile?.value?.login!!)
    }
}