package io.github.z3r0c00l_2k.aquadroid

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var totalIntake: Int = 0
    private var inTook: Int = 0
    private lateinit var sharedPref: SharedPreferences
    private lateinit var sqliteHelper: SqliteHelper
    private lateinit var dateNow: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPref = getSharedPreferences(AppUtils.USERS_SHARED_PREF, AppUtils.PRIVATE_MODE)
        sqliteHelper = SqliteHelper(this)
        dateNow = AppUtils.getCurrentDate()!!

        totalIntake = sharedPref.getInt(AppUtils.TOTAL_INTAKE, 0)

        if (totalIntake <= 0) {
            startActivity(Intent(this, InitUserInfoActivity::class.java))
            finish()
        }


        val r = sqliteHelper.addAll(dateNow, 20, totalIntake)

        inTook = sqliteHelper.getIntook(dateNow)

        setWaterLevel(inTook, totalIntake)

        fabAdd.setOnClickListener {
            if (inTook < totalIntake) {
                Snackbar.make(it, "Did you drank a glass of water..?", Snackbar.LENGTH_SHORT).setAction("  Yes  ") {
                    if (sqliteHelper.addIntook(dateNow) > 0) {
                        inTook++
                        setWaterLevel(inTook, totalIntake)
                    }
                }.show()
            } else {
                Snackbar.make(it, "You achieved the goal", Snackbar.LENGTH_SHORT).show()
            }

        }

    }

    private fun setWaterLevel(inTook: Int, totalIntake: Int) {
        intakeText.text = "" + inTook + "/" + totalIntake + "\nGlasses"
        val progress = ((inTook / totalIntake.toFloat()) * 100).toInt()
        waterLevelView.centerTitle = "" + progress + "%"
        waterLevelView.progressValue = progress
        if (inTook >= totalIntake) {
            Snackbar.make(main_activity_parent, "You achieved the goal", Snackbar.LENGTH_SHORT).show()
        }
    }


}
