package com.progressifff.materialsearchview.sample

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class SimpleListAdapter(data: ArrayList<String>) : RecyclerView.Adapter<SimpleListAdapter.SimpleViewHolder>(){

    var data = arrayListOf<String>()
    set(value) {
        field = value
        notifyDataSetChanged()
    }

    init{
        this.data = data
    }

    override fun onCreateViewHolder(vg: ViewGroup, p1: Int): SimpleViewHolder {
        val v = LayoutInflater.from(vg.context).inflate(R.layout.simple_list_item, vg, false)
        return SimpleViewHolder(v)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(vh: SimpleViewHolder, index: Int) {
        vh.textView.text = data[index]
    }

    inner class SimpleViewHolder(v: View) : RecyclerView.ViewHolder(v){
        val textView = v as TextView
    }
}