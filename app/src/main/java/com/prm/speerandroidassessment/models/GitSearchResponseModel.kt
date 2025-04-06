package com.prm.speerandroidassessment.models

import com.google.gson.annotations.SerializedName

data class GitSearchResponseModel(
    val incomplete_results: Boolean,

    @SerializedName("items")
    val gitUserModels: List<GitUserModel>,

    val total_count: Int
)