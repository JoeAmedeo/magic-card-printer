package com.example.cardprinter.printer

import android.graphics.Bitmap
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import com.dantsu.escposprinter.EscPosPrinter
import com.dantsu.escposprinter.connection.usb.UsbConnection
import com.dantsu.escposprinter.textparser.PrinterTextParserImg
import kotlin.io.encoding.ExperimentalEncodingApi


class PrinterClient : IPrinterClient {

    @OptIn(ExperimentalEncodingApi::class)
    override fun print(image: Bitmap, usbManager: UsbManager, usbDevice: UsbDevice) {
        val printer: EscPosPrinter = EscPosPrinter(UsbConnection(usbManager, usbDevice), 203, 48f, 32)

        /*
        val matrix = Matrix()

        matrix.postRotate(90f)

        val rotatedImage = Bitmap.createBitmap(image, 0, 0, image.width, image.height, matrix, true)

        printer.printFormattedTextAndCut("[C]<img>" + PrinterTextParserImg.bitmapToHexadecimalString(printer, rotatedImage)+"</img>")
        */

        val width = image.width
        val height = image.height

        val textToPrint = StringBuilder()

        var y = 0
        while (y < height) {
            val bitmap = Bitmap.createBitmap(
                image,
                0,
                y,
                width,
                if ((y + 256 >= height)) height - y else 256
            )
            textToPrint.append(
                "[C]<img>" + PrinterTextParserImg.bitmapToHexadecimalString(
                    printer,
                    bitmap
                ) + "</img>\n"
            )
            y += 256
        }

        printer.printFormattedTextAndCut(textToPrint.toString())
    }
}