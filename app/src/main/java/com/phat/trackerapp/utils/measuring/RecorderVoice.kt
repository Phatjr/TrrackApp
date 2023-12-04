package com.blood.pressure.utils.measuring

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaPlayer
import android.media.MediaRecorder
import java.io.IOException


class RecorderVoice(context: Context) {
    private val context = context
    private var myRecorder: MediaRecorder? = null
    private var myPlay: MediaPlayer? = null
    private var outputFile: String? = null

    @SuppressLint("WrongConstant")
    fun init() {
        outputFile =
            context.getExternalFilesDir(null)!!.absoluteFile.toString() + "/fmeet_voice.3gpp"
        myRecorder = MediaRecorder()
        myRecorder?.let {
            it.setAudioSource(MediaRecorder.AudioSource.MIC)
            it.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            it.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB)
            it.setAudioSamplingRate(44100)
            it.setOutputFile(outputFile)
        }
    }

    fun start() {
        try {
            myRecorder = null
            init()
            myRecorder?.let {
                it.prepare()
                it.start()
            }
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun stop() {
        try {
            myRecorder?.let {
                it.stop()
                it.release()
            }
            myRecorder = null
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        } catch (e: RuntimeException) {
            e.printStackTrace()
        }
    }

    fun playBeep() {
        try {
            if(isPlay()) return
            myPlay = MediaPlayer()
            val descriptor = context.assets.openFd("sound_blood.mp3")
           myPlay?.let {
               it.setDataSource(
                   descriptor.fileDescriptor,
                   descriptor.startOffset,
                   descriptor.length
               )
               descriptor.close()
               it.prepare()
               it.setVolume(1f, 1f)
               it.isLooping = true

               it.start()
           }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun play(path: String) {
        try {
            myPlay = MediaPlayer()
            myPlay?.let {
                it.setDataSource(path)
                it.prepare()
                it.setOnCompletionListener {
                    //  (context as ChatFayoActivity).btnPlayVoice.setImageResource(R.drawable.ic_play)
                    stop()
                }
                it.start()
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun stopPlay() {
        try {
            myPlay?.let {
                it.stop()
                it.release()
            }
            myPlay = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getTime(): Int {
        myPlay?.let {
            return it.duration
        }
        return 0
    }

    fun isPlay(): Boolean {
        myPlay?.let {
            return it.isPlaying
        }
        return false
    }
}
