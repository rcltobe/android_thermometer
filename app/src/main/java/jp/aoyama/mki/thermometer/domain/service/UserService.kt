package jp.aoyama.mki.thermometer.domain.service

import android.content.Context
import jp.aoyama.mki.thermometer.domain.models.device.Device
import jp.aoyama.mki.thermometer.domain.models.user.Grade
import jp.aoyama.mki.thermometer.domain.models.user.User
import jp.aoyama.mki.thermometer.domain.models.user.UserEntity
import jp.aoyama.mki.thermometer.domain.repository.DeviceRepository
import jp.aoyama.mki.thermometer.domain.repository.UserRepository
import jp.aoyama.mki.thermometer.infrastructure.local.device.LocalFileDeviceRepository
import jp.aoyama.mki.thermometer.infrastructure.local.user.LocalFileUserRepository
import jp.aoyama.mki.thermometer.view.models.UserData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import jp.aoyama.mki.thermometer.view.models.UserEntity as UserViewEntity

class UserService(
    private val userRepository: UserRepository,
    private val deviceRepository: DeviceRepository,
) {

    constructor(context: Context) : this(
        userRepository = LocalFileUserRepository(context),
        deviceRepository = LocalFileDeviceRepository(context)
    )

    suspend fun addUser(user: UserEntity) =
        withContext(Dispatchers.IO) {
            userRepository.save(user)
        }

    suspend fun deleteUser(userId: String) =
        withContext(Dispatchers.IO) {
            userRepository.delete(userId)
        }

    suspend fun getUsers(): UserData =
        withContext(Dispatchers.IO) {
            val users = userRepository.findAll()
            val entities = users.map {
                val devices = deviceRepository.findByUserId(it.id)
                val user = it.toUser(devices)
                UserViewEntity(user, null, null)
            }
            UserData(users = entities)
        }

    suspend fun getUser(userId: String): User? =
        withContext(Dispatchers.IO) {
            val entity = userRepository.find(userId) ?: return@withContext null
            val device = deviceRepository.findByUserId(userId)
            entity.toUser(device)
        }

    suspend fun updateName(userId: String, name: String) =
        withContext(Dispatchers.IO) {
            userRepository.updateName(userId, name)
        }

    suspend fun updateGrade(userId: String, grade: Grade?) =
        withContext(Dispatchers.IO) {
            userRepository.updateGrade(userId, grade)
        }

    suspend fun addBluetoothDevice(device: Device) =
        withContext(Dispatchers.IO) {
            deviceRepository.save(device)
        }

    suspend fun removeBluetoothDevice(address: String) =
        withContext(Dispatchers.IO) {
            deviceRepository.delete(address)
        }
}