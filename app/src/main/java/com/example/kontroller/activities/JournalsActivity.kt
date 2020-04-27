package com.example.kontroller.activities

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AlertDialog
import android.util.Log
import com.example.kontroller.*
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

class JournalsActivity : AppCompatActivity() {
    private lateinit var dbAdapter: DbAdapter
    private lateinit var context: Context
    private val LOGIN_ACTIVITY_CODE: Int = 0
    private val REPORT_ACTIVITY_CODE: Int = 1
    private var currentJournal: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_journals)
        context = this.applicationContext
        dbAdapter = DbAdapter.getInstance(context)

        Authenticate()
    }

    fun Authenticate(){
        val filename = getString(R.string.local_storage)
        File(context.filesDir, filename).delete()

        if (File(context.filesDir, filename).exists()){

            val fileInputStream = openFileInput(filename)
            val inputStreamReader: InputStreamReader = InputStreamReader(fileInputStream)
            val bufferedReader: BufferedReader = BufferedReader(inputStreamReader)
            val text: String? = bufferedReader.readLine()

            if ( text != null){
                val regex = Regex("[a-z]+: ([0-9a-zA-Z]+)")
                val prefs = regex.findAll(text)

                if (prefs.count() != 2) {
                    callLoginActivity()
                } else {

                    val username = prefs.elementAt(0).groupValues[1]
                    val password = prefs.elementAt(1).groupValues[1]
                    dbAdapter.Login(username, password, ::errorHandler)

                    Handler().postDelayed({
                        if (!dbAdapter.isAuthorized()) {
                            callLoginActivity()
                        } else {
                            val journalsList = JournalsFragment()
                            supportFragmentManager.beginTransaction()
                                .add(R.id.journals_container, journalsList).commit()
                        }
                    }, 1000)
                }
            }
        } else {
            callLoginActivity()
        }
    }

    fun callLoginActivity(){
        val intent = Intent(this, LoginActivity::class.java)
        startActivityForResult(intent, LOGIN_ACTIVITY_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            LOGIN_ACTIVITY_CODE -> {
                val journalsList = JournalsFragment()
                supportFragmentManager.beginTransaction()
                    .add(R.id.journals_container, journalsList)
                    .commitAllowingStateLoss()
            }
            REPORT_ACTIVITY_CODE -> {
                showObjects()
            }

            else -> errorHandler("")

        }
    }

    fun errorHandler(error:String){
        ErrorHandler.errorParser(error, this)
    }

    fun showObjects(journal: String = ""){
        if (journal != "") currentJournal = journal

        val objectsList = ObjectsFragment()
        val bundle = Bundle()

        bundle.putString("journal_id", currentJournal)
        objectsList.arguments = bundle
        supportFragmentManager.beginTransaction().replace(R.id.objects_container, objectsList).commitAllowingStateLoss()
    }

    fun makeReport(report: String){
        val intent = Intent(this, ReportActivity::class.java)
        intent.putExtra("report", report)
        startActivityForResult(intent, REPORT_ACTIVITY_CODE)
    }
}
