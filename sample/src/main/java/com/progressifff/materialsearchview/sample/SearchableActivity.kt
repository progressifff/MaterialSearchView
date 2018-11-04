package com.progressifff.materialsearchview.sample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.TextView
import com.progressifff.materialsearchview.SearchView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SearchableActivity : AppCompatActivity() {

    private lateinit var searchView: com.progressifff.materialsearchview.SearchView
    private lateinit var data: ArrayList<String>
    private lateinit var searchedEntriesListAdapter: SimpleListAdapter
    private lateinit var notFoundResult: TextView
    private lateinit var searchedEntriesList: RecyclerView
    private val searchViewListener = object : SearchView.SearchViewListener{
        override fun onBackButtonPressed() { finish() }

        override fun onVisibilityChanged(visible: Boolean) {
            Log.v(SearchableActivity::javaClass.name, "onVisibilityChanged, visible $visible")
        }

        override fun onSuggestionsVisibilityChanged(visible: Boolean) {
            Log.v(SearchableActivity::javaClass.name, "onSuggestionsVisibilityChanged, visible $visible")
            if(visible){
                updateSuggestions()
            }
        }

        override fun onQueryTextChange(query: String) {
            Log.v(SearchableActivity::javaClass.name, "onQueryTextChange: $query")
        }

        override fun onQueryTextSubmit(query: String): Boolean {
            Log.v(SearchableActivity::javaClass.name, "onQueryTextSubmit: $query")
            if(query.isEmpty()) {
                return false
            }
            val result = find(query)
            if(result.isEmpty()){
                notFoundResult.visibility = View.VISIBLE
                searchedEntriesList.visibility = View.INVISIBLE
            }
            else{
                notFoundResult.visibility = View.INVISIBLE
                searchedEntriesList.visibility = View.VISIBLE
                searchedEntriesListAdapter.data = result
            }
            saveSuggestion(query)
            return true
        }

        override fun onSuggestionSelected(suggestion: String) {
            Log.v(SearchableActivity::javaClass.name, "onSuggestionSelected: $suggestion")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.serchable_activity)
        data = ArrayList(resources.getStringArray(R.array.simple_list_values).asList())
        searchView = findViewById(R.id.searchView)
        val query = intent.getStringExtra(QUERY_KEY)
        searchView.setQueryText(query)
        searchView.searchViewListener = searchViewListener

        saveSuggestion(query)
        updateSuggestions()

        notFoundResult = findViewById(R.id.notFoundResult)
        searchedEntriesListAdapter = SimpleListAdapter(find(query))
        searchedEntriesList = findViewById(R.id.searchedEntriesList)
        searchedEntriesList.adapter = searchedEntriesListAdapter
        searchedEntriesListAdapter.notifyDataSetChanged()
    }

    private fun find(query: String): ArrayList<String>{
        val result = ArrayList<String>()
        for(entry in data){
            if(entry.contains(query, true)){
                result.add(entry)
            }
        }
        return result
    }

    private fun saveSuggestion(suggestion: String){
        GlobalScope.launch {
            val suggestionDao = SearchSuggestions.instance.suggestionDao()
            suggestionDao.insert(Suggestion(suggestion))
            suggestionDao.truncateHistory()
        }
    }

    private fun updateSuggestions(){
        GlobalScope.launch {
            val suggestions = ArrayList(SearchSuggestions.instance.suggestionDao().getAll())
            launch(Dispatchers.Main) { searchView.suggestionsData = suggestions }
        }
    }

    override fun onBackPressed() {
        if(!searchView.onBackPressed()){
            super.onBackPressed()
        }
    }

    companion object {
        const val QUERY_KEY = "QueryKey"
    }
}