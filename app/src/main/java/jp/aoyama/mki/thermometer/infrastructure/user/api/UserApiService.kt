package jp.aoyama.mki.thermometer.infrastructure.user.api

import jp.aoyama.mki.thermometer.infrastructure.user.api.models.*
import retrofit2.Call
import retrofit2.http.*

interface UserApiService {
    @GET("/")
    fun findAll(): Call<List<User>>

    @GET("/{id}")
    fun find(@Path("id") userId: String): Call<User>

    @POST("/create")
    fun create(@Body request: CreateUserRequest): Call<Unit>

    @POST("/edit/{id}")
    fun editName(@Path("id") userId: String, @Body request: EditNameRequest): Call<Unit>

    @POST("/edit/{id}")
    fun editGrade(@Path("id") userId: String, @Body request: EditGradeRequest): Call<Unit>

    @POST("/edit/{id}")
    fun editDevices(@Path("id") userId: String, @Body request: EditDeviceRequest): Call<Unit>

    @DELETE("/delete/{id}")
    fun deleteUser(@Path("id") userId: String): Call<Unit>
}

