package com.progressifff.materialsearchview

import android.content.Context
import android.content.res.Resources
import android.view.View
import android.view.inputmethod.InputMethodManager

fun View.showKeyboard(){
    this.requestFocus()
    val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.showSoftInput(this, 0)
}

fun View.hideKeyboard(){
    val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(this.windowToken, 0)
}

fun View.resetFocus(){
    this.clearFocus()
    this.requestFocus()
}

val View.visible get() = this.visibility == View.VISIBLE

fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()