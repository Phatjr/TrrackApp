package com.blood.pressure.utils.qrcode.model

import androidx.room.TypeConverters
import com.blood.pressure.utils.qrcode.BarcodeDatabaseTypeConverter
import com.google.zxing.BarcodeFormat

@TypeConverters(BarcodeDatabaseTypeConverter::class)
data class ExportBarcode(
    val date: Long,
    val format: BarcodeFormat,
    val text: String
)