package com.example.ampel

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.content.pm.PackageManager
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


class MainActivity : AppCompatActivity() {

    private var m_bluetoothAdapter:BluetoothAdapter? = null
    private val REQUEST_ENABLE_BLUETOOTH = 1


    companion object{
        val EXTRA_ADDRESS: String = "Device_address"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)




        m_bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (m_bluetoothAdapter == null){
            Toast.makeText(this, "this device doesen't support bluetooth", Toast.LENGTH_SHORT).show()
            return
        }

        permissionBluetooth()
        val refresh = findViewById<Button>(R.id.select_device_refresh)
        val connect = findViewById<Button>(R.id.connect)
        connect.setOnClickListener { connection() }
        refresh.setOnClickListener { refresh() }
    }


    private fun connection(){
        val address: String = getString(R.string.ampelAddress)
        val intent = Intent(this, ControlActivity::class.java)
        intent.putExtra(EXTRA_ADDRESS, address)
        startActivity(intent)
    }
    private fun refresh(){
        val connect = findViewById<Button>(R.id.connect)
        val ampelStart = findViewById<ImageView>(R.id.ampelStart)
        if (m_bluetoothAdapter!!.isEnabled){
            connect.visibility = View.VISIBLE
            ampelStart.setImageResource(R.drawable.ampelgreen)
        }
    }



    private fun permissionBluetooth(){
        val connect = findViewById<Button>(R.id.connect)
        val ampelStart = findViewById<ImageView>(R.id.ampelStart)
        val bluetoothOFF = findViewById<TextView>(R.id.bluetoothOFF)
        val requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ) { permissions ->
                var allowed = true;
                permissions.entries.forEach {
                    val permissionName = it.key
                    val isGranted = it.value
                    if (isGranted) {
                        // Permission is granted


                    } else {
                        // Explain to the user that the feature is unavailable because the
                        // features requires a permission that the user has denied. At the
                        // same time, respect the user's decision. Don't link to system
                        // settings in an effort to convince the user to change their
                        // decision.

                        ampelStart.setImageResource(R.drawable.ampelred)

                        val connectLost = findViewById<TextView>(R.id.connectLost)
                        connectLost.visibility = View.VISIBLE
                        connect.visibility = View.INVISIBLE

                        Toast.makeText(this, "Ich brauche die Bluetooth erlaubnis, um auf die Ampel zuzugreifen", Toast.LENGTH_LONG).show()
                        allowed = false;

                        // Permission is denied
                    }
                }


                if (allowed) {
                    ampelStart.setImageResource(R.drawable.ampelgreen)
                    if (!m_bluetoothAdapter!!.isEnabled) {
                        ampelStart.setImageResource(R.drawable.ampelred)
                        bluetoothOFF.visibility = View.VISIBLE
                        connect.visibility = View.INVISIBLE
                        val enableBluetoothIntent =
                            Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                        startActivityForResult(enableBluetoothIntent, REQUEST_ENABLE_BLUETOOTH)

                    }
                }
            }


        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT + ContextCompat.checkSelfPermission(this,Manifest.permission.BLUETOOTH_SCAN)
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            requestPermissionLauncher.launch(
                arrayOf(Manifest.permission.BLUETOOTH_CONNECT,Manifest.permission.BLUETOOTH_SCAN)
            )

        }
    }
}