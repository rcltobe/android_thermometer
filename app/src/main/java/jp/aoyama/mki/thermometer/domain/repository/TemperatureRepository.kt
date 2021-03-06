package jp.aoyama.mki.thermometer.domain.repository

import jp.aoyama.mki.thermometer.domain.models.temperature.BodyTemperatureEntity

interface TemperatureRepository {
    suspend fun findAll(): List<BodyTemperatureEntity>

    suspend fun save(data: BodyTemperatureEntity)
}