package com.phat.trackerapp.screen.scanner

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.blood.pressure.utils.qrcode.BarcodeParser
import com.blood.pressure.utils.qrcode.model.schema.BarcodeSchema
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerException
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ScanMode
import com.bumptech.glide.Glide
import com.google.zxing.BarcodeFormat
import com.google.zxing.Result
import com.google.zxing.client.android.BeepManager
import com.phat.trackerapp.R
import com.phat.trackerapp.extension.showNotice
import com.phat.trackerapp.utils.Utils
import com.phat.trackerapp.utils.qrcode.model.ParsedBarcode
import kotlinx.android.synthetic.main.activity_food_scanner.*

class FoodScannerActivity : AppCompatActivity() {
    companion object{
        private const val PERMISSION_REQUEST_CODE = 101
    }

    private lateinit var codeScanner: CodeScanner

    private lateinit var beepManager: BeepManager

    private val vibrationPattern = arrayOf<Long>(0, 350).toLongArray()

    private var barcodeParser = BarcodeParser

    private lateinit var barcode: ParsedBarcode

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food_scanner)
        Utils.changeStatusBarColor(this,R.color.white)
        initView()
        handleEvent()
    }

    override fun onResume() {
        super.onResume()
        requestPermissions()
    }

    private fun initView() {
        Glide.with(this).load("https://alloffice.app/nghia/BloodPressure/animated2_scan.gif").into(imgGuideline)

        codeScanner = CodeScanner(this, scannerView).apply {
            camera = CodeScanner.CAMERA_BACK
            autoFocusMode = AutoFocusMode.SAFE

            formats = listOf(
                BarcodeFormat.QR_CODE,
                BarcodeFormat.DATA_MATRIX,
                BarcodeFormat.AZTEC,
                BarcodeFormat.PDF_417,
                BarcodeFormat.EAN_13,
                BarcodeFormat.EAN_8,
                BarcodeFormat.UPC_E,
                BarcodeFormat.UPC_A,
                BarcodeFormat.CODE_128,
                BarcodeFormat.CODE_93,
                BarcodeFormat.CODE_39,
                BarcodeFormat.CODABAR,
                BarcodeFormat.ITF
            )
            scanMode = ScanMode.SINGLE
            isAutoFocusEnabled = true
            isFlashEnabled = false
            isTouchFocusEnabled = false
            decodeCallback = DecodeCallback(::handleScannedBarcode)
//            errorCallback = ErrorCallback(::showError)
        }
    }

    private fun handleEvent() {
        btnBack.setOnClickListener {
            finish()
        }

        btnFlash.setOnClickListener {
            codeScanner.isFlashEnabled = codeScanner.isFlashEnabled.not()
            if(codeScanner.isFlashEnabled){
                btnFlash.setColorFilter(Color.BLACK)
            }else{
                btnFlash.setColorFilter(ContextCompat.getColor(this,R.color.color_disable_btn_measure))
            }
        }
    }

    private fun restartPreview() {
        runOnUiThread {
            codeScanner.startPreview()
        }
    }

    private fun startPreview(){
        try {
            codeScanner.startPreview()
        }catch (e: CodeScannerException){
            e.printStackTrace()
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    private fun requestPermissions() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), PERMISSION_REQUEST_CODE)
        }else{
           startPreview()
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.d("3232323", "gggggg")
        if (requestCode == PERMISSION_REQUEST_CODE ) {
            if((grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)){
                startPreview()
            }
        }
    }

    private fun handleScannedBarcode(result: Result) {
        vibrateIfNeeded()
        val barcode = barcodeParser.parseResult(result)
        this.barcode = ParsedBarcode(barcode)
        Log.d("444444", barcode.text)
//        restartPreview()
        openApp()
    }

    private fun openApp() {
        when (barcode.schema) {
            BarcodeSchema.YOUTUBE -> openAppWithIntent()
            BarcodeSchema.BARCODE -> openGoogle(barcode.text)
            BarcodeSchema.URL -> openGoogle(barcode.url.orEmpty())
            else -> openGoogle(barcode.text)
        }
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }

    private fun openGoogle(text: String) {
        val uri = if (text.contains("https://")) {
            Uri.parse(text)
        } else {
            Uri.parse("https://www.google.com/search?q=" + text)
        }
        val intent = Intent(Intent.ACTION_VIEW, uri)
        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            runOnUiThread {
                showNotice(getString(R.string.txt_no_found_app))
            }
            Log.d("5556666", e.message.toString())
        }
    }



    private fun openAppWithIntent() {
        var uri = Uri.parse(barcode.text)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            showNotice(getString(R.string.txt_no_found_app))
            Log.d("5556666", e.message.toString())
        }
    }

    private fun vibrateIfNeeded() {
        beepManager = BeepManager(this)
        this.apply {
            runOnUiThread {
                val vibrator: Vibrator? = getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                vibrator?.vibrateOnce(vibrationPattern)
                beepManager.playBeepSound()
            }
        }

    }

    fun Vibrator.vibrateOnce(pattern: LongArray) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrate(VibrationEffect.createWaveform(pattern, -1))
        } else {
            vibrate(pattern, -1)
        }
    }


}