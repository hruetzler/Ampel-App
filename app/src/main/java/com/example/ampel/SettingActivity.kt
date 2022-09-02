package com.example.ampel

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.example.ampel.ControlActivity.Companion.greenTime
import com.example.ampel.ControlActivity.Companion.greenTimeDelay
import com.example.ampel.ControlActivity.Companion.redTime
import com.example.ampel.ControlActivity.Companion.versionControler
import com.example.ampel.ControlActivity.Companion.versionProtocol
import com.google.android.material.textfield.TextInputEditText

class SettingActivity: AppCompatActivity() {
    private var greenTimeChange: Boolean = false
    private var redTimeChange: Boolean = false
    private var greenTimeDaleyChange: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("wichtig", "jetzt sind wir in der Activity angekomme")
        setContentView(R.layout.setting_layout)
        Log.i("wichtig", "jetzt wurde die ansicht geändert.")



        val greenTimeInputEditText = findViewById<TextInputEditText>(R.id.greenTimeEditText)
        val redTimeInputEditText = findViewById<TextInputEditText>(R.id.redTimeEditText)
        val greenTimeDelayEditText = findViewById<TextInputEditText>(R.id.greenTimeDelayEditText)
        val version = findViewById<TextView>(R.id.version)

        version.text = "Controler: " + versionControler + " App: 1.0.0 Protocol: " + versionProtocol + "App Protocol: 1.0.0"

        greenTimeInputEditText.setOnKeyListener{ view, keyCode, _ ->
            handleKeyEvent(view, keyCode)
        }

        redTimeInputEditText.setOnKeyListener{ view, keyCode, _ ->
            handleKeyEvent(view, keyCode)
        }
        greenTimeDelayEditText.setOnKeyListener{ view, keyCode, _ ->
            handleKeyEvent(view, keyCode)
        }


        greenTimeInputEditText.setText(greenTime)
        redTimeInputEditText.setText(redTime)
        greenTimeDelayEditText.setText(greenTimeDelay)

        greenTimeInputEditText.addTextChangedListener { greenTimeChange = true }
        redTimeInputEditText.addTextChangedListener { redTimeChange = true }
        greenTimeDelayEditText.addTextChangedListener { greenTimeDaleyChange = true }
    }

    private fun greenTime(){
        val greenTimeInputEditText = findViewById<TextInputEditText>(R.id.greenTimeEditText)
        ControlActivity.g_controlActivity.sendComand("setGreenTime", greenTimeInputEditText.text.toString())
        greenTime = greenTimeInputEditText.text.toString()
    }
    private fun redTime(){
        val redTimeInputEditText = findViewById<TextInputEditText>(R.id.redTimeEditText)
        ControlActivity.g_controlActivity.sendComand("setRedTime", redTimeInputEditText.text.toString())
        redTime = redTimeInputEditText.text.toString()
    }
    private fun greenTimeDelay(){
        val greenTimeDelayEditText = findViewById<TextInputEditText>(R.id.greenTimeDelayEditText)
        ControlActivity.g_controlActivity.sendComand("setGreenDelayTime", greenTimeDelayEditText.text.toString())
        greenTimeDelay = greenTimeDelayEditText.text.toString()
    }


    private fun handleKeyEvent(view: View, keyCode: Int): Boolean {
        if (keyCode == KeyEvent.KEYCODE_ENTER) {
           if (greenTimeChange){
               greenTime()
               greenTimeChange = false
           }
            if (redTimeChange){
                redTime()
                redTimeChange = false
            }
            if (greenTimeDaleyChange){
                greenTimeDelay()
                greenTimeDaleyChange = false
            }

            // Hide the keyboard
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
            return true
        }
        return false
    }
}