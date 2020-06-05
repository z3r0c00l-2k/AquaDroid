package io.github.z3r0c00l_2k.aquadroid.fragments

import android.app.Activity
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.github.z3r0c00l_2k.aquadroid.MainActivity
import io.github.z3r0c00l_2k.aquadroid.R
import io.github.z3r0c00l_2k.aquadroid.helpers.AlarmHelper
import io.github.z3r0c00l_2k.aquadroid.helpers.SqliteHelper
import io.github.z3r0c00l_2k.aquadroid.utils.AppUtils
import kotlinx.android.synthetic.main.bottom_sheet_fragment.*
import java.math.RoundingMode
import java.text.DateFormat
import java.text.DecimalFormat
import java.util.*


class BottomSheetFragment(val mCtx: Context) : BottomSheetDialogFragment() {

    private lateinit var sharedPref: SharedPreferences
    private var weight: String = ""
    private var workTime: String = ""
    private var customTarget: String = ""
    private var wakeupTime: Long = 0
    private var sleepingTime: Long = 0
    private var notificMsg: String = ""
    private var notificFrequency: Int = 0
    private var currentToneUri: String? = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_fragment, container, false)

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val is24h = android.text.format.DateFormat.is24HourFormat(mCtx)

        sharedPref = mCtx.getSharedPreferences(AppUtils.USERS_SHARED_PREF, AppUtils.PRIVATE_MODE)

        etWeight.editText!!.setText("" + sharedPref.getInt(AppUtils.WEIGHT_KEY, 0))
        etWorkTime.editText!!.setText("" + sharedPref.getInt(AppUtils.WORK_TIME_KEY, 0))
        etTarget.editText!!.setText("" + sharedPref.getInt(AppUtils.TOTAL_INTAKE, 0))
        etNotificationText.editText!!.setText(
            sharedPref.getString(
                AppUtils.NOTIFICATION_MSG_KEY,
                "Hey... Lets drink some water...."
            )
        )
        currentToneUri = sharedPref.getString(
            AppUtils.NOTIFICATION_TONE_URI_KEY,
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION).toString()
        )
        etRingtone.editText!!.setText(
            RingtoneManager.getRingtone(
                mCtx,
                Uri.parse(currentToneUri)
            ).getTitle(mCtx)
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

        etRingtone.editText!!.setOnClickListener {
            val intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER)
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION)
            intent.putExtra(
                RingtoneManager.EXTRA_RINGTONE_TITLE,
                "Select ringtone for notifications:"
            )
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false)
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true)
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, currentToneUri)
            startActivityForResult(intent, 999)
        }

        wakeupTime = sharedPref.getLong(AppUtils.WAKEUP_TIME, 1558323000000)
        sleepingTime = sharedPref.getLong(AppUtils.SLEEPING_TIME_KEY, 1558369800000)
        val cal = Calendar.getInstance()
        cal.timeInMillis = wakeupTime
        etWakeUpTime.editText!!.setText(
            String.format(
                "%02d:%02d",
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE)
            )
        )
        cal.timeInMillis = sleepingTime
        etSleepTime.editText!!.setText(
            String.format(
                "%02d:%02d",
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE)
            )
        )

        etWakeUpTime.editText!!.setOnClickListener {

            val calendar = Calendar.getInstance()
            calendar.timeInMillis = wakeupTime

            val mTimePicker: TimePickerDialog
            mTimePicker = TimePickerDialog(
                mCtx,
                TimePickerDialog.OnTimeSetListener { timePicker, selectedHour, selectedMinute ->

                    val time = Calendar.getInstance()
                    time.set(Calendar.HOUR_OF_DAY, selectedHour)
                    time.set(Calendar.MINUTE, selectedMinute)
                    wakeupTime = time.timeInMillis

                    etWakeUpTime.editText!!.setText(
                        String.format("%02d:%02d", selectedHour, selectedMinute)
                    )
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), is24h
            )
            mTimePicker.setTitle("Select Wakeup Time")
            mTimePicker.show()
        }


        etSleepTime.editText!!.setOnClickListener {

            val calendar = Calendar.getInstance()
            calendar.timeInMillis = sleepingTime

            val mTimePicker: TimePickerDialog
            mTimePicker = TimePickerDialog(
                mCtx,
                TimePickerDialog.OnTimeSetListener { timePicker, selectedHour, selectedMinute ->

                    val time = Calendar.getInstance()
                    time.set(Calendar.HOUR_OF_DAY, selectedHour)
                    time.set(Calendar.MINUTE, selectedMinute)
                    sleepingTime = time.timeInMillis

                    etSleepTime.editText!!.setText(
                        String.format("%02d:%02d", selectedHour, selectedMinute)
                    )
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), is24h
            )
            mTimePicker.setTitle("Select Sleeping Time")
            mTimePicker.show()
        }

        btnUpdate.setOnClickListener {

            val currentTarget = sharedPref.getInt(AppUtils.TOTAL_INTAKE, 0)

            weight = etWeight.editText!!.text.toString()
            workTime = etWorkTime.editText!!.text.toString()
            notificMsg = etNotificationText.editText!!.text.toString()
            customTarget = etTarget.editText!!.text.toString()

            when {
                TextUtils.isEmpty(notificMsg) -> Toast.makeText(
                    mCtx,
                    "Please a notification message",
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
                TextUtils.isEmpty(customTarget) -> Toast.makeText(
                    mCtx,
                    "Please input your custom target",
                    Toast.LENGTH_SHORT
                ).show()
                else -> {

                    val editor = sharedPref.edit()
                    editor.putInt(AppUtils.WEIGHT_KEY, weight.toInt())
                    editor.putInt(AppUtils.WORK_TIME_KEY, workTime.toInt())
                    editor.putLong(AppUtils.WAKEUP_TIME, wakeupTime)
                    editor.putLong(AppUtils.SLEEPING_TIME_KEY, sleepingTime)
                    editor.putString(AppUtils.NOTIFICATION_MSG_KEY, notificMsg)
                    editor.putInt(AppUtils.NOTIFICATION_FREQUENCY_KEY, notificFrequency)

                    val sqliteHelper = SqliteHelper(mCtx)

                    if (currentTarget != customTarget.toInt()) {
                        editor.putInt(AppUtils.TOTAL_INTAKE, customTarget.toInt())

                        sqliteHelper.updateTotalIntake(
                            AppUtils.getCurrentDate()!!,
                            customTarget.toInt()
                        )
                    } else {
                        val totalIntake = AppUtils.calculateIntake(weight.toInt(), workTime.toInt())
                        val df = DecimalFormat("#")
                        df.roundingMode = RoundingMode.CEILING
                        editor.putInt(AppUtils.TOTAL_INTAKE, df.format(totalIntake).toInt())

                        sqliteHelper.updateTotalIntake(
                            AppUtils.getCurrentDate()!!,
                            df.format(totalIntake).toInt()
                        )
                    }

                    editor.apply()

                    Toast.makeText(mCtx, "Values updated successfully", Toast.LENGTH_SHORT).show()
                    val alarmHelper = AlarmHelper()
                    alarmHelper.cancelAlarm(mCtx)
                    alarmHelper.setAlarm(
                        mCtx,
                        sharedPref.getInt(AppUtils.NOTIFICATION_FREQUENCY_KEY, 30).toLong()
                    )
                    dismiss()
                    (activity as MainActivity?)!!.updateValues()

                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == 999) {

            val uri = data!!.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI) as Uri
            currentToneUri = uri.toString()
            sharedPref.edit().putString(AppUtils.NOTIFICATION_TONE_URI_KEY, currentToneUri).apply()
            val ringtone = RingtoneManager.getRingtone(mCtx, uri)
            etRingtone.editText!!.setText(ringtone.getTitle(mCtx))

        }
    }
}