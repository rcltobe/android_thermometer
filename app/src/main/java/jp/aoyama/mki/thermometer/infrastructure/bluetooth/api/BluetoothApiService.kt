package jp.aoyama.mki.thermometer.infrastructure.bluetooth.api

import retrofit2.Call
import retrofit2.http.GET

data class BluetoothDevice(
    val address: String = "",
    val found: Boolean = false
)

interface BluetoothApiService {
    @GET("scan")
    fun scan(): Call<List<BluetoothDevice>>
}