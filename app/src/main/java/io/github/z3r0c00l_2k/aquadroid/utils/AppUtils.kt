package io.github.z3r0c00l_2k.aquadroid.utils

import java.text.SimpleDateFormat
import java.util.*


class AppUtils {
    companion object {
        fun calculateIntake(weight: Int, workTime: Int): Double {

            return ((weight * 100 / 3.0) + (workTime / 6 * 7))

        }

        fun getCurrentDate(): String? {
            val c = Calendar.getInstance().time
            val df = SimpleDateFormat("dd-MM-yyyy")
            return df.format(c)
        }

        val USERS_SHARED_PREF = "user_pref"
        val PRIVATE_MODE = 0
        val WEIGHT_KEY = "weight"
        val WORK_TIME_KEY = "worktime"
        val TOTAL_INTAKE = "totalintake"
        val NOTIFICATION_STATUS_KEY = "notificationstatus"
        val NOTIFICATION_FREQUENCY_KEY = "notificationfrequency"
        val NOTIFICATION_MSG_KEY = "notificationmsg"
        val SLEEPING_TIME_KEY = "sleepingtime"
        val WAKEUP_TIME = "wakeuptime"
        val NOTIFICATION_TONE_URI_KEY = "notificationtone"
        val FIRST_RUN_KEY = "firstrun"
    }
}