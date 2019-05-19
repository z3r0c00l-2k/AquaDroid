package io.github.z3r0c00l_2k.aquadroid.fragments

import android.app.TimePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.github.z3r0c00l_2k.aquadroid.R
import io.github.z3r0c00l_2k.aquadroid.utils.AppUtils
import kotlinx.android.synthetic.main.bottom_sheet_fragment.*
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.*


class BottomSheetFragment(val mCtx: Context) : BottomSheetDialogFragment() {

    private lateinit var sharedPref: SharedPreferences
    private var age: String = ""
    private var weight: String = ""
    private var workTime: String = ""
    private var wakeupTime: String = ""
    private var sleepingTime: String = ""
    private var notificMsg: String = ""
    private var notificFrequency: Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.bottom_sheet_fragment, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPref = mCtx.getSharedPreferences(AppUtils.USERS_SHARED_PREF, AppUtils.PRIVATE_MODE)

        etAge.editText!!.setText("" + sharedPref.getInt(AppUtils.AGE_KEY, 0))
        etWeight.editText!!.setText("" + sharedPref.getInt(AppUtils.WEIGHT_KEY, 0))
        etWorkTime.editText!!.setText("" + sharedPref.getInt(AppUtils.WORK_TIME_KEY, 0))
        etWakeUpTime.editText!!.setText(sharedPref.getString(AppUtils.WAKEUP_TIME, "00:00"))
        etSleepTime.editText!!.setText(sharedPref.getString(AppUtils.SLEEPING_TIME_KEY, "00:00"))
        etNotificationText.editText!!.setText(
            sharedPref.getString(
                AppUtils.NOTIFICATION_MSG_KEY,
                "Hey... Lets drink some water...."
            )
        )

        radioNotificItervel.setOnClickedButtonListener { button, position ->
            notificFrequency = when (position) {
                0 -> 30
                1 -> 45
                2 -> 60
                else -> 30
            }
        }
        notificFrequency = sharedPref.getInt(AppUtils.NOTIFICATION_FREQUENCY_KEY, 30)
        when (notificFrequency) {
            30 -> radioNotificItervel.position = 0
            45 -> radioNotificItervel.position = 1
            60 -> radioNotificItervel.position = 2
            else -> {
                radioNotificItervel.position = 0
                notificFrequency = 30
            }
        }


        etWakeUpTime.editText!!.setOnClickListener {
            val hour: Int
            val minute: Int
            if (TextUtils.isEmpty(etWakeUpTime.editText!!.text)) {
                val mCurrentTime = Calendar.getInstance()
                hour = mCurrentTime.get(Calendar.HOUR_OF_DAY)
                minute = mCurrentTime.get(Calendar.MINUTE)
            } else {
                val time = etWakeUpTime.editText!!.text.split(":")
                hour = time[0].toInt()
                minute = time[1].toInt()
            }
            val mTimePicker: TimePickerDialog
            mTimePicker = TimePickerDialog(
                mCtx,
                TimePickerDialog.OnTimeSetListener { timePicker, selectedHour, selectedMinute ->
                    etWakeUpTime.editText!!.setText(
                        String.format("%02d:%02d", selectedHour, selectedMinute)
                    )
                }, hour, minute, false
            )
            mTimePicker.setTitle("Select Wakeup Time")
            mTimePicker.show()
        }


        etSleepTime.editText!!.setOnClickListener {
            val hour: Int
            val minute: Int
            if (TextUtils.isEmpty(etSleepTime.editText!!.text)) {
                val mCurrentTime = Calendar.getInstance()
                hour = mCurrentTime.get(Calendar.HOUR_OF_DAY)
                minute = mCurrentTime.get(Calendar.MINUTE)
            } else {
                val time = etSleepTime.editText!!.text.split(":")
                hour = time[0].toInt()
                minute = time[1].toInt()
            }
            val mTimePicker: TimePickerDialog
            mTimePicker = TimePickerDialog(
                mCtx,
                TimePickerDialog.OnTimeSetListener { timePicker, selectedHour, selectedMinute ->
                    etSleepTime.editText!!.setText(
                        String.format("%02d:%02d", selectedHour, selectedMinute)
                    )
                }, hour, minute, false
            )
            mTimePicker.setTitle("Select Sleeping Time")
            mTimePicker.show()
        }

        btnUpdate.setOnClickListener {

            age = etAge.editText!!.text.toString()
            weight = etWeight.editText!!.text.toString()
            workTime = etWorkTime.editText!!.text.toString()
            wakeupTime = etWakeUpTime.editText!!.text.toString()
            sleepingTime = etSleepTime.editText!!.text.toString()
            notificMsg = etNotificationText.editText!!.text.toString()

            when {
                TextUtils.isEmpty(notificMsg) -> Toast.makeText(
                    mCtx,
                    "Please a notification message",
                    Toast.LENGTH_SHORT
                ).show()
                TextUtils.isEmpty(age) -> Toast.makeText(mCtx, "Please input your age", Toast.LENGTH_SHORT).show()
                age.toInt() > 100 || age.toInt() < 1 -> Toast.makeText(
                    mCtx,
                    "Please input a valid age",
                    Toast.LENGTH_SHORT
                ).show()
                notificFrequency == 0 -> Toast.makeText(
                    mCtx,
                    "Please select a notification frequency",
                    Toast.LENGTH_SHORT
                ).show()
                TextUtils.isEmpty(weight) -> Toast.makeText(
                    mCtx, "Please input your weight", Toast.LENGTH_SHORT
                ).show()
                weight.toInt() > 200 || weight.toInt() < 20 -> Toast.makeText(
                    mCtx,
                    "Please input a valid weight",
                    Toast.LENGTH_SHORT
                ).show()
                TextUtils.isEmpty(workTime) -> Toast.makeText(
                    mCtx,
                    "Please input your workout time",
                    Toast.LENGTH_SHORT
                ).show()
                workTime.toInt() > 500 || workTime.toInt() < 0 -> Toast.makeText(
                    mCtx,
                    "Please input a valid workout time",
                    Toast.LENGTH_SHORT
                ).show()
                TextUtils.isEmpty(wakeupTime) -> Toast.makeText(
                    mCtx,
                    "Please input your wakeup time", Toast.LENGTH_SHORT
                ).show()
                TextUtils.isEmpty(sleepingTime) -> Toast.makeText(
                    mCtx,
                    "Please input your sleeping time", Toast.LENGTH_SHORT
                ).show()
                else -> {

                    val editor = sharedPref.edit()
                    editor.putInt(AppUtils.AGE_KEY, age.toInt())
                    editor.putInt(AppUtils.WEIGHT_KEY, weight.toInt())
                    editor.putInt(AppUtils.WORK_TIME_KEY, workTime.toInt())
                    editor.putString(AppUtils.WAKEUP_TIME, wakeupTime)
                    editor.putString(AppUtils.SLEEPING_TIME_KEY, sleepingTime)
                    editor.putString(AppUtils.NOTIFICATION_MSG_KEY, notificMsg)
                    editor.putInt(AppUtils.NOTIFICATION_FREQUENCY_KEY, notificFrequency)

                    val totalIntake = AppUtils.calculateIntake(age.toInt(), weight.toInt(), workTime.toInt())
                    val df = DecimalFormat("#")
                    df.roundingMode = RoundingMode.CEILING
                    editor.putInt(AppUtils.TOTAL_INTAKE, df.format(totalIntake).toInt())
                    editor.apply()
                    Toast.makeText(mCtx, "Values updated successfully", Toast.LENGTH_SHORT).show()
                    dismiss()
                }
            }
        }
    }
}