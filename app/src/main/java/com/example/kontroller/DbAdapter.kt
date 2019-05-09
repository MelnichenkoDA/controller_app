package com.example.kontroller

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import co.metalab.asyncawait.async
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONObject
import java.lang.Exception
import kotlin.math.log

class DbAdapter constructor(context: Context){

    private var token: String = ""      //currently is _id field
    private var bAuth = false
    private val volley = VolleyAdapter(context)

    companion object {
        @Volatile
        private var INSTANCE: DbAdapter? = null

        fun getInstance(context: Context) =
            INSTANCE ?: DbAdapter(context).also {
                    INSTANCE = it
                }
    }

    internal fun Login(username: String, password: String,
                       errorCallback: (error:String) -> Unit,
                       loginDialog: AlertDialog? = null,
                       callback: (dialog: AlertDialog) -> Unit = {}) {
        val url: String = "https://rep.moeka.host/api/v1/controller"

        val jsonRequest = JsonArrayRequest(Request.Method.GET, url, null,
            Response.Listener { response ->
                try {
                    for (i in 0 until response.length()){
                        var jsonObject  = response.getJSONObject(i)
                        if (username == jsonObject.getString("first_name")){
                            bAuth = true
                            token = jsonObject.getString("ID")
                            break
                        }
                    }
                    if (loginDialog != null)
                        callback(loginDialog)
                } catch (e: Exception){
                    errorCallback("")
                }
            },
            Response.ErrorListener { error ->
                if (loginDialog != null)
                    callback(loginDialog)
                errorCallback(error.toString())
            }
        )

        volley.addToRequestQueue(jsonRequest)
    }

    internal fun initJournalsList(objectsList: MutableList<List<String>>,
                                  errorCallback: (error: String) -> kotlin.Unit,
                                  callback:() -> Unit){
        val url = "https://rep.moeka.host/api/v1/scheme/item"

        val jsonRequest = JsonArrayRequest(Request.Method.GET, url, null,
            Response.Listener { response ->
                val titlesList = mutableListOf<String>()
                val idList = mutableListOf<String>()
                var errorFlag = false

                for (i in 0 until response.length()){
                    try{
                        val jsonObject = response.getJSONObject(i)
                        titlesList.add(jsonObject.getString("title"))
                        idList.add(jsonObject.getString("_id"))
                    } catch (e: Exception){
                        errorFlag = true
                        continue
                    }
                }

                if (errorFlag) errorCallback("")

                objectsList.add(titlesList)
                objectsList.add(idList)
                callback()
            },
            Response.ErrorListener { error ->
                errorCallback(error.toString())
            }
        )
        volley.addToRequestQueue(jsonRequest)
    }

    internal fun initObjectsList(journal: String, objectsList: MutableList<List<String>>,
                                 errorCallback: (error: String) -> Unit,
                                 callback: () -> Unit){
        val url = "https://rep.moeka.host/api/v1/journal"

        val jsonRequest = JsonArrayRequest(Request.Method.GET, url, null,
            Response.Listener { response ->
                val titlesList = mutableListOf<String>()
                val idList = mutableListOf<String>()
                var errorFlag = false
                for (i in 0 until response.length()){
                    val jsonObject = response.getJSONObject(i)
                    try {
                        if (jsonObject.getString("journal_id") == journal){
                            titlesList.add(jsonObject.getString("title"))
                            idList.add(jsonObject.getString("_id"))
                        }
                    } catch (e: Exception){
                        errorFlag = true
                        continue
                    }
                }
                if (errorFlag) errorCallback("")

                objectsList.add(titlesList)
                objectsList.add(idList)
                callback()
            },
            Response.ErrorListener { error ->
                errorCallback(error.toString())
            }
        )
        volley.addToRequestQueue(jsonRequest)
    }

    internal fun getObjectById(id: String, callback: (json: JSONObject) -> Unit){
        val url = "https://rep.moeka.host/api/v1/journal/$id"

        val request = JsonObjectRequest(Request.Method.GET, url, null,
            Response.Listener { response ->
                callback(response)
            },
            Response.ErrorListener { error ->
                Log.d("ERROR", error.networkResponse.statusCode.toString())
            }
        )

        volley.addToRequestQueue(request)
    }

    internal fun sendReport(jsonObject: JSONObject){
        val url = "https://rep.moeka.host/api/v1/journal"

        val request = JsonObjectRequest(Request.Method.POST, url, jsonObject,
            Response.Listener { response ->  Log.d("REPORT:", response.toString())},
            Response.ErrorListener { error -> Log.d("REPORT:", error.message) }
        )

        volley.addToRequestQueue(request)
    }


    internal fun isAuthorized() = bAuth
}