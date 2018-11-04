package com.progressifff.materialsearchview.sample

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Menu
import com.progressifff.materialsearchview.SearchView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var searchView: com.progressifff.materialsearchview.SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))
        findViewById<RecyclerView>(R.id.simpleList).adapter = SimpleListAdapter(ArrayList(resources.getStringArray(R.array.simple_list_values).asList()))
        searchView = findViewById(R.id.searchView)
        setSearchSuggestions()
    }

    override fun onBackPressed() {
        if(!searchView.onBackPressed()){
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        val item = menu.findItem(R.id.action_search)
        searchView.setMenuItem(item)
        searchView.searchMenuItemIndex = 0

        searchView.searchViewListener = object : SearchView.SearchViewListener{

            override fun onVisibilityChanged(visible: Boolean) {
                Log.v(MainActivity::javaClass.name, "onVisibilityChanged, visible $visible")
                if(visible){
                    setSearchSuggestions()
                }
            }

            override fun onQueryTextChange(query: String) {
                Log.v(MainActivity::javaClass.name, "onQueryTextChange: $query")
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                Log.v(MainActivity::javaClass.name, "onQueryTextSubmit: $query")
                if(query.isEmpty()) {
                    return false
                }
                startSearchableActivity(query)
                return true
            }

            override fun onSuggestionSelected(suggestion: String) {
                Log.v(MainActivity::javaClass.name, "onSuggestionSelected $suggestion")
                startSearchableActivity(suggestion)
            }
        }
        return true
    }

    private fun setSearchSuggestions(){
        GlobalScope.launch {
            val suggestionDao = SearchSuggestions.instance.suggestionDao()
            val suggestions = ArrayList(suggestionDao.getAll())
            launch(Dispatchers.Main){ searchView.suggestionsData = suggestions }
        }
    }

    private fun startSearchableActivity(query: String){
        val startActivityIntent = Intent(this@MainActivity, SearchableActivity::class.java)
        startActivityIntent.putExtra(SearchableActivity.QUERY_KEY, query)
        startActivity(startActivityIntent)
    }
}
