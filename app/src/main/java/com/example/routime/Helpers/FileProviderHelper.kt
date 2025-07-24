package com.example.routime.Helpers

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.routime.Constants
import com.example.routime.FileType
import java.io.File
import java.io.FileOutputStream

class FileProviderHelper(val context :Context) {
    private val resolver = context.contentResolver

    fun savePictureToExternalAppStorage(uri: Uri) : Uri?{
        val inputStream = resolver.openInputStream(uri)?: return null

        val storageDir = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), Constants.SAVED_PICTURES_PATH)
        if (!storageDir.exists()) storageDir.mkdirs()

        val fileName = getFileName(uri)?: "IMG_${System.currentTimeMillis()}.jpg"
        val imgFile = File(storageDir,fileName)

       runCatching {
           val fileOutputStream = FileOutputStream(imgFile)
           inputStream.copyTo(fileOutputStream)

           inputStream.close()
           fileOutputStream.close()
           return getFileUriExternalAppStorage(imgFile)
       }.onFailure {
           it.printStackTrace()
       }
        return null
    }

    fun saveVideosToExternalAppStorage(uri: Uri) : Uri?{
        val inputStream = resolver.openInputStream(uri)?:return null

        val storageDir = File(context.getExternalFilesDir(Environment.DIRECTORY_MOVIES), Constants.SAVED_VIDEOS_PATH)
        if (!storageDir.exists()) storageDir.mkdirs()

        val fileName = getFileName(uri)?: "VID_${System.currentTimeMillis()}.mp4"
        val videoFile = File(storageDir,fileName)

        runCatching {
            val fileOutputStream = FileOutputStream(videoFile)
            inputStream.copyTo(fileOutputStream)

            inputStream.close()
            fileOutputStream.close()
            return getFileUriExternalAppStorage(videoFile)
        }.onFailure {
            it.printStackTrace()
        }
        return null
    }

    fun saveDocumentToExternalAppStorage(uri: Uri):Uri?{
        val inputStream = resolver.openInputStream(uri)?: return null

        val storageDir = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), Constants.SAVED_DOCS_PATH)
        if (!storageDir.exists()) storageDir.mkdirs()

        val fileName = getFileName(uri)?: "DOC_${System.currentTimeMillis()}.pdf"
        val docFile = File(storageDir,fileName)

        runCatching {
            val fileOutputStream = FileOutputStream(docFile)
            inputStream.copyTo(fileOutputStream)

            inputStream.close()
            fileOutputStream.close()
            return getFileUriExternalAppStorage(docFile)
        }.onFailure {
            it.printStackTrace()
        }
        return null
    }

    fun saveAudioToExternalAppStorage(uri: Uri):Uri?{
        val inputStream = resolver.openInputStream(uri)?: return null

        val storageDir = File(context.getExternalFilesDir(Environment.DIRECTORY_MUSIC), Constants.SAVED_AUDIO_PATH)
        if (!storageDir.exists()) storageDir.mkdirs()

        val fileName = getFileName(uri)?: "DOC_${System.currentTimeMillis()}.pdf"
        val audioFile = File(storageDir,fileName)

        runCatching {
            val fileOutputStream = FileOutputStream(audioFile)
            inputStream.copyTo(fileOutputStream)

            inputStream.close()
            fileOutputStream.close()
            return getFileUriExternalAppStorage(audioFile)
        }.onFailure {
            it.printStackTrace()
        }
        return null
    }

    fun getFileName(uri : Uri) : String?{
        val queryCursor = resolver.query(uri,null,null,null,null)
        queryCursor?.use {
            val displayNameColumnIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (it.moveToFirst()){
                return it.getString(displayNameColumnIndex)
            }
        }
        return null
    }

    fun deleteFile(fileName : String,fileType : FileType){
        val (directory,subFolder) = when(fileType){
            FileType.Picture -> Pair(Environment.DIRECTORY_PICTURES,Constants.SAVED_PICTURES_PATH)
            FileType.Video -> Pair(Environment.DIRECTORY_MOVIES,Constants.SAVED_VIDEOS_PATH)
            FileType.Document -> Pair(Environment.DIRECTORY_DOCUMENTS,Constants.SAVED_DOCS_PATH)
            FileType.Audio -> Pair(Environment.DIRECTORY_MUSIC,Constants.SAVED_AUDIO_PATH)
            FileType.Undefined -> return
        }

        println("Reached Here directory and file name file name : $fileName $directory")
        val fileToDelete = File(context.getExternalFilesDir(directory),"$subFolder/$fileName")
        fileToDelete.delete().also {
            if (it){
                println("Deleted Successfully")
            }else println("Failed Deleting")

        }
    }

    private fun getFileUriExternalAppStorage(file : File):Uri{
        return FileProvider.getUriForFile(context,"${context.packageName}.provider",file)
    }
}