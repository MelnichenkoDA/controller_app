package com.example.kontroller

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.kontroller.activities.JournalsActivity

class JournalsFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerAdapter: RecyclerAdapter
    private lateinit var journalsList: MutableList<List<String>>

    private val onItemClickListener = View.OnClickListener() { view ->
        val viewHolder = view.tag as RecyclerAdapter.ItemHolder
        (activity as JournalsActivity).showObjects(journalsList[1][viewHolder.pos])
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("JournalsFragment:", "ASDASDASDASDADAD")
        val view = inflater.inflate(R.layout.journals_list, container, false)

        journalsList = mutableListOf()
        DbAdapter.getInstance(activity!!.applicationContext).initJournalsList(journalsList, ::errorHandler, ::setJournals)

        return view
    }

    private fun errorHandler(error: String){
        ErrorHandler.errorParser(error, this.context!!)
    }

    fun setJournals() {
        recyclerAdapter = RecyclerAdapter(journalsList[0])
        recyclerAdapter.setOnItemClickListener(onItemClickListener)

        recyclerView = this.view?.findViewById<RecyclerView>(R.id.journals_list_recycler)!!
        recyclerView.layoutManager = LinearLayoutManager(activity?.applicationContext)
        recyclerView.adapter = recyclerAdapter
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
