package ru.dvfu.appliances.model.repository_offline

interface OfflineRepository {
    suspend fun getApplianceById()
}