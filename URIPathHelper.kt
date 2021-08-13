package com.example.apadandroidapp

import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns
import android.view.View
import com.google.android.material.snackbar.Snackbar

fun View.snackbar(message: String){
    Snackbar.make(
        this,
        message,
        Snackbar.LENGTH_LONG
    ).also { snackbar ->
        snackbar.setAction("Image Uploaded Successfully!"){
            snackbar.dismiss()
        }.show()
    }

}

fun ContentResolver.getFileName(uri: Uri): String{
    var name = ""
    val returnCursor = query(uri, null, null, null,null)
    if (returnCursor != null) {
        val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        returnCursor.moveToFirst()
        name = returnCursor.getString(nameIndex)
        returnCursor.close()
    }
    return name
}