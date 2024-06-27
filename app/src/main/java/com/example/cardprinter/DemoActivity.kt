package com.example.cardprinter

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.activity.ComponentActivity
import com.dantsu.escposprinter.connection.usb.UsbPrintersConnections
import com.example.cardprinter.printer.PrinterClient
import com.example.cardprinter.scryfall.named.NamedRequest
import com.example.cardprinter.scryfall.search.IScryfallClient
import com.example.cardprinter.scryfall.search.ScryfallClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class DemoActivity : ComponentActivity() {

    // on below line we are
    // creating variables for listview
    lateinit var image: ImageView

    // creating variable for searchview
    lateinit var button: Button

    lateinit var imageBitmap: Bitmap

    private val ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION"

    private val usbReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (ACTION_USB_PERMISSION == action) {
                synchronized(this) {
                    val usbManager = getSystemService(USB_SERVICE) as UsbManager

                    val deviceList: HashMap<String, UsbDevice> = usbManager.deviceList

                    val usbDevice = deviceList.entries.first().value

                    if (usbManager.hasPermission(usbDevice)) {
                        if (usbManager != null && usbDevice != null) {
                            val printerClient = PrinterClient()
                            printerClient.print(imageBitmap, usbManager, usbDevice)
                        }
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.demo_main)

        // initializing variables of list view with their ids.
        image = findViewById(R.id.image)
        button = findViewById(R.id.button)
        button.setOnClickListener {
            print()
        }

        // get dependency injections from app container
        val scryfallClient: IScryfallClient = ScryfallClient()

        val name = intent.getStringExtra("name")

        CoroutineScope(Dispatchers.Default).launch {
            val namedRequest = NamedRequest(exact = name!!)
            imageBitmap = scryfallClient.NamedCards(namedRequest)
            runOnUiThread {
                image.setImageBitmap(imageBitmap)
            }
        }

        registerReceiver(this.usbReceiver, IntentFilter(), RECEIVER_EXPORTED)
    }

    private fun print() {
        val usbConnection = UsbPrintersConnections.selectFirstConnected(this)
        val usbManager = this.getSystemService(USB_SERVICE) as UsbManager
        if (usbConnection != null && usbManager != null) {
            val permissionIntent = PendingIntent.getBroadcast(
                this,
                0,
                Intent(ACTION_USB_PERMISSION),
                PendingIntent.FLAG_IMMUTABLE,
            )
            val filter = IntentFilter(ACTION_USB_PERMISSION)
            registerReceiver(this.usbReceiver, filter, RECEIVER_EXPORTED)
            usbManager.requestPermission(usbConnection.device, permissionIntent)
        }
    }
}