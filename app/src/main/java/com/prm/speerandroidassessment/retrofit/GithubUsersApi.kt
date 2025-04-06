package com.example.daggermvvmdemo.retrofit

import com.prm.speerandroidassessment.models.GitSearchResponseModel
import com.prm.speerandroidassessment.models.GitUserModel
import com.prm.speerandroidassessment.models.UserProfileModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

/*
* Created by Parambir Singh ON 2025-04-06
*/
interface GithubUsersApi {

    @GET("search/users")
    suspend fun searchUsers(@Query("q") queryString : String): Response<GitSearchResponseModel>

    @GET()
    suspend fun getUserProfile(@Url url : String): Response<UserProfileModel>

    @GET()
    suspend fun getFollowers(@Url url : String): Response<List<GitUserModel>>

    @GET()
    suspend fun getFollowings(@Url url : String): Response<List<GitUserModel>>
}