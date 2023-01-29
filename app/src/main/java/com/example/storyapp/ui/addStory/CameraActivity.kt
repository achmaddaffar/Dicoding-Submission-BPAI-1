package com.example.storyapp.ui.addStory

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import com.example.storyapp.R
import com.example.storyapp.databinding.ActivityCameraBinding

class CameraActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCameraBinding
    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private var imageCapture: ImageCapture? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAction()
    }

    override fun onResume() {
        super.onResume()

    }

    private fun setupAction() {
        binding.apply {
            captureImage.setOnClickListener { takePhoto() }
            switchCamera.setOnClickListener {
                cameraSelector = if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) CameraSelector.DEFAULT_FRONT_CAMERA
                else CameraSelector.DEFAULT_BACK_CAMERA
                startCamera()
            }
        }
    }

    private fun takePhoto() {
        
    }

    private fun startCamera() {
        TODO("Not yet implemented")
    }
}