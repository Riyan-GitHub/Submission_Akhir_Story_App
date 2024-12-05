package com.example.submissionakhirstoryapp.di

import android.content.Context
import com.example.submissionakhirstoryapp.config.ApiConfig
import com.example.submissionakhirstoryapp.data.pref.UserPreference
import com.example.submissionakhirstoryapp.data.pref.UserRepository
import com.example.submissionakhirstoryapp.data.pref.dataStore

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService()
        return UserRepository.getInstance(apiService, pref)
    }
}