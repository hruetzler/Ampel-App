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
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.IOException
import java.util.*


class ControlActivity: AppCompatActivity(){

    companion object {
        var m_myUUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
        var m_bluetoothSocket: BluetoothSocket? = null
        lateinit var m_progress: ProgressDialog
        lateinit var m_bluetoothAdapter: BluetoothAdapter
        var m_isConnected: Boolean = false
        lateinit var m_address: String

    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.control_layout)
        m_address = intent.getStringExtra(MainActivity.EXTRA_ADDRESS).toString()

        val bluetooth = Bluetooth()

        bluetooth.connectBluetooth()

        ConnectToDevice(this).execute()
        val settingButton = findViewById<ImageButton>(R.id.confirm)
        val ampelPicture = findViewById<ImageView>(R.id.ampelPicture)
        val ampelSchalter = findViewById<Button>(R.id.schalter)
        val ampelRed = findViewById<Button>(R.id.ampelRed)
        val ampelGreen = findViewById<Button>(R.id.ampelGreen)
        val control_led_disconnect = findViewById<Button>(R.id.control_led_disconnect)

        settingButton.setOnClickListener { setting() }
        ampelPicture.setOnClickListener { sendComand("toggle") }
        ampelSchalter.setOnClickListener { sendComand("toggle") }
        ampelRed.setOnClickListener { sendComand("Red") }
        ampelGreen.setOnClickListener { sendComand("Green") }
        control_led_disconnect.setOnClickListener { disconnect() }



    }

    private fun sendComand(input: String){
        if (m_bluetoothSocket != null){
            try {
                m_bluetoothSocket!!.outputStream.write(input.plus("\r\n").toByteArray())
                /*if (m_bluetoothSocket!!.inputStream.available()){
                    m_bluetoothSocket!!.inputStream.readBytes();
                }*/
            } catch (e: IOException){
                Toast.makeText(this, "Ein Satz mit X das war wohl nix.", Toast.LENGTH_LONG).show()
                e.printStackTrace()
            }
        }
    }

    private fun setting(){
        val settings = Intent(this, SettingActivity::class.java)
        Log.i("wichtig", "jetzt wird die Activity gestartet")
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
                Log.i("data", "connect2")
            }
            m_progress.dismiss()
        }

    }
}