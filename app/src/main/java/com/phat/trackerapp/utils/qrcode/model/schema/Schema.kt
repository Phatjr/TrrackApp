package com.blood.pressure.utils.qrcode.model.schema

enum class BarcodeSchema {
    TEXT,
    URL,
    YOUTUBE,
    BARCODE;
}

interface Schema {
    val schema: BarcodeSchema
    fun toFormattedText(): String
    fun toBarcodeText(): String
}