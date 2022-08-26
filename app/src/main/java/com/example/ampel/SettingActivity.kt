package com.example.ampel

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText

class SettingActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("wichtig", "jetzt sind wir in der Activity angekomme")
        setContentView(R.layout.setting_layout)
        Log.i("wichtig", "jetzt wurde die ansicht geändert.")


        val settingButton = findViewById<ImageButton>(R.id.floating_setting_button)


        settingButton.setOnClickListener { setting() }




    }

    private fun setting(){
        val intent = Intent(this, ControlActivity::class.java)
        startActivity(intent)

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