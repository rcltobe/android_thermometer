package jp.aoyama.mki.thermometer.infrastructure.api.bluetooth

import jp.aoyama.mki.thermometer.domain.models.device.Device
import jp.aoyama.mki.thermometer.domain.repository.DeviceRepository
import jp.aoyama.mki.thermometer.infrastructure.api.ApiRepositoryUtil
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiDeviceRepository : DeviceRepository {

    private val service: ApiDeviceService by lazy {
        val client = OkHttpClient()
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .client(client)
            .build()

        retrofit.create(ApiDeviceService::class.java)
    }

    override suspend fun findAll(): List<Device> {
        val response = service.getDevices().execute()
        return response.body()?.map {
            Device(address = it.address, userId = it.userId)
        } ?: emptyList()
    }

    override suspend fun findByUserId(userId: String): List<Device> {
        val response = service.getUserDevices(userId).execute()
        return response.body()?.map {
            Device(address = it.address, userId = it.userId)
        } ?: emptyList()
    }

    override suspend fun save(device: Device) {
        val request = AddUserDeviceRequest(userId = device.userId, address = device.address)
        service.addUserDevice(request).execute()
    }

    override suspend fun delete(address: String) {
        val request = DeleteDeviceRequest(address)
        service.deleteDevice(request).execute()
    }

    companion object {
        private const val BASE_URL = ApiRepositoryUtil.BASE_URL
    }
}