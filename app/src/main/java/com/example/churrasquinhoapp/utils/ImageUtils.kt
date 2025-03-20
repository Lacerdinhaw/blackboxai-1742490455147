package com.example.churrasquinhoapp.utils

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.util.Base64
import androidx.core.content.ContextCompat
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object ImageUtils {
    private const val COMPRESSION_QUALITY = 80
    private const val MAX_IMAGE_DIMENSION = 1024

    /**
     * Bitmap compression and resizing
     */
    fun compressBitmap(bitmap: Bitmap, quality: Int = COMPRESSION_QUALITY): ByteArray {
        return ByteArrayOutputStream().use { stream ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream)
            stream.toByteArray()
        }
    }

    fun resizeBitmap(bitmap: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
        if (bitmap.width <= maxWidth && bitmap.height <= maxHeight) {
            return bitmap
        }

        val ratioWidth = maxWidth.toFloat() / bitmap.width
        val ratioHeight = maxHeight.toFloat() / bitmap.height
        val ratio = minOf(ratioWidth, ratioHeight)

        val newWidth = (bitmap.width * ratio).toInt()
        val newHeight = (bitmap.height * ratio).toInt()

        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }

    /**
     * Bitmap conversion
     */
    fun bitmapToBase64(bitmap: Bitmap): String {
        val byteArray = compressBitmap(bitmap)
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    fun base64ToBitmap(base64String: String): Bitmap? {
        return try {
            val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        } catch (e: Exception) {
            Logger.e("Error converting base64 to bitmap", e)
            null
        }
    }

    /**
     * Drawable conversion
     */
    fun drawableToBitmap(drawable: Drawable): Bitmap {
        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }

        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    /**
     * Image file handling
     */
    fun saveBitmapToFile(context: Context, bitmap: Bitmap, fileName: String): File? {
        return try {
            val file = File(context.filesDir, fileName)
            FileOutputStream(file).use { stream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, COMPRESSION_QUALITY, stream)
            }
            file
        } catch (e: IOException) {
            Logger.e("Error saving bitmap to file", e)
            null
        }
    }

    fun loadBitmapFromFile(file: File): Bitmap? {
        return try {
            BitmapFactory.decodeFile(file.absolutePath)
        } catch (e: Exception) {
            Logger.e("Error loading bitmap from file", e)
            null
        }
    }

    /**
     * Image manipulation
     */
    fun rotateBitmap(bitmap: Bitmap, degrees: Float): Bitmap {
        val matrix = Matrix().apply { postRotate(degrees) }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    fun cropBitmap(bitmap: Bitmap, rect: Rect): Bitmap {
        return Bitmap.createBitmap(
            bitmap,
            rect.left,
            rect.top,
            rect.width(),
            rect.height()
        )
    }

    fun applyGrayscale(bitmap: Bitmap): Bitmap {
        val output = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)
        val canvas = Canvas(output)
        val paint = Paint().apply {
            colorFilter = ColorMatrixColorFilter(ColorMatrix().apply { setSaturation(0f) })
        }
        canvas.drawBitmap(bitmap, 0f, 0f, paint)
        return output
    }

    /**
     * Image effects
     */
    fun applyBlur(bitmap: Bitmap, radius: Float): Bitmap {
        val output = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)
        val canvas = Canvas(output)
        val paint = Paint().apply {
            maskFilter = BlurMaskFilter(radius, BlurMaskFilter.Blur.NORMAL)
        }
        canvas.drawBitmap(bitmap, 0f, 0f, paint)
        return output
    }

    fun adjustBrightness(bitmap: Bitmap, value: Float): Bitmap {
        val output = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)
        val canvas = Canvas(output)
        val paint = Paint().apply {
            colorFilter = ColorMatrixColorFilter(ColorMatrix().apply {
                set(floatArrayOf(
                    1f, 0f, 0f, 0f, value,
                    0f, 1f, 0f, 0f, value,
                    0f, 0f, 1f, 0f, value,
                    0f, 0f, 0f, 1f, 0f
                ))
            })
        }
        canvas.drawBitmap(bitmap, 0f, 0f, paint)
        return output
    }

    /**
     * Image information
     */
    fun getImageOrientation(context: Context, uri: Uri): Int {
        return try {
            context.contentResolver.openInputStream(uri)?.use { input ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    ExifInterface(input).getAttributeInt(
                        ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_NORMAL
                    )
                } else {
                    ExifInterface(uri.path!!).getAttributeInt(
                        ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_NORMAL
                    )
                }
            } ?: ExifInterface.ORIENTATION_NORMAL
        } catch (e: IOException) {
            Logger.e("Error getting image orientation", e)
            ExifInterface.ORIENTATION_NORMAL
        }
    }

    /**
     * Resource loading
     */
    fun loadBitmapFromResource(context: Context, resourceId: Int): Bitmap? {
        return try {
            ContextCompat.getDrawable(context, resourceId)?.let { drawable ->
                drawableToBitmap(drawable)
            }
        } catch (e: Exception) {
            Logger.e("Error loading bitmap from resource", e)
            null
        }
    }

    /**
     * Utility functions
     */
    fun calculateInSampleSize(
        options: BitmapFactory.Options,
        reqWidth: Int,
        reqHeight: Int
    ): Int {
        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2

            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }

    fun decodeSampledBitmap(
        context: Context,
        uri: Uri,
        reqWidth: Int,
        reqHeight: Int
    ): Bitmap? {
        return try {
            context.contentResolver.openInputStream(uri)?.use { input ->
                BitmapFactory.Options().run {
                    inJustDecodeBounds = true
                    BitmapFactory.decodeStream(input, null, this)

                    inSampleSize = calculateInSampleSize(this, reqWidth, reqHeight)
                    inJustDecodeBounds = false

                    context.contentResolver.openInputStream(uri)?.use { input2 ->
                        BitmapFactory.decodeStream(input2, null, this)
                    }
                }
            }
        } catch (e: Exception) {
            Logger.e("Error decoding sampled bitmap", e)
            null
        }
    }
}