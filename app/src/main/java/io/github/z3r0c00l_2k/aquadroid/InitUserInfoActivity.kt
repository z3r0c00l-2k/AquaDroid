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
import io.github.z3r0c00l_2k.aquadroid.utils.AppUtils
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
    private var wakeupTime: Long = 0
    private var sleepingTime: Long = 0
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

        wakeupTime = sharedPref.getLong(AppUtils.WAKEUP_TIME, 1558323000000)
        sleepingTime = sharedPref.getLong(AppUtils.SLEEPING_TIME_KEY, 1558369800000)

        etWakeUpTime.editText!!.setOnClickListener {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = wakeupTime

            val mTimePicker: TimePickerDialog
            mTimePicker = TimePickerDialog(
                this,
                TimePickerDialog.OnTimeSetListener { timePicker, selectedHour, selectedMinute ->

                    val time = Calendar.getInstance()
                    time.set(Calendar.HOUR_OF_DAY, selectedHour)
                    time.set(Calendar.MINUTE, selectedMinute)
                    wakeupTime = time.timeInMillis

                    etWakeUpTime.editText!!.setText(
                        String.format("%02d:%02d", selectedHour, selectedMinute)
                    )
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true
            )
            mTimePicker.setTitle("Select Wakeup Time")
            mTimePicker.show()
        }


        etSleepTime.editText!!.setOnClickListener {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = sleepingTime

            val mTimePicker: TimePickerDialog
            mTimePicker = TimePickerDialog(
                this,
                TimePickerDialog.OnTimeSetListener { timePicker, selectedHour, selectedMinute ->

                    val time = Calendar.getInstance()
                    time.set(Calendar.HOUR_OF_DAY, selectedHour)
                    time.set(Calendar.MINUTE, selectedMinute)
                    sleepingTime = time.timeInMillis

                    etSleepTime.editText!!.setText(
                        String.format("%02d:%02d", selectedHour, selectedMinute)
                    )
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true
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
                else -> {

                    val editor = sharedPref.edit()
                    editor.putString(AppUtils.NAME_KEY, name)
                    editor.putString(AppUtils.GENDER_KEY, gender)
                    editor.putInt(AppUtils.AGE_KEY, age.toInt())
                    editor.putInt(AppUtils.WEIGHT_KEY, weight.toInt())
                    editor.putInt(AppUtils.WORK_TIME_KEY, workTime.toInt())
                    editor.putLong(AppUtils.WAKEUP_TIME, wakeupTime)
                    editor.putLong(AppUtils.SLEEPING_TIME_KEY, sleepingTime)

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
