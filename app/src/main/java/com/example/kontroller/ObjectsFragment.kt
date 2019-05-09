package com.example.kontroller

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.kontroller.activities.JournalsActivity

class ObjectsFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerAdapter: RecyclerAdapter
    private var ID: String = ""

    private lateinit var objectsList: MutableList<List<String>>

    private val onItemClickListener = View.OnClickListener() { view ->
        val viewHolder = view.tag as RecyclerAdapter.ItemHolder
        (activity as JournalsActivity).makeReport(objectsList[1][viewHolder.pos])
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.objects_list, container, false)

        if (this.arguments?.getString("journal_id") != null)
            ID = this.arguments?.getString("journal_id").toString()

        objectsList = mutableListOf<List<String>>()
        DbAdapter.getInstance(activity!!.applicationContext).
            initObjectsList(ID, objectsList, ::errorHandler, ::setObjectsList)

        return view
    }

    private fun errorHandler(error:String){
        ErrorHandler.errorParser(error, this.context!!)
    }

    private fun setObjectsList(){
        recyclerAdapter = RecyclerAdapter(objectsList[0])
        recyclerAdapter.setOnItemClickListener(onItemClickListener)
        recyclerView = this.view?.findViewById(R.id.objects_list_recycler)!!
        recyclerView.layoutManager = LinearLayoutManager(activity?.applicationContext)
        recyclerView.adapter = recyclerAdapter
    }

    fun onButtonPressed(uri: Uri) {

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

    }

    override fun onDetach() {
        super.onDetach()

    }

    interface OnFragmentInteractionListener {

    }


}
