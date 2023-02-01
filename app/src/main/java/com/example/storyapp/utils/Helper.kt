package com.example.storyapp.utils

import android.app.Application
import android.content.ContentResolver
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Environment
import android.util.Patterns
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.storyapp.R
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*

class Helper {
    companion object {
        fun String.isValidPassword() = !isNullOrEmpty() && this.count() >= 8
        fun CharSequence?.isValidEmail() =
            !isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()

        val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

        const val BASE_URL = "https://story-api.dicoding.dev/v1/"
        private const val FILENAME_FORMAT = "dd-MM-yyyy"
        private val timeStamp: String = SimpleDateFormat(
            FILENAME_FORMAT,
            Locale.US
        ).format(System.currentTimeMillis())

        private fun createCustomTempFile(context: Context): File {
            val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            return File.createTempFile(timeStamp, ".jpg", storageDir)
        }

        fun createFile(application: Application): File {
            val mediaDir = application.externalMediaDirs.firstOrNull()?.let {
                File(it, application.resources.getString(R.string.app_name)).apply { mkdirs() }
            }

            val outputDirectory = if (
                mediaDir != null && mediaDir.exists()
            ) mediaDir else application.filesDir

            return File(outputDirectory, "$timeStamp.jpg")
        }

        fun bitmapToFile(context: Context, bitmap: Bitmap): File {
            val wrapper = ContextWrapper(context)
            var file = wrapper.getDir("Images", Context.MODE_PRIVATE)
            file = File(file, "$timeStamp.jpg")
            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 25, stream)
            stream.flush()
            stream.close()
            return file
        }

        fun rotateBitmap(bitmap: Bitmap, isBackCamera: Boolean = false): Bitmap {
            val matrix = Matrix()
            return if (isBackCamera) {
                matrix.postRotate(90f)
                Bitmap.createBitmap(
                    bitmap,
                    0,
                    0,
                    bitmap.width,
                    bitmap.height,
                    matrix,
                    true
                )
            } else {
                matrix.postRotate(-90f)
                matrix.postScale(-1f, 1f, bitmap.width / 2f, bitmap.height / 2f)
                Bitmap.createBitmap(
                    bitmap,
                    0,
                    0,
                    bitmap.width,
                    bitmap.height,
                    matrix,
                    true
                )
            }
        }

        fun uriToFile(selectedImg: Uri, context: Context): File {
            val contentResolver: ContentResolver = context.contentResolver
            val file = createCustomTempFile(context)

            val inputStream = contentResolver.openInputStream(selectedImg) as InputStream
            val outputStream: OutputStream = FileOutputStream(file)
            val buf = ByteArray(1024)
            var len: Int
            while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)
            outputStream.close()
            inputStream.close()

            return file
        }

        fun reduceFileImage(file: File): File {
            val bitmap = BitmapFactory.decodeFile(file.path)

            var compressQuality = 100
            var streamLength: Int

            do {
                val bmpStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
                val bmpPickByteArray = bmpStream.toByteArray()
                streamLength = bmpPickByteArray.size
                compressQuality -= 5
            } while (streamLength > 1_000_000)

            bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))

            return file
        }
    }
}