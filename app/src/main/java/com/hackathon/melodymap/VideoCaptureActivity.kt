package com.hackathon.melodymap

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.media.MediaRecorder
import android.os.Bundle
import android.util.Log
import android.view.Surface
import android.view.TextureView
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File

class VideoCaptureActivity : AppCompatActivity() {

    private lateinit var textureView: TextureView
    private lateinit var recordButton: ImageButton
    private var cameraDevice: CameraDevice? = null
    private var captureSession: CameraCaptureSession? = null
    private lateinit var previewRequestBuilder: CaptureRequest.Builder
    private lateinit var previewRequest: CaptureRequest
    private lateinit var mediaRecorder: MediaRecorder
    private lateinit var videoFile: File
    private var isRecording = false

    companion object {
        private const val TAG = "VideoCaptureActivity"
        private const val REQUEST_CODE_PERMISSIONS = 1001
        private val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_capture)

        Log.d(TAG, "onCreate called")

        textureView = findViewById(R.id.textureView)
        recordButton = findViewById(R.id.recordButton)

        if (allPermissionsGranted()) {
            Log.d(TAG, "All permissions are granted.")
            textureView.surfaceTextureListener = surfaceTextureListener
        } else {
            Log.d(TAG, "Requesting permissions.")
            requestPermissions()
        }

        recordButton.setOnClickListener {
            if (isRecording) {
                stopRecordingVideo()
            } else {
                startRecordingVideo()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                Log.d(TAG, "Permissions granted by user.")
                textureView.surfaceTextureListener = surfaceTextureListener
            } else {
                Log.d(TAG, "Permissions not granted by user.")
                Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private val surfaceTextureListener = object : TextureView.SurfaceTextureListener {
        override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
            Log.d(TAG, "Surface texture available.")
            initializeCamera()
        }

        override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {
            Log.d(TAG, "Surface texture size changed.")
        }

        override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
            Log.d(TAG, "Surface texture destroyed.")
            return true
        }

        override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {
            Log.d(TAG, "Surface texture updated.")
        }
    }

    private fun initializeCamera() {
        val cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        val cameraId = cameraManager.cameraIdList[0]
        try {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Log.e(TAG, "Camera permission not granted.")
                return
            }
            Log.d(TAG, "Opening camera.")
            cameraManager.openCamera(cameraId, stateCallback, null)
        } catch (e: CameraAccessException) {
            Log.e(TAG, "Camera access exception: ${e.message}")
        }
    }

    private val stateCallback = object : CameraDevice.StateCallback() {
        override fun onOpened(camera: CameraDevice) {
            Log.d(TAG, "Camera opened.")
            cameraDevice = camera
            startPreview()
        }

        override fun onDisconnected(camera: CameraDevice) {
            Log.d(TAG, "Camera disconnected.")
            camera.close()
            cameraDevice = null
        }

        override fun onError(camera: CameraDevice, error: Int) {
            Log.e(TAG, "Camera error: $error")
            camera.close()
            cameraDevice = null
        }
    }

    private fun startPreview() {
        val texture = textureView.surfaceTexture
        texture?.setDefaultBufferSize(1920, 1080)
        val surface = Surface(texture)

        try {
            previewRequestBuilder = cameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            previewRequestBuilder.addTarget(surface)

            cameraDevice!!.createCaptureSession(listOf(surface), object : CameraCaptureSession.StateCallback() {
                override fun onConfigured(session: CameraCaptureSession) {
                    Log.d(TAG, "Camera capture session configured.")
                    captureSession = session
                    previewRequest = previewRequestBuilder.build()
                    captureSession!!.setRepeatingRequest(previewRequest, null, null)
                }

                override fun onConfigureFailed(session: CameraCaptureSession) {
                    Log.e(TAG, "Failed to configure camera.")
                    Toast.makeText(this@VideoCaptureActivity, "Failed to configure camera", Toast.LENGTH_SHORT).show()
                }
            }, null)
        } catch (e: CameraAccessException) {
            Log.e(TAG, "Camera access exception during startPreview: ${e.message}")
        }
    }

    private fun setupMediaRecorder() {
        mediaRecorder = MediaRecorder()
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE)
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        videoFile = File(getExternalFilesDir(null), "video.mp4")
        mediaRecorder.setOutputFile(videoFile.absolutePath)
        mediaRecorder.setVideoEncodingBitRate(10000000)
        mediaRecorder.setVideoFrameRate(30)
        mediaRecorder.setVideoSize(1920, 1080)
        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264)
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        mediaRecorder.prepare()
        Log.d(TAG, "Media recorder setup complete.")
    }

    private fun startRecordingVideo() {
        try {
            setupMediaRecorder()
            val texture = textureView.surfaceTexture
            texture?.setDefaultBufferSize(1920, 1080)
            val previewSurface = Surface(texture)
            val recorderSurface = mediaRecorder.surface

            previewRequestBuilder = cameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_RECORD)
            previewRequestBuilder.addTarget(previewSurface)
            previewRequestBuilder.addTarget(recorderSurface)

            cameraDevice!!.createCaptureSession(listOf(previewSurface, recorderSurface), object : CameraCaptureSession.StateCallback() {
                override fun onConfigured(session: CameraCaptureSession) {
                    Log.d(TAG, "Recording session configured.")
                    captureSession = session
                    previewRequest = previewRequestBuilder.build()
                    captureSession!!.setRepeatingRequest(previewRequest, null, null)
                    mediaRecorder.start()
                    isRecording = true
                    recordButton.setImageResource(R.drawable.ic_stop)  // Change icon to stop
                    Log.d(TAG, "Video recording started.")
                }

                override fun onConfigureFailed(session: CameraCaptureSession) {
                    Log.e(TAG, "Failed to configure recording session.")
                    Toast.makeText(this@VideoCaptureActivity, "Failed to configure camera for recording", Toast.LENGTH_SHORT).show()
                }
            }, null)
        } catch (e: CameraAccessException) {
            Log.e(TAG, "Camera access exception during startRecordingVideo: ${e.message}")
        }
    }

    private fun stopRecordingVideo() {
        try {
            captureSession?.stopRepeating()
            captureSession?.abortCaptures()
            mediaRecorder.stop()
            mediaRecorder.reset()
            isRecording = false
            recordButton.setImageResource(R.drawable.ic_record)  // Change icon back to record
            Log.d(TAG, "Video recording stopped. File saved at: ${videoFile.absolutePath}")
            Toast.makeText(this, "Video saved: ${videoFile.absolutePath}", Toast.LENGTH_SHORT).show()

            // Move to AddDetailsActivity
            val intent = Intent(this, AddDetailsActivity::class.java)
            intent.putExtra("VIDEO_FILE_PATH", videoFile.absolutePath)
            startActivity(intent)
        } catch (e: Exception) {
            Log.e(TAG, "Exception during stopRecordingVideo: ${e.message}")
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume called.")
        if (textureView.isAvailable) {
            initializeCamera()
        } else {
            textureView.surfaceTextureListener = surfaceTextureListener
        }
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause called.")
        captureSession?.close()
        captureSession = null
        cameraDevice?.close()
        cameraDevice = null
    }
}
