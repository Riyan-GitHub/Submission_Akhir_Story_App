package com.example.submissionakhirstoryapp.data.response

import com.google.gson.annotations.SerializedName

data class ResponseAddStories (
    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String
)