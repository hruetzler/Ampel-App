package com.example.ampel

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import com.example.ampel.ControlActivity.Companion.greenTime
import com.example.ampel.ControlActivity.Companion.greenTimeDelay
import com.example.ampel.ControlActivity.Companion.messageID
import com.example.ampel.ControlActivity.Companion.redTime
import com.google.android.material.textfield.TextInputEditText

class SettingActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("wichtig", "jetzt sind wir in der Activity angekomme")
        setContentView(R.layout.setting_layout)
        Log.i("wichtig", "jetzt wurde die ansicht geändert.")



        val greenTimeInputEditText = findViewById<TextInputEditText>(R.id.greenTimeEditText)
        val redTimeInputEditText = findViewById<TextInputEditText>(R.id.redTimeEditText)
        val greenTimeDelayEditText = findViewById<TextInputEditText>(R.id.greenTimeDelayEditText)

        greenTimeInputEditText.setText(greenTime)
        redTimeInputEditText.setText(redTime)
        greenTimeDelayEditText.setText(greenTimeDelay)

        /*greenTimeInputEditText.addTextChangedListener { greenTime() }
        redTimeInputEditText.addTextChangedListener { redTime() }
        greenTimeDelayEditText.addTextChangedListener { greenTimeDelay() }*/
        greenTimeInputEditText.doAfterTextChanged { greenTime() }
        redTimeInputEditText.doAfterTextChanged { redTime() }
        greenTimeDelayEditText.doAfterTextChanged { greenTimeDelay() }



    }




    fun greenTime(){
        val greenTimeInputEditText = findViewById<TextInputEditText>(R.id.greenTimeEditText)
        ControlActivity.Companion.g_controlActivity.sendComand("setGreenTime", greenTimeInputEditText.text.toString())
        greenTime = greenTimeInputEditText.text.toString()
    }
    fun redTime(){
        val redTimeInputEditText = findViewById<TextInputEditText>(R.id.redTimeEditText)
        ControlActivity.Companion.g_controlActivity.sendComand("setRedTime", redTimeInputEditText.text.toString())
        redTime = redTimeInputEditText.text.toString()
    }
    fun greenTimeDelay(){
        val greenTimeDelayEditText = findViewById<TextInputEditText>(R.id.greenTimeDelayEditText)
        ControlActivity.Companion.g_controlActivity.sendComand("setGreenDelayTime", greenTimeDelayEditText.text.toString())
        greenTimeDelay = greenTimeDelayEditText.text.toString()
    }



    private fun handleKeyEvent(view: View, keyCode: Int): Boolean {
        if (keyCode == KeyEvent.KEYCODE_ENTER) {
            // Hide the keyboard
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
            return true
        }
        return false
    }
}