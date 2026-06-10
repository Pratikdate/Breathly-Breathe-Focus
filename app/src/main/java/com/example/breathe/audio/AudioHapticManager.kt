package com.shanacoder.breathly.audio

import android.content.Context
import android.media.MediaPlayer
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.speech.tts.TextToSpeech
import com.shanacoder.breathly.R
import java.util.Locale

class AudioHapticManager(private val context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isTtsReady = false
    private var mediaPlayer: MediaPlayer? = null
    private var naturePlayer: MediaPlayer? = null

    // We get the vibrator service
    private val vibrator: Vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vibratorManager.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    init {
        tts = TextToSpeech(context, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.language = Locale.US
            isTtsReady = true
        }
    }

    fun playVoice(text: String, followRhythmEnabled: Boolean) {
        if (!followRhythmEnabled) return

        val resId = when (text.lowercase()) {
            "breathe in" -> R.raw.breathe_in
            "breathe out" -> R.raw.breath_out
            else -> null
        }

        if (resId != null) {
            playRawResource(resId)
        } else if (isTtsReady) {
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }

    private fun playRawResource(resId: Int) {
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer.create(context, resId)
            mediaPlayer?.start()
            mediaPlayer?.setOnCompletionListener { 
                it.release()
                if (mediaPlayer == it) mediaPlayer = null
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun triggerHaptic(hapticsEnabled: Boolean) {
        if (!hapticsEnabled) return
        
        if (vibrator.hasVibrator()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Gentle tick vibration
                vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(50)
            }
        }
    }

    // Stub for nature sounds (placeholder until assets are added)
    fun startNatureSound(soundEnabled: Boolean) {
        if (!soundEnabled) return
        // TODO: Initialize naturePlayer with R.raw.rain or similar
    }

    fun stopNatureSound() {
        naturePlayer?.stop()
        naturePlayer?.release()
        naturePlayer = null
    }

    fun release() {
        tts?.stop()
        tts?.shutdown()
        mediaPlayer?.release()
        naturePlayer?.release()
    }
}
