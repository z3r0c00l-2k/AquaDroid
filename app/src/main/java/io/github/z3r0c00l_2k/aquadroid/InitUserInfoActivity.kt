package io.github.z3r0c00l_2k.aquadroid

import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_init_user_info.*
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.*

class InitUserInfoActivity : AppCompatActivity() {

    private var gender: String = ""
    private var name: String = ""
    private var age: String = ""
    private var weight: String = ""
    private var workTime: String = ""
    private var wakeupTime: String = ""
    private var sleeingTime: String = ""
    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        setContentView(R.layout.activity_init_user_info)

        sharedPref = getSharedPreferences(AppUtils.USERS_SHARED_PREF, AppUtils.PRIVATE_MODE)

        radioGender.setOnClickedButtonListener { button, position ->
            gender = if (position == 0) {
                "Male"
            } else {
                "Female"
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
                this,
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
                this,
                TimePickerDialog.OnTimeSetListener { timePicker, selectedHour, selectedMinute ->
                    etSleepTime.editText!!.setText(
                        String.format("%02d:%02d", selectedHour, selectedMinute)
                    )
                }, hour, minute, false
            )
            mTimePicker.setTitle("Select Sleeping Time")
            mTimePicker.show()
        }

        btnContinue.setOnClickListener {

            val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(init_user_info_parent_layout.windowToken, 0)

            name = etName.editText!!.text.toString()
            age = etAge.editText!!.text.toString()
            weight = etWeight.editText!!.text.toString()
            workTime = etWorkTime.editText!!.text.toString()
            wakeupTime = etWakeUpTime.editText!!.text.toString()
            sleeingTime = etSleepTime.editText!!.text.toString()

            when {
                TextUtils.isEmpty(name) -> Snackbar.make(it, "Please input your name", Snackbar.LENGTH_SHORT).show()
                TextUtils.isEmpty(age) -> Snackbar.make(it, "Please input your age", Snackbar.LENGTH_SHORT).show()
                age.toInt() > 100 || age.toInt() < 1 -> Snackbar.make(
                    it,
                    "Please input a valid age",
                    Snackbar.LENGTH_SHORT
                ).show()
                TextUtils.isEmpty(gender) -> Snackbar.make(
                    it,
                    "Please select your gender",
                    Snackbar.LENGTH_SHORT
                ).show()
                TextUtils.isEmpty(weight) -> Snackbar.make(it, "Please input your weight", Snackbar.LENGTH_SHORT).show()
                weight.toInt() > 200 || weight.toInt() < 20 -> Snackbar.make(
                    it,
                    "Please input a valid weight",
                    Snackbar.LENGTH_SHORT
                ).show()
                TextUtils.isEmpty(workTime) -> Snackbar.make(
                    it,
                    "Please input your workout time",
                    Snackbar.LENGTH_SHORT
                ).show()
                workTime.toInt() > 500 || workTime.toInt() < 0 -> Snackbar.make(
                    it,
                    "Please input a valid workout time",
                    Snackbar.LENGTH_SHORT
                ).show()
                TextUtils.isEmpty(wakeupTime) -> Snackbar.make(
                    it,
                    "Please input your wakeup time", Snackbar.LENGTH_SHORT
                ).show()
                TextUtils.isEmpty(sleeingTime) -> Snackbar.make(
                    it,
                    "Please input your sleeping time", Snackbar.LENGTH_SHORT
                ).show()
                else -> {

                    val editor = sharedPref.edit()
                    editor.putString(AppUtils.NAME_KEY, name)
                    editor.putString(AppUtils.GENDER_KEY, gender)
                    editor.putInt(AppUtils.AGE_KEY, age.toInt())
                    editor.putInt(AppUtils.WEIGHT_KEY, weight.toInt())
                    editor.putInt(AppUtils.WORK_TIME_KEY, workTime.toInt())
                    editor.putString(AppUtils.WAKEUP_TIME, wakeupTime)
                    editor.putString(AppUtils.SLEEPING_TIME_KEY, sleeingTime)

                    val totalIntake = AppUtils.calculateIntake(age.toInt(), weight.toInt(), workTime.toInt())
                    val df = DecimalFormat("#")
                    df.roundingMode = RoundingMode.CEILING
                    editor.putInt(AppUtils.TOTAL_INTAKE, df.format(totalIntake).toInt())

                    editor.apply()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()

                }
            }
        }

    }
}
