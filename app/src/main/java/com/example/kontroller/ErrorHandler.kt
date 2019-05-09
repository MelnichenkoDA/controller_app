package com.example.kontroller

import android.app.AlertDialog
import android.content.Context

class ErrorHandler(){
    companion object {
        private  fun errorDialog(error:String, context: Context){
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Error message!")
                .setMessage("Error code $error")
                .setPositiveButton("Ok"){ dialog, _ ->
                    dialog.cancel()
                }
            val dialog = builder.create()
            dialog.show()
        }

        internal fun errorParser(error: String, context: Context){
            when(error){
                "com.android.volley.ClientError" ->
                    errorDialog("502", context)
                "404" -> errorDialog("404", context)
                "500" -> errorDialog("500", context)
                "501" -> errorDialog("501. May be you are offline?", context)
                else ->
                    errorDialog("503", context)
            }
        }
    }
}



