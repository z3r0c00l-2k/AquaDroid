package io.github.z3r0c00l_2k.aquadroid

import java.text.SimpleDateFormat
import java.util.*


class AppUtils {
    companion object {
        fun calculateIntake(age: Int, weight: Int, workTime: Int): Double {

            val ageGroup = when {
                age > 55 -> 30
                age > 30 -> 35
                else -> 40
            }

            return ((weight * 2.205 * ageGroup / 28) + (workTime / 30 * 120)) / 6
        }

        fun getCurrentDate(): String? {
            val c = Calendar.getInstance().time
            val df = SimpleDateFormat("dd-MM-yyyy")
            return df.format(c)
        }

        val USERS_SHARED_PREF = "user_pref"
        val PRIVATE_MODE = 0
        val NAME_KEY = "name"
        val AGE_KEY = "age"
        val GENDER_KEY = "gender"
        val WEIGHT_KEY = "weight"
        val WORK_TIME_KEY = "worktime"
        val TOTAL_INTAKE = "totalintake"
        val NOTIFICATION_STATUS_KEY = "notificationstatus"
        val NOTIFICATION_FREQUENCY_KEY = "notificationfrequency"
        val NOTIFICATION_MSG_KEY = "notificationmsg"
        val SLEEPING_TIME_KEY = "sleepingtime"
        val WAKEUP_TIME = "wakeuptime"
    }
}