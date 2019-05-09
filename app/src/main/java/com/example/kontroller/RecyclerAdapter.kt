package com.example.kontroller

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class RecyclerAdapter(private val values: List<String>):
    RecyclerView.Adapter<RecyclerAdapter.ItemHolder>(){

    private lateinit var onItemClickListener: View.OnClickListener

    override fun getItemCount() = values.size

    override fun onCreateViewHolder(parent: ViewGroup,
                                    position: Int): RecyclerAdapter.ItemHolder {
        val item = LayoutInflater.from(parent.context).
            inflate(R.layout.item_journal, parent, false)
        return ItemHolder(item)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int){
        holder.textView?.text = values[position]
        holder.pos = position
    }

    public fun setOnItemClickListener(itemClickListener: View.OnClickListener){
        onItemClickListener = itemClickListener
    }


    inner class ItemHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var textView: TextView? = null
        var pos = 0
        init{
            textView = itemView.findViewById(R.id.journal_item)

            itemView.tag = this
            itemView.setOnClickListener(onItemClickListener)
        }
    }
}