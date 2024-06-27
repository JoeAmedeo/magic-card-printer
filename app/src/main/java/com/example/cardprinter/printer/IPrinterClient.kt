package com.example.cardprinter.printer

import android.graphics.Bitmap
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager

interface IPrinterClient {

    fun print(image: Bitmap, usbManager: UsbManager, usbDevice: UsbDevice)

}