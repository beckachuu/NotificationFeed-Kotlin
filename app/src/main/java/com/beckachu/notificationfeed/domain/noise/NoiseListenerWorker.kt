package com.beckachu.notificationfeed.domain.noise

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioRecord
import android.media.MediaRecorder
import android.provider.Settings
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.beckachu.notificationfeed.data.SharedPrefsManager
import java.util.concurrent.TimeUnit
import kotlin.math.sqrt

class NoiseListenerWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    private val audioManager by lazy { applicationContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager }
    private val sharedPref: SharedPreferences =
        context.getSharedPreferences(SharedPrefsManager.DEFAULT_NAME, Context.MODE_PRIVATE)

    override suspend fun doWork(): Result {
        val averageNoiseLevel = recordNoise()
        adjustVolume(averageNoiseLevel)
        println("New noise level: $averageNoiseLevel")

        val autoAdjustVolume =
            SharedPrefsManager.getBool(sharedPref, SharedPrefsManager.AUTO_VOLUME, false)
        if (autoAdjustVolume) {
            // Schedule the next run
            val delayDuration = 5000L // 5 seconds
            val nextRunRequest = OneTimeWorkRequestBuilder<NoiseListenerWorker>()
                .setInitialDelay(delayDuration, TimeUnit.MILLISECONDS)
                .addTag("noise_listener_worker_tag")
                .build()
            WorkManager.getInstance(applicationContext).enqueue(nextRunRequest)
        }

        return Result.success()
    }

    private fun recordNoise(): Float {
        val sampleRate = 8000
        val bufferSize = AudioRecord.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )

        val RECORD_AUDIO_REQUEST_CODE = 101

        val audioRecord: AudioRecord = if (ActivityCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            val intent = Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            applicationContext.startActivity(intent)

            AudioRecord(
                MediaRecorder.AudioSource.MIC,
                sampleRate,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize
            )
        } else {
            AudioRecord(
                MediaRecorder.AudioSource.MIC,
                sampleRate,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize
            )
        }

        if (audioRecord.state != AudioRecord.STATE_INITIALIZED) {
            // Handle failed AudioRecord initialization (log error, return default value)
            return 0f
        }

        val audioData = ShortArray(bufferSize)
        audioRecord.startRecording()

        val startTime = System.currentTimeMillis()
        var totalSquared = 0.0
        var sampleCount = 0
        while (System.currentTimeMillis() - startTime < 3000) {
            val readSize = audioRecord.read(audioData, 0, bufferSize)
            if (readSize == AudioRecord.ERROR_INVALID_OPERATION || readSize == AudioRecord.ERROR_BAD_VALUE) {
                Log.e("Audio prob", "Audio read has problem")
                break
            }

            for (i in 0 until readSize) {
                val sample = audioData[i]
                totalSquared += (sample * sample).toDouble()
                sampleCount++
            }
        }

        audioRecord.stop()
        audioRecord.release()

        // Calculate the root mean square value as the noise level
        val averageSquared = totalSquared / sampleCount
        val rms = sqrt(averageSquared)

        val normalizedNoiseLevel = rms / 3000

        return normalizedNoiseLevel.toFloat()
    }

    private fun adjustVolume(averageNoiseLevel: Float) {
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        val newVolume = averageNoiseLevel * maxVolume
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, newVolume.toInt(), 0)
    }

}

