package jp.aoyama.mki.thermometer.domain.service

import android.content.Context
import jp.aoyama.mki.thermometer.domain.models.attendance.AttendanceEntity
import jp.aoyama.mki.thermometer.domain.models.attendance.UserAttendance
import jp.aoyama.mki.thermometer.domain.models.device.Device
import jp.aoyama.mki.thermometer.domain.models.device.DeviceStateEntity
import jp.aoyama.mki.thermometer.domain.models.device.DeviceStateEntity.Companion.getAddressOf
import jp.aoyama.mki.thermometer.domain.repository.DeviceRepository
import jp.aoyama.mki.thermometer.domain.repository.DeviceStateRepository
import jp.aoyama.mki.thermometer.domain.repository.UserRepository
import jp.aoyama.mki.thermometer.infrastructure.repositories.RepositoryContainer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AttendanceService(
    private val userRepository: UserRepository,
    private val deviceRepository: DeviceRepository,
    private val deviceStateRepository: DeviceStateRepository
) {

    constructor(context: Context) : this(
        userRepository = RepositoryContainer(context).userRepository,
        deviceRepository = RepositoryContainer(context).deviceRepository,
        deviceStateRepository = RepositoryContainer(context).deviceStateRepository
    )

    suspend fun getAttendances(): List<UserAttendance> = withContext(Dispatchers.IO) {
        val users = userRepository.findAll()
        val devices = deviceRepository.findAll()
        val states = deviceStateRepository.findAll()
        return@withContext users.map { user ->
            getUserAttendance(
                user.id,
                user.name,
                devices,
                states
            )
        }
    }

    suspend fun getUserAttendance(
        userId: String,
        userName: String,
        devices: List<Device>? = null,
        allDeviceStates: List<DeviceStateEntity>? = null
    ): UserAttendance {
        val userDevices =
            if (devices == null) {
                deviceRepository.findByUserId(userId)
            } else {
                devices.filter { it.userId == userId }
            }

        // ユーザーに紐づくすべての端末の検索記録を、日時順にソート
        val userAllDeviceStates = if (allDeviceStates == null) {
            userDevices.flatMap { deviceStateRepository.findByAddress(it.address) }
        } else {
            allDeviceStates.getAddressOf(userDevices.map { it.address })
        }
        val sortedStates = userAllDeviceStates.sortedBy { it.createdAt.timeInMillis }

        // 中間の結果を破棄する
        val deviceStates = sortedStates.mapIndexedNotNull { index, currentState ->
            if (index == 0)
                return@mapIndexedNotNull currentState

            val prevState = sortedStates[index - 1]
            if (prevState.found != currentState.found) currentState
            else null
        }

        // 入室と退室履歴の紐付けを行う
        val foundStates = deviceStates.filter { it.found }
        val attendances = foundStates.map { enterState ->
            val nextLeftState = deviceStates
                .filter { !it.found }
                .firstOrNull { it.createdAt.timeInMillis >= enterState.createdAt.timeInMillis }

            AttendanceEntity(
                userId = userId,
                enterAt = enterState.createdAt,
                leftAt = nextLeftState?.createdAt
            )
        }

        return UserAttendance(
            userId = userId,
            userName = userName,
            attendances = attendances
        )
    }
}