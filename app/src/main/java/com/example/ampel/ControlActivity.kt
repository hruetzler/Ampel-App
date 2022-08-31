package com.example.ampel

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.ampel.ControlActivity.Companion.m_bluetoothSocket
import com.example.ampel.ControlActivity.Companion.m_isConnected
import com.example.ampel.ControlActivity.Companion.m_progress
import com.google.android.material.switchmaterial.SwitchMaterial
import java.io.IOException
import java.io.InputStream
import java.util.*
import kotlin.concurrent.thread


class ControlActivity: AppCompatActivity(){


    companion object {
        var m_myUUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
        var m_bluetoothSocket: BluetoothSocket? = null
        lateinit var m_progress: ProgressDialog
        lateinit var m_bluetoothAdapter: BluetoothAdapter
        var m_isConnected: Boolean = false
        lateinit var m_address: String
        var messageID = 0
        var greenTimeDelay: Int = 50
        var greenTime : Int = 5
        var redTime: Int = 60


        lateinit var g_controlActivity: ControlActivity
        val msgIdMap: MutableMap<String, String> = mutableMapOf()
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        g_controlActivity = this
        setContentView(R.layout.control_layout)
        m_address = intent.getStringExtra(MainActivity.EXTRA_ADDRESS).toString()

        val bluetooth = Bluetooth()

        bluetooth.connectBluetooth()


        ConnectToDevice(this).execute()
        val settingButton = findViewById<ImageButton>(R.id.floating_setting_button)

        val ampelSchalter = findViewById<Button>(R.id.schalter)
        val control_led_disconnect = findViewById<Button>(R.id.control_led_disconnect)
        val ampelPicture = findViewById<ImageView>(R.id.ampelPicture)
        val automaticMode = findViewById<SwitchMaterial>(R.id.automaticModeSwitch)
        val radioButton = findViewById<RadioGroup>(R.id.radioGroup)

        automaticMode.setOnClickListener{automatic()}
        settingButton.setOnClickListener { setting() }
        ampelPicture.setOnClickListener { toggle() }
        ampelSchalter.setOnClickListener { toggle() }
        control_led_disconnect.setOnClickListener { disconnect() }


        radioButton.setOnCheckedChangeListener { group, checkedID ->
            Log.i("wichtig", group.toString() + checkedID.toString())
            tasterEinstellung()

        }



    }

    fun startAbfrage(){
        sendComand("getAutomaticMode", null)
        sendComand("getButtonMode", null)
        sendComand("getState", null)

        sendComand("getGreenTime", null)
        sendComand("getRedTime", null)
        sendComand("getGreenDelayTime", null)


    }

    fun automatic(){
        val automaticMode = findViewById<SwitchMaterial>(R.id.automaticModeSwitch)
        if (automaticMode.isChecked){
            sendComand("setAutomaticMode", "true")
        }else{
            sendComand("setAutomaticMode", "false")
        }
    }

    private fun toggle(){
        sendComand("toggle", null)

    }

    private fun tasterEinstellung(){
        val radioButtonGreen = findViewById<RadioButton>(R.id.radio_button_requestGreen)
        val radioButtonToggle = findViewById<RadioButton>(R.id.radio_button_toggle)
        val radioButtonOff = findViewById<RadioButton>(R.id.radio_button_off)

        if (radioButtonGreen.isChecked){
            sendComand("setButtonMode", "requestGreen")

        } else{
            if (radioButtonToggle.isChecked){
                sendComand("setButtonMode", "toggle")

            } else {
                if (radioButtonOff.isChecked){
                    sendComand("setButtonMode", "off")

                }
            }
        }
    }

    fun sendComand(command: String, wert: String?){
        if (m_bluetoothSocket != null){
            try {
                msgIdMap[messageID.toString()] = command
                if (wert == null){
                    m_bluetoothSocket!!.outputStream.write((command + "|" + messageID + "\n").toByteArray())
                    messageID++
                } else {
                    m_bluetoothSocket!!.outputStream.write((command + "|" + messageID + "|" + wert + "\n").toByteArray())
                    messageID++
                }

            } catch (e: IOException){
                Log.i("wichtig", "Das senden hat eines Comand hat nicht geklapt.")
                Toast.makeText(this, "Ein Satz mit X das war wohl nix.", Toast.LENGTH_LONG).show()
                e.printStackTrace()
            }
        }
    }

    private fun setting(){
        val settings = Intent(this, SettingActivity::class.java)
        startActivity(settings)

    }

    private fun disconnect() {
        if (m_bluetoothSocket != null){
            try {
                m_bluetoothSocket!!.close()
                m_bluetoothSocket = null
                m_isConnected = false
            } catch (e: IOException){
                e.printStackTrace()
            }
        }
        finish()
    }

    private fun receive(){
        startAbfrage()
        val mmBuffer: ByteArray = ByteArray(1024) // mmBuffer store for the stream
        val mmInStream: InputStream = m_bluetoothSocket!!.inputStream
        var message: String
        var value : Int
        var bufferIndex : Int = 0
        while (true){
            value = try {
                mmInStream.read()
            } catch (e: IOException) {
                Log.d("wichtig", "Input stream was disconnected", e)
                break
            }
            if ( value != 0x0a){
                mmBuffer[bufferIndex++] = value.toByte()
            }else{
                message = String(mmBuffer)
                message = message.substring(0,bufferIndex)
                bufferIndex = 0
                Log.i("wichtig", message)
                val messageArry = message.split("|").toTypedArray()
                val command = messageArry[0];
                val msgId = messageArry[1];

                if (command == "ACK"){
                    Log.i("wichtig", msgIdMap.toString() + " " + msgId)
                    var sendCommand = msgIdMap.remove(msgId);
                    if (sendCommand != null){
                        Log.i("wichtig", "received ack from " + sendCommand)
                        answerMessage(sendCommand, messageArry)

                    }

                }else{
                    Log.i("wichtig", message)
                    messageEdit(messageArry)
                }


            }
            if (bufferIndex > 1024) bufferIndex = 0
        }
    }

    fun answerMessage(sendCommand: String, messageArray: Array<String>){
        runOnUiThread {
            if (sendCommand == "getAutomaticMode") {
                val automaticMode = findViewById<SwitchMaterial>(R.id.automaticModeSwitch)
                automaticMode.isChecked = messageArray[2] == "true"
            }
            if (sendCommand == "getButtonMode"){
                val radioButtonGreen = findViewById<RadioButton>(R.id.radio_button_requestGreen)
                val radioButtonToggle = findViewById<RadioButton>(R.id.radio_button_toggle)
                val radioButtonOff = findViewById<RadioButton>(R.id.radio_button_off)
                if (messageArray[2] == "requestGreen"){
                    Log.i("wichtig", "Auswahl: Request Green")
                    radioButtonGreen.isChecked = true

                } else {
                    if (messageArray[2] == "toggle"){
                        Log.i("wichtig", "Auswahl: Request Toggle")
                        radioButtonToggle.isChecked = true

                    } else {
                        Log.i("wichtig", "Auswahl: Request Off")
                        radioButtonOff.isChecked = true
                    }
                }
            }
            if (sendCommand == "getState"){
                val ampelPicture = findViewById<ImageView>(R.id.ampelPicture)
                if (messageArray[2] == "green"){
                    ampelPicture.setImageResource(R.drawable.ampelgreen)
                } else{
                    ampelPicture.setImageResource(R.drawable.ampelred)
                }

            }

            if (sendCommand == "getGreenTime"){
                greenTime = messageArray[2].toInt()
            }
            if (sendCommand == "getRedTime"){
                redTime = messageArray[2].toInt()
            }
            if (sendCommand == "getGreenDelayTime"){
                greenTimeDelay = messageArray[2].toInt()
            }

        }
    }



    fun messageEdit(input: Array<String>) {

        runOnUiThread {
            val ampelPicture = findViewById<ImageView>(R.id.ampelPicture)
            if (input[0] == "actState"){
                if (input[2] == "green"){
                    ampelPicture.setImageResource(R.drawable.ampelgreen)
                    Log.i("wichtig", "Ampel farbe Grün")

                } else{
                    ampelPicture.setImageResource(R.drawable.ampelred)
                    Log.i("wichtig", "Ampel farbe Rot")

                }
            }

        }
    }


    private class ConnectToDevice(c: Context): AsyncTask<Void, Void, String>() {
        private var connectSuccess: Boolean = true
        private val context: Context

        init {
            this.context = c
        }


        override fun onPreExecute() {
            super.onPreExecute()
            m_progress = ProgressDialog.show(context, "Connecting", "please wait")
        }




        @SuppressLint("MissingPermission")
        override fun doInBackground(vararg p0: Void?): String? {
            try {
                if (m_bluetoothSocket == null || !m_isConnected ){
                    m_bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
                    val device: BluetoothDevice = m_bluetoothAdapter.getRemoteDevice(m_address)
                    m_bluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(m_myUUID)
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery()
                    m_bluetoothSocket!!.connect()

                    thread {
                       (context as ControlActivity).receive()

                    }


                }
            } catch (e: IOException){
                Log.i("data", "Catch, couldn't connect")
                connectSuccess = false

                e.printStackTrace()
            }
            return null
        }


        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if (!connectSuccess){
                Log.i("data", "couldn't connect")
            } else {
                connectSuccess = true
            }
            m_progress.dismiss()
        }

    }
}