package com.progressifff.materialsearchview

import android.support.annotation.ColorInt
import android.support.annotation.Dimension
import android.support.annotation.DrawableRes
import android.support.v7.widget.RecyclerView
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView

class SuggestionsAdapter(suggestionsData: ArrayList<String>, var suggestionsListener: SuggestionsListener? = null) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {

    var suggestions = suggestionsData
    set(value) {
        field = value
        currentSuggestions = field
    }

    private var currentSuggestions = suggestions
    set(value) {
        field = value
        notifyDataSetChanged()
    }

    private val suggestionsFilter = object: Filter() {

        @Suppress("UNCHECKED_CAST")
        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            val result = results!!.values as ArrayList<String>
            currentSuggestions = if(result.isEmpty() && constraint.isNullOrEmpty()){
                suggestions
            } else{
                result
            }
        }

        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filterResults = FilterResults()

            if(constraint != null){
                if(constraint.isNullOrEmpty()){
                    filterResults.values = suggestions
                }
                else{
                    val suitable = arrayListOf<String>()
                    for(suggestion in suggestions){
                        if(suggestion.startsWith(constraint, true) ||
                                suggestion.contains(constraint, true)){
                            suitable.add(suggestion)
                        }
                    }
                    filterResults.values = suitable
                }
            }

            return filterResults
        }
    }

    @Dimension var suggestionTextSize: Float? = null
    @ColorInt var suggestionTextColor: Int? = null
    @DrawableRes var suggestionImg: Int? = null
    @DrawableRes var itemClickEffect: Int? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.search_suggestion_row, parent, false)
        return SuggestionViewHolder(view)
    }

    override fun getItemCount(): Int = currentSuggestions.size

    override fun onBindViewHolder(vh: RecyclerView.ViewHolder, index: Int) {
        val suggestion = currentSuggestions[index]
        val suggestionViewHolder = vh as SuggestionViewHolder
        suggestionViewHolder.suggestionTextView.text = suggestion

        if(suggestionTextSize != null){
            suggestionViewHolder.suggestionTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, suggestionTextSize!!)
        }

        if(suggestionTextColor != null){
            suggestionViewHolder.suggestionTextView.setTextColor(suggestionTextColor!!)
        }

        if(suggestionImg != null){
            suggestionViewHolder.suggestionImg.setImageResource(suggestionImg!!)
        }

        if(itemClickEffect != null){
            vh.itemView.setBackgroundResource(itemClickEffect!!)
        }
    }

    override fun getFilter(): Filter {
        return suggestionsFilter
    }

    inner class SuggestionViewHolder(v: View) : RecyclerView.ViewHolder(v){
        val suggestionTextView = v.findViewById<TextView>(R.id.suggestionText)!!
        val suggestionImg = v.findViewById<ImageView>(R.id.suggestionImg)!!
        init {
            v.setOnClickListener {
                suggestionsListener?.onSelected(currentSuggestions[adapterPosition])
            }
        }
    }

    interface SuggestionsListener{
        fun onSelected(suggestion: String)
    }
}