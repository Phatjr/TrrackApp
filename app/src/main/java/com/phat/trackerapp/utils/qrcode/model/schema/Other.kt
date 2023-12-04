package com.blood.pressure.utils.qrcode.model.schema

class Other(val text: String): Schema {
    override val schema = BarcodeSchema.BARCODE
    override fun toFormattedText(): String = text
    override fun toBarcodeText(): String = text
}