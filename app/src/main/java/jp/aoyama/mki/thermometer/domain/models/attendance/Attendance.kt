package jp.aoyama.mki.thermometer.domain.models.attendance

import java.util.*

data class Attendance(
    val userId: String,
    val userName: String,
    val enterAt: Calendar,
    val leftAt: Calendar
)
