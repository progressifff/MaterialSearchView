package com.progressifff.materialsearchview

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.os.Parcel
import android.os.Parcelable
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.TypedValue.COMPLEX_UNIT_PX
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageButton

open class SearchView : FrameLayout, View.OnClickListener {

    protected lateinit var searchText: EditText
    protected lateinit var searchViewBar: ConstraintLayout
    protected lateinit var backBtn: ImageButton
    protected lateinit var clearBtn: ImageButton
    protected lateinit var overlay: View
    protected lateinit var suggestions: FrameLayout
    protected lateinit var suggestionsList: RecyclerView
    protected lateinit var suggestionsListAdapter: SuggestionsAdapter
    var searchViewListener: SearchViewListener? = null
    var searchMenuItemIndex: Int = 0
    var isOpened: Boolean = false
                            private set

    var isAlwaysVisible = false
    set(value) {
        field = value
        if(field){
            open(false)
        }
    }
    var suggestionsData = arrayListOf<String>()
    set(value) {
        field = value
        suggestionsListAdapter.suggestions = field
        suggestionsListAdapter.filter.filter(searchText.text)
        if(!isAlwaysVisible) {
            showSuggestions()
        }
    }

    private val circularAnimationCenter: Point by lazy {
        val x = width - ((searchMenuItemIndex + 1) * TOOLBAR_ACTION_BUTTON_WIDTH).toPx()
        val y = searchViewBar.height / 2
        return@lazy Point(x, y)
    }

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.searchViewStyle)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttrs: Int) : super(context, attrs, defStyleAttrs, R.style.SearchView_Dark){
        init()
        applyAttributes(attrs, defStyleAttrs)
    }

    private fun init(){
        LayoutInflater.from(context).inflate(R.layout.search_view_layout, this, true)
        visibility = View.INVISIBLE
        searchText = findViewById(R.id.searchText)
        backBtn = findViewById(R.id.backBtn)
        clearBtn = findViewById(R.id.clearBtn)
        searchViewBar = findViewById(R.id.searchViewBar)
        suggestions = findViewById(R.id.suggestions)
        overlay = findViewById(R.id.overlay)
        suggestionsList = findViewById(R.id.suggestionsList)

        suggestionsListAdapter = SuggestionsAdapter(suggestionsData, object : SuggestionsAdapter.SuggestionsListener{
            override fun onSelected(suggestion: String) {
                hide(false)
                searchViewListener?.onSuggestionSelected(suggestion)
                setQueryText(suggestion)
            }
        })

        suggestionsList.adapter = suggestionsListAdapter

        searchText.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                searchViewListener?.onQueryTextChange(s.toString())
                clearBtn.visibility = if(s.isNotEmpty()) VISIBLE else INVISIBLE
                suggestionsListAdapter.filter.filter(s)
            }
        })

        searchText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if(searchViewListener != null){
                    val submitted = searchViewListener!!.onQueryTextSubmit(searchText.text.toString())
                    if (submitted) {
                        hide(false)
                    }
                }
                true
            } else false
        }

        searchText.setOnFocusChangeListener { _, hasFocus ->
            if(hasFocus &&
                isAlwaysVisible && suggestionsData.isNotEmpty()
                && suggestions.visibility == View.INVISIBLE) { //If view is always visible show suggestions
                showSuggestions()
            }
        }

        searchText.setOnClickListener(this)
        overlay.setOnClickListener(this)
        backBtn.setOnClickListener(this)
        clearBtn.setOnClickListener(this)
    }

    private fun applyAttributes(attrs: AttributeSet?, defStyleAttrs: Int){

        val attributes = context.obtainStyledAttributes(attrs, R.styleable.SearchView, defStyleAttrs, R.style.SearchView_Dark)

        if (attributes != null) {
            if (attributes.hasValue(R.styleable.SearchView_isAlwaysVisible)) {
                isAlwaysVisible = attributes.getBoolean(R.styleable.SearchView_isAlwaysVisible, false)
            }
            if (attributes.hasValue(R.styleable.SearchView_query)) {
                val query = attributes.getString(R.styleable.SearchView_query)
                if(query != null) setQueryText(query)
            }
            if (attributes.hasValue(R.styleable.SearchView_hint)) {
                searchText.hint = attributes.getString(R.styleable.SearchView_hint)
            }
            if(attributes.hasValue(R.styleable.SearchView_hintColor)){
                val hintColor = attributes.getColor(R.styleable.SearchView_hintColor, Color.BLACK)
                searchText.setHintTextColor(hintColor)
            }
            if(attributes.hasValue(R.styleable.SearchView_textColor)){
                val textColor = attributes.getColor(R.styleable.SearchView_textColor, Color.BLACK)
                searchText.setTextColor(textColor)
                suggestionsListAdapter.suggestionTextColor = textColor
            }
            if(attributes.hasValue(R.styleable.SearchView_textSize)){
                val textSize = attributes.getDimension(R.styleable.SearchView_textSize, 32F)
                suggestionsListAdapter.suggestionTextSize = textSize
                searchText.setTextSize(COMPLEX_UNIT_PX, textSize)
            }
            if(attributes.hasValue(R.styleable.SearchView_backgroundColor)){
                val backgroundColor = attributes.getColor(R.styleable.SearchView_backgroundColor, Color.WHITE)
                searchViewBar.setBackgroundColor(backgroundColor)
                suggestionsList.setBackgroundColor(backgroundColor)
            }
            if(attributes.hasValue(R.styleable.SearchView_overlayColor)){
                val overlayColor = attributes.getColor(R.styleable.SearchView_overlayColor, Color.WHITE)
                overlay.setBackgroundColor(overlayColor)
            }
            if(attributes.hasValue(R.styleable.SearchView_backBtnIcon)){
                val backBtnImg = attributes.getResourceId(R.styleable.SearchView_backBtnIcon, -1)
                assert(backBtnImg > (-1))
                backBtn.setImageResource(backBtnImg)
            }
            if(attributes.hasValue(R.styleable.SearchView_clearBtnIcon)){
                val clearBtnImg = attributes.getResourceId(R.styleable.SearchView_clearBtnIcon, -1)
                assert(clearBtnImg > (-1))
                clearBtn.setImageResource(clearBtnImg)
            }
            if(attributes.hasValue(R.styleable.SearchView_recentIcon)){
                val recentIcon = attributes.getResourceId(R.styleable.SearchView_recentIcon, -1)
                assert(recentIcon > (-1))
                suggestionsListAdapter.suggestionImg = recentIcon
            }
            if(attributes.hasValue(R.styleable.SearchView_clickEffect)){
                val clickEffect = attributes.getResourceId(R.styleable.SearchView_clickEffect, -1)
                assert(clickEffect > (-1))
                suggestionsListAdapter.itemClickEffect = clickEffect
            }
            if(attributes.hasValue(R.styleable.SearchView_circleClickEffect)){
                val circleClickEffect = attributes.getResourceId(R.styleable.SearchView_circleClickEffect, -1)
                assert(circleClickEffect > (-1))
                clearBtn.setBackgroundResource(circleClickEffect)
                backBtn.setBackgroundResource(circleClickEffect)
            }
            attributes.recycle()
        }
    }

    fun setMenuItem(menuItem: MenuItem) {
        menuItem.setOnMenuItemClickListener {
            if(!isAlwaysVisible){
                clearQueryText()
                open(!isOpened)
                return@setOnMenuItemClickListener true
            }
            return@setOnMenuItemClickListener false
        }
    }

    fun open(animate: Boolean = true){
        if(isOpened) {
            return
        }

        visibility = View.VISIBLE

        val finalize: () -> Unit = {
            isOpened = true
            if(!isAlwaysVisible){
                searchText.showKeyboard()
            }
            searchViewListener?.onVisibilityChanged(true)
        }

        if(animate){
            val toolbarSearchAnimation = ViewAnimationUtils.createCircularReveal(this, circularAnimationCenter.x, circularAnimationCenter.y, 0.0f, circularAnimationCenter.x.toFloat())
            toolbarSearchAnimation.duration = ANIMATION_DURATION
            toolbarSearchAnimation.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(p0: Animator?) {
                    finalize()
                }
            })
            toolbarSearchAnimation.start()
        }
        else{
            finalize()
        }
    }

    fun close(animate: Boolean = true){
        if(!isOpened || isAlwaysVisible) {
            return
        }

        val finalize: () -> Unit = {
            isOpened = false
            visibility = View.INVISIBLE
            searchText.hideKeyboard()
            searchViewListener?.onVisibilityChanged(false)
        }

        if(animate){
            val createCircularReveal = ViewAnimationUtils.createCircularReveal(this, circularAnimationCenter.x, circularAnimationCenter.y, circularAnimationCenter.x.toFloat(), 0.0f)
            createCircularReveal.duration = ANIMATION_DURATION
            createCircularReveal.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    finalize()
                }
            })
            createCircularReveal.start()
        }
        else{
            finalize()
        }
    }

    fun showSuggestions(){
        suggestions.visibility = View.VISIBLE
        searchViewListener?.onSuggestionsVisibilityChanged(true)
    }

    fun hideSuggestions(){
        suggestions.visibility = View.INVISIBLE
        searchViewListener?.onSuggestionsVisibilityChanged(false)
    }

    fun setQueryText(query: String){
        searchText.setText(query)
        searchText.setSelection(searchText.text.length) //move cursor to end
    }

    fun getQueryText(): String{
        return searchText.text.toString()
    }

    fun clearQueryText(){
        searchText.text.clear()
    }

    fun onBackPressed(): Boolean{
        if((isAlwaysVisible && !suggestions.visible) || !isOpened){
            return false
        }
        hide() //Process back pressed
        return true
    }

    override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()!!
        val state = SavedState(superState)
        state.isOpened = isOpened
        state.isSuggestionsVisible = suggestions.visible
        state.suggestionsListState = suggestionsList.layoutManager!!.onSaveInstanceState()
        state.suggestions = suggestionsData
        return state
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        super.onRestoreInstanceState(state)
        if(state is SavedState){
            if(state.isOpened && !isAlwaysVisible) {
                open(false)
            }
            if(state.isSuggestionsVisible){
                showSuggestions()
                suggestionsList.layoutManager!!.onRestoreInstanceState(state.suggestionsListState)
            }
            suggestionsData = state.suggestions
        }
    }

    private fun hide(animate: Boolean = true) {

        if(isAlwaysVisible){
            if(suggestions.visible){
                hideSuggestions()
            }
            searchText.hideKeyboard()
        }
        else{
            close(animate)
        }
        searchText.clearFocus()

    }

    override fun onClick(v: View) {
        when(v.id){
            R.id.backBtn -> {
                if(!isAlwaysVisible || (isAlwaysVisible && !suggestions.visible)){
                    searchViewListener?.onBackButtonPressed()
                }
                hide()
            }

            R.id.clearBtn -> {
                clearQueryText()
                searchText.resetFocus()
                searchText.showKeyboard()
            }

            R.id.overlay -> hide()

            R.id.searchText -> {
                searchText.resetFocus()
            }
        }
    }

    class SavedState : View.BaseSavedState {
        var isOpened = false
        var isSuggestionsVisible = false
        lateinit var suggestions: ArrayList<String>
        var suggestionsListState: Parcelable? = null

        constructor(superState: Parcelable) : super(superState)

        @SuppressLint("ParcelClassLoader")
        constructor(source: Parcel) : super(source) {
            isOpened = source.readByte() == 1.toByte()
            isSuggestionsVisible = source.readByte() == 1.toByte()
            suggestionsListState = source.readParcelable(null)
            source.readStringList(suggestions)
        }

        override fun writeToParcel(out: Parcel?, flags: Int) {
            super.writeToParcel(out, flags)
            if(isOpened){ out?.writeByte(1) } else out?.writeByte(0)
            if(isSuggestionsVisible){ out?.writeByte(1) } else out?.writeByte(0)
            out?.writeParcelable(suggestionsListState, 0)
            out?.writeStringList(suggestions)
        }

        companion object CREATOR : Parcelable.Creator<SavedState> {
            override fun createFromParcel(parcel: Parcel): SavedState {
                return SavedState(parcel)
            }

            override fun newArray(size: Int): Array<SavedState?> {
                return arrayOfNulls(size)
            }
        }
    }

    companion object {
        const val ANIMATION_DURATION = 250L
        private const val TOOLBAR_ACTION_BUTTON_WIDTH = 32 //dp
    }

    interface SearchViewListener{
        fun onBackButtonPressed() {}
        fun onVisibilityChanged(visible: Boolean)
        fun onSuggestionsVisibilityChanged(visible: Boolean) {}
        fun onQueryTextChange(query: String) {}
        fun onQueryTextSubmit(query: String): Boolean
        fun onSuggestionSelected(suggestion: String)
    }
}