package com.example.health.data.local

import android.content.Context
import com.example.health.data.model.DisclaimerData
import com.example.health.data.model.EmergencyData
import com.example.health.data.model.HospitalData
import com.example.health.data.model.LearningData
import com.example.health.data.model.MythsData
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class JsonDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val gson = Gson()

    private inline fun <reified T> loadJson(fileName: String): Result<T> {
        return try {
            val lang = java.util.Locale.getDefault().language
            val targetFileName = if (lang != "en") fileName.replace(".json", "_${lang}.json") else fileName
            val json = try {
                context.assets.open(targetFileName).bufferedReader().use { it.readText() }
            } catch (e: Exception) {
                context.assets.open(fileName).bufferedReader().use { it.readText() }
            }
            val data = gson.fromJson(json, T::class.java)
            if (data != null) Result.success(data)
            else Result.failure(IllegalStateException("Failed to parse $fileName"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun loadEmergencies(): Result<EmergencyData> = loadJson("emergencies.json")

    fun loadHospitals(): Result<HospitalData> = loadJson("hospitals.json")

    fun loadLearning(): Result<LearningData> = loadJson("learning.json")

    fun loadMyths(): Result<MythsData> = loadJson("myths.json")

    fun loadDisclaimer(): Result<DisclaimerData> = loadJson("disclaimer.json")
}
