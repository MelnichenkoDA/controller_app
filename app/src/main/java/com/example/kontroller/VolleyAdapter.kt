package com.example.kontroller

import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

class VolleyAdapter(context: Context) {
    private var requestQueue = Volley.newRequestQueue(context)



    fun <T> addToRequestQueue(req: Request<T>) {
        requestQueue.add(req)
    }


}