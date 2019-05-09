package com.example.kontroller.activities

import android.app.AlertDialog
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import com.example.kontroller.DbAdapter
import com.example.kontroller.ErrorHandler
import com.example.kontroller.R
import java.io.FileNotFoundException
import java.lang.Exception

class LoginActivity : AppCompatActivity() {

    private lateinit var dbAdapter: DbAdapter
    private lateinit var loginText: EditText
    private lateinit var passwordText: EditText
    private lateinit var checkBox: CheckBox
    private lateinit var loginButton: Button

    private var test:String = String()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        dbAdapter = DbAdapter.getInstance(this)

        loginText = findViewById(R.id.input_login)
        passwordText = findViewById(R.id.input_password)
        checkBox = findViewById(R.id.checkbox_password)
        loginButton = findViewById(R.id.btn_login)

        checkBox.setOnClickListener {
            if (checkBox.isChecked){
                passwordText.transformationMethod = HideReturnsTransformationMethod.getInstance()
            } else {
                passwordText.transformationMethod = PasswordTransformationMethod.getInstance()
            }
        }

        loginButton.setOnClickListener {
            if (checkInput()) login()
        }
    }

    private fun login(){
        val username = loginText.text.toString()
        val password = passwordText.text.toString()
        val builder = AlertDialog.Builder(this)
        builder.setView(layoutInflater.inflate(R.layout.progress_dialog, null))
        builder.setCancelable(false)

        val dialog = builder.create()
        dialog.show()

        //TODO
        dbAdapter.Login(username, password, ::errorHandler, dialog, ::waitLogin)

        Handler().postDelayed({
            if (dialog.isShowing) {
                errorHandler("503")
            }
        }, 5000)
    }

    private fun waitLogin(dialog: AlertDialog){
        dialog.dismiss()

        val username = loginText.text.toString()
        val password = passwordText.text.toString()

        if (dbAdapter.isAuthorized()) {
            try {
                val fileOutputStream =
                    openFileOutput(getString(R.string.local_storage), Context.MODE_PRIVATE)
                fileOutputStream.write(("username: $username" +
                        ", password: $password").toByteArray())
                finish()
            } catch (e: FileNotFoundException){
                e.printStackTrace()
            } catch (e: Exception){
                e.printStackTrace()
            }
        } else {
            loginText.setText("")
            loginText.error = getString(R.string.wrong_login)

            passwordText.setText("")
            passwordText.error = getString(R.string.wrong_password)
        }
    }

    private fun errorHandler(error: String){
        ErrorHandler.errorParser(error, this)
    }

    //
    private fun checkInput():Boolean{
        var result = true
        val regex = Regex("^[0-9a-zA-Z]+$")

        if (loginText.text.isEmpty()){
            result = false
            loginText.error = getString(R.string.error_empty_login)
        } else if (!regex.matches(loginText.text.toString())){
            result = false
            loginText.error = getString(R.string.error_incorrect_input)
        }

        if (passwordText.text.isEmpty()){
            result = false
            passwordText.error = getString(R.string.error_empty_password)
        } else if (!regex.matches(passwordText.text)){
            result = false
            passwordText.error = getString(R.string.error_incorrect_input)
        }

        return result
    }
}
