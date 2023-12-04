package com.phat.trackerapp.utils.qrcode.model

import com.blood.pressure.utils.qrcode.model.Barcode
import com.blood.pressure.utils.qrcode.model.schema.BarcodeSchema
import com.google.zxing.BarcodeFormat

class ParsedBarcode(barcode: Barcode) {
    var id = barcode.id
    val text = barcode.text
    val formattedText = barcode.formattedText
    val format = barcode.format
    val schema = barcode.schema
    val date = barcode.date
    var note: String? = null

    var url: String? = null
    var youtubeUrl: String? = null

    val isInDb: Boolean
        get() = id != 0L

    val isProductBarcode: Boolean
        get() = when (format) {
            BarcodeFormat.EAN_8, BarcodeFormat.EAN_13, BarcodeFormat.UPC_A, BarcodeFormat.UPC_E -> true
            else -> false
        }

    init {
        when (schema) {
            BarcodeSchema.YOUTUBE -> parseYoutube()
            BarcodeSchema.URL -> parseUrl()
            else -> {}
        }
    }

    private fun parseYoutube() {
        youtubeUrl = text
    }

    private fun parseUrl() {
        url = text
    }

}