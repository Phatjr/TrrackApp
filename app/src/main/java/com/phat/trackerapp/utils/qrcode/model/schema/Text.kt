package com.blood.pressure.utils.qrcode.model.schema

class Text(val text: String): Schema {
    override val schema = BarcodeSchema.TEXT
    override fun toFormattedText(): String = text
    override fun toBarcodeText(): String = text
}