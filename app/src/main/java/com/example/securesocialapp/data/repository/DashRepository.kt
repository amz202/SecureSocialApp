package com.example.securesocialapp.data.repository

import com.example.securesocialapp.data.model.response.ActivityLog
import com.example.securesocialapp.network.ApiService

interface DashRepository{
    suspend fun getActivityLog(): List<ActivityLog>
}

class DashRepositoryImpl(
    private val apiService: ApiService
): DashRepository{

    override suspend fun getActivityLog(): List<ActivityLog> {
        return apiService.getActivityLog()
    }
}