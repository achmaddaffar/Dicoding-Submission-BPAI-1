package com.example.storyapp.ui.addStory

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.R
import com.example.storyapp.data.local.UserPreference
import com.example.storyapp.databinding.ActivityAddStoryBinding
import com.example.storyapp.ui.addStory.CameraActivity.Companion.IS_BACK_CAMERA_EXTRA
import com.example.storyapp.ui.addStory.CameraActivity.Companion.PICTURE_EXTRA
import com.example.storyapp.utils.Helper.Companion.bitmapToFile
import com.example.storyapp.utils.Helper.Companion.dataStore
import com.example.storyapp.utils.Helper.Companion.rotateBitmap
import com.example.storyapp.utils.Helper.Companion.uriToFile
import com.example.storyapp.utils.ViewModelFactory
import com.google.android.material.snackbar.Snackbar
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
            val file = it.data?.getSerializableExtra(PICTURE_EXTRA) as File
            val isBackCamera = it.data?.getBooleanExtra(IS_BACK_CAMERA_EXTRA, true) as Boolean
            val bmp = rotateBitmap(
                BitmapFactory.decodeFile(file.path),
                isBackCamera
            )
            val result = bitmapToFile(this, bmp)
            getFile = result
            viewModel.setFile(result)
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val file = uriToFile(selectedImg, this)
            getFile = file
            viewModel.setFile(file)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        setupViewModel()
        setupAction()
        setButtonEnable()
    }

    override fun onResume() {
        super.onResume()
        setButtonEnable()
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
            isLoading.observe(this@AddStoryActivity) { isLoading ->
                showLoading(isLoading)
            }

            snackBarText.observe(this@AddStoryActivity) {
                it.getContentIfNotHandled()?.let { snackBarText ->
                    if (snackBarText != getString(R.string.failed_to_connect))
                        finish()

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
                                R.color.black
                            )
                        )
                        .show()
                }
            }

            getUser().observe(this@AddStoryActivity) { user ->
                token = "Bearer ${user.token}"
            }

            tempFile.observe(this@AddStoryActivity) { file ->
                val bitmap = BitmapFactory.decodeFile(file.path)
                binding.ivPreviewImage.setImageBitmap(bitmap)
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
            buttonAdd.setOnClickListener {
                viewModel.uploadStory(
                    token as String,
                    binding.edAddDescription.text.toString()
                )
            }
            btnGallery.setOnClickListener { startGallery() }
            btnCamera.setOnClickListener { startCameraX() }

            edAddDescription.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    c: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(c: CharSequence?, start: Int, before: Int, count: Int) {
                    setButtonEnable()
                }

                override fun afterTextChanged(s: Editable?) {
                    edAddDescription.error = if (edAddDescription.text.toString()
                            .isEmpty()
                    ) getString(R.string.this_field_cannot_be_blank)
                    else null
                }
            })
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
        val intent = Intent(this, CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private fun setButtonEnable() {
        val storyImage = binding.ivPreviewImage.drawable
        val description = binding.edAddDescription.text

        binding.buttonAdd.isEnabled =
            storyImage != null && description != null && description.toString()
                .isNotEmpty()
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