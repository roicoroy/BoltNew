package com.example.boltnew.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.UUID

object ImageUtils {
    
    fun saveImageToInternalStorage(context: Context, imageUri: Uri): String {
        val inputStream = context.contentResolver.openInputStream(imageUri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        inputStream?.close()
        
        // Create a unique filename
        val filename = "avatar_${UUID.randomUUID()}.jpg"
        val file = File(context.filesDir, filename)
        
        try {
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
            outputStream.close()
            return file.absolutePath
        } catch (e: IOException) {
            throw Exception("Failed to save image: ${e.message}")
        }
    }
    
    fun getImageFile(context: Context, filename: String): File {
        return File(context.filesDir, filename)
    }
    
    fun deleteImageFile(filePath: String): Boolean {
        return try {
            val file = File(filePath)
            file.delete()
        } catch (e: Exception) {
            false
        }
    }
}