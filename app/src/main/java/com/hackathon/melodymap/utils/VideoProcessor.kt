package com.hackathon.melodymap

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.util.Log
import java.io.File

class VideoProcesser {

    companion object {
        private const val TAG = "VideoProcessingHelper"
    }

    fun processVideo(context: Context, videoFilePath: String, callback: (List<Bitmap>) -> Unit) {
        val frames = mutableListOf<Bitmap>()
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(videoFilePath)
        val duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong() ?: 0L
        val interval = duration / 8

        try {
            for (i in 0 until 8) {
                val frameTime = i * interval * 1000
                val bitmap = retriever.getFrameAtTime(frameTime, MediaMetadataRetriever.OPTION_CLOSEST)
                if (bitmap != null) {
                    frames.add(bitmap)
                    saveBitmap(context, bitmap, "frame_$i.png")
                    Log.d(TAG, "Frame $i processed")
                }
            }
            callback(frames)
        } catch (e: Exception) {
            Log.e(TAG, "Error processing video: ${e.message}")
        } finally {
            retriever.release()
        }
    }

    private fun saveBitmap(context: Context, bitmap: Bitmap, fileName: String) {
        val file = File(context.getExternalFilesDir(null), fileName)
        file.outputStream().use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            Log.d(TAG, "Bitmap saved: $fileName")
        }
    }
}
