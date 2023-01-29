package com.example.storyapp.ui.addStory

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.R
import com.example.storyapp.data.local.UserPreference
import com.example.storyapp.databinding.ActivityAddStoryBinding
import com.example.storyapp.utils.Helper.Companion.dataStore
import com.example.storyapp.utils.Helper.Companion.reduceFileImage
import com.example.storyapp.utils.Helper.Companion.rotateBitmap
import com.example.storyapp.utils.Helper.Companion.uriToFile
import com.example.storyapp.utils.ViewModelFactory
import com.google.android.material.snackbar.Snackbar
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class AddStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddStoryBinding
    private lateinit var viewModel: AddStoryViewModel
    private lateinit var dialog: Dialog

    private var getFile: File? = null
    private var token: String? = null

    @Suppress("DEPRECATION")
    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val file = it.data?.getSerializableExtra("picture") as File
            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean

            getFile = file
            val result = rotateBitmap(
                BitmapFactory.decodeFile(getFile?.path),
                isBackCamera
            )

            binding.ivPreviewImage.setImageBitmap(result)
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val file = uriToFile(selectedImg, this)
            getFile = file
            binding.ivPreviewImage.setImageURI(selectedImg)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        setupAction()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    getString(R.string.unable_to_obtain_permission),
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore), application)
        )[AddStoryViewModel::class.java]

        viewModel.apply {
            isLoading.observe(this@AddStoryActivity) {isLoading ->
                showLoading(isLoading)
            }

            snackBarText.observe(this@AddStoryActivity) {
                it.getContentIfNotHandled()?.let { snackBarText ->
                    Snackbar.make(
                        window.decorView.rootView,
                        snackBarText,
                        Snackbar.LENGTH_SHORT
                    )
                        .setBackgroundTint(
                            ContextCompat.getColor(
                                this@AddStoryActivity,
                                R.color.red_light
                            )
                        )
                        .setTextColor(
                            ContextCompat.getColor(
                                this@AddStoryActivity,
                                R.color.button_text_color
                            )
                        )
                        .show()
                }
            }

            getUser().observe(this@AddStoryActivity) {user ->
                token = "Bearer ${user.token}"
            }
        }
    }

    private fun setupAction() {
        dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_loading)
        dialog.setCancelable(false)
        if (dialog.window != null)
            dialog.window?.setBackgroundDrawable(ColorDrawable(0))

        binding.apply {
            buttonAdd.setOnClickListener { uploadImage() }
            btnGallery.setOnClickListener { startGallery() }
            btnCamera.setOnClickListener { startCameraX() }
        }
    }

    private fun uploadImage() {
        if (getFile != null) {
            val file = reduceFileImage(getFile as File)

            val description =
                binding.edAddDescription.text.toString().toRequestBody("text/plain".toMediaType())
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                requestImageFile
            )

            viewModel.uploadStory(token as String, imageMultipart, description)
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, getString(R.string.choose_a_picture))
        launcherIntentGallery.launch(chooser)
    }

    private fun startCameraX() {

    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) dialog.show() else dialog.cancel()
    }

    companion object {
        const val CAMERA_X_RESULT = 200
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}