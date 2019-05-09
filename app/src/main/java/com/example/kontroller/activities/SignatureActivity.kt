package com.example.kontroller.activities

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.example.kontroller.R
import com.github.gcacace.signaturepad.views.SignaturePad
import java.io.ByteArrayOutputStream

class SignatureActivity : AppCompatActivity() {
    private lateinit var signPad: SignaturePad

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signature)

        signPad = findViewById(R.id.signaturePad)

        findViewById<Button>(R.id.saveButton).setOnClickListener {
            val bitmap = signPad.signatureBitmap
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            val byteArray = stream.toByteArray()

            val intent = Intent()
            intent.putExtra("sign", byteArray)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }

        findViewById<Button>(R.id.clearButton).setOnClickListener {
            signPad.clear()
        }

    }
}
