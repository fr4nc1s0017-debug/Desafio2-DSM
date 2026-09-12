package udb.edu.sv.dsm.agenciaviajes.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import java.io.ByteArrayOutputStream

object ImageUtils {

    fun uriToBase64(
        context: Context,
        uri: Uri
    ): String? {

        return try {

            val inputStream =
                context.contentResolver
                    .openInputStream(uri)

            val bitmap =
                BitmapFactory.decodeStream(inputStream)

            inputStream?.close()

            if (bitmap == null) {
                return null
            }

            val resized =
                resizeBitmap(bitmap, 800)

            val outputStream =
                ByteArrayOutputStream()

            resized.compress(
                Bitmap.CompressFormat.JPEG,
                65,
                outputStream
            )

            Base64.encodeToString(
                outputStream.toByteArray(),
                Base64.DEFAULT
            )

        } catch (e: Exception) {

            e.printStackTrace()
            null
        }
    }

    fun base64ToBitmap(
        base64: String
    ): Bitmap? {

        return try {

            if (base64.isEmpty()) {
                return null
            }

            val bytes =
                Base64.decode(
                    base64,
                    Base64.DEFAULT
                )

            BitmapFactory.decodeByteArray(
                bytes,
                0,
                bytes.size
            )

        } catch (e: Exception) {

            e.printStackTrace()
            null
        }
    }

    private fun resizeBitmap(
        bitmap: Bitmap,
        maxWidth: Int
    ): Bitmap {

        if (bitmap.width <= maxWidth) {
            return bitmap
        }

        val ratio =
            maxWidth.toDouble() /
                    bitmap.width.toDouble()

        val newHeight =
            (bitmap.height * ratio).toInt()

        return Bitmap.createScaledBitmap(
            bitmap,
            maxWidth,
            newHeight,
            true
        )
    }
}