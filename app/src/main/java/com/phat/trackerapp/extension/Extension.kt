package com.phat.trackerapp.extension

import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.PorterDuff
import android.os.Build
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.pow
import kotlin.math.roundToInt

fun <R> CoroutineScope.executeAsyncTask(
    onPreExecute: () -> Unit,
    doInBackground: () -> R,
    onPostExecute: (R) -> Unit
) = launch {
    onPreExecute()
    val result = withContext(Dispatchers.Default) { // runs in background thread without blocking the Main Thread
        doInBackground()
    }
    onPostExecute(result)
}

fun Fragment.showNotice(msg: String){
    Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
}

fun Float.roundTo(numFractionDigits: Int): Float {
    val factor = 10.0.pow(numFractionDigits.toDouble())
    return ((this * factor).roundToInt() / factor).toFloat()
}

fun View.setColorFilter(color: Int){
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        background.colorFilter = BlendModeColorFilter(color, BlendMode.SRC_ATOP)
    } else {
        background.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
    }
}