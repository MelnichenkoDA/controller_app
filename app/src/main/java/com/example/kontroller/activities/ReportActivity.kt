package com.example.kontroller.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import com.example.kontroller.DbAdapter
import com.example.kontroller.R
import org.json.JSONArray
import org.json.JSONObject

class ReportActivity : AppCompatActivity() {
    private lateinit var dataList: MutableList<EditText>
    private lateinit var report: JSONObject
    private lateinit var signView: ImageView
    private lateinit var signByteArray: ByteArray

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)

        dataList = mutableListOf()
        val bundle = intent.extras
        DbAdapter.getInstance(applicationContext).
            getObjectById(bundle.getString("report"), ::makeForm)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1 && resultCode == Activity.RESULT_OK){
            try {
                signByteArray = data?.getByteArrayExtra("sign")!!
                val bitmap = BitmapFactory.decodeByteArray(signByteArray, 0, signByteArray.size)
                signView.setImageBitmap(bitmap)
            } catch (e: Exception){
                e.printStackTrace()
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Error message!")
                    .setMessage(getString(R.string.bitmap_error))
                    .setPositiveButton("Ok"){ dialog, _ ->
                        finish()
                    }
                val dialog = builder.create()
                dialog.show()
            }
        }
    }

    fun makeForm(form: JSONObject){
        report = form
        val parentLayout = findViewById<LinearLayout>(R.id.report_container)

        val layoutInflater = layoutInflater
        var view: View

        val fields = form.getJSONArray("fields")

        for (i in 0 until fields.length() - 1){
            view = layoutInflater.inflate(R.layout.text_layout, parentLayout, false)
            val textInput = view.findViewById<EditText>(R.id.report_field)

            val title = fields.getJSONObject(i).getString("name")
            textInput.hint = "Enter the $title"
            textInput.inputType = getType(fields.getJSONObject(i).getString("type"))
            dataList.add(textInput)
            parentLayout.addView(textInput)
        }

        view = layoutInflater.inflate(R.layout.signature_container, parentLayout, false)
        view.findViewById<Button>(R.id.sign_button).setOnClickListener {
            val intent = Intent(this, SignatureActivity::class.java)
            startActivityForResult(intent, 1)
        }
        parentLayout.addView(view)

        signView = findViewById(R.id.image_container)
        signView.layoutParams.height = 500
        signView.layoutParams.width = 1200
        signView.setBackgroundColor(Color.WHITE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            signView.background = getDrawable(R.drawable.back_sign)
        }
        view = layoutInflater.inflate(R.layout.button_layout, parentLayout, false)
        val button = view.findViewById<Button>(R.id.report_submit)
        button.setOnClickListener{
            if (checkInput()) {
                DbAdapter.getInstance(applicationContext).sendReport(makeJson())
                finish()
            }
        }

        parentLayout.addView(button)
    }

    fun getType(type: String):Int {
        return when (type) {
            "Int" -> 2          //number
            "Date" -> 20        //date
            "Double" -> 8194    //decimalNumber
            else -> 1           //text
        }
    }

    fun makeJson(): JSONObject{
        val jsonObject = JSONObject()

        jsonObject.put("journal", report.getString("journal"))
        jsonObject.put("name", report.getString("name"))
        jsonObject.put("title", report.getString("title"))
        jsonObject.put("journal_id", report.getString("journal_id"))

        val tempFields = JSONArray()
        val fields = report.getJSONArray("fields")
        for (i  in 0 until fields.length() - 1){
            val temp = JSONObject()

            temp.put("name", fields.getJSONObject(i).getString("name"))
            temp.put("title", fields.getJSONObject(i).getString("title"))
            temp.put("type", fields.getJSONObject(i).getString("type"))
            temp.put("value", dataList[i].text)

            tempFields.put(temp)
        }

        jsonObject.put("fields", tempFields)
        return jsonObject
    }

    fun checkInput(): Boolean{
        var result = true

        for (i in 0 until dataList.size){
            //Not empty
            if (dataList[i].text.isEmpty()) {
                dataList[i].error = getString(R.string.error_empty_field)
                result = false
            } else {
                //Incorrect input
                if (dataList[i].inputType == 1){
                    val regex = Regex("^[0-9a-zA-Z]+$")
                    if (!regex.matches(dataList[i].text)){
                        result = false
                        dataList[i].error = getString(R.string.error_incorrect_input)
                    }
                }
            }
        }

        return result
    }

}
