package com.example.health.data.repository

import com.example.health.data.local.JsonDataSource
import com.example.health.data.model.Hospital
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HospitalRepository @Inject constructor(
    private val jsonDataSource: JsonDataSource
) {
    private var cachedHospitals: List<Hospital>? = null

    fun getHospitals(): Result<List<Hospital>> {
        cachedHospitals?.let { return Result.success(it) }
        return jsonDataSource.loadHospitals().map { data ->
            data.hospitals.also { cachedHospitals = it }
        }
    }

    fun getHospitalsSortedByDistance(lat: Double, lng: Double): List<Hospital> {
        val hospitals = getHospitals().getOrNull() ?: return emptyList()
        return hospitals.sortedBy { it.distanceTo(lat, lng) }
    }

    fun getEmergencyHospitals(): List<Hospital> {
        return getHospitals().getOrNull()?.filter { it.emergencyAvailable } ?: emptyList()
    }

    fun searchHospitals(query: String): List<Hospital> {
        val hospitals = getHospitals().getOrNull() ?: return emptyList()
        if (query.isBlank()) return hospitals
        val lq = query.lowercase().trim()
        return hospitals.filter {
            it.name.lowercase().contains(lq) ||
            it.address.lowercase().contains(lq) ||
            it.specialties.any { s -> s.lowercase().contains(lq) } ||
            it.type.lowercase().contains(lq)
        }
    }
}
