# MaterialSearchView

Custom SearchView in a Material Design Approach. Works from Android API 21 and above.

![Alt text](https://github.com/progressifff/MaterialSearchView/blob/master/screenshots/2.jpg "Optional title")
![Alt text](https://github.com/progressifff/MaterialSearchView/blob/master/screenshots/3.jpg "Optional title")
![Alt text](https://github.com/progressifff/MaterialSearchView/blob/master/screenshots/4.jpg "Optional title")


# Usage
**Add the dependencies to your gradle file:**
```
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
dependencies {
    implementation 'com.github.progressifff:MaterialSearchView:1.0.1'
}
```
**Add MaterialSearchView to your layout. Root layout should be the FrameLayout and MaterialSearchView should be placed at the bottom of it.**
```
<FrameLayout
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:custom="http://schemas.android.com/apk/res-auto"
    >

    <android.support.design.widget.CoordinatorLayout
        xmlns:android="http://schemas.android.com/apk/res/android"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        xmlns:app="http://schemas.android.com/apk/res-auto">

        <android.support.v7.widget.Toolbar
            android:id="@+id/toolbar"
            android:layout_width="match_parent"
            android:layout_height="?attr/actionBarSize"
            android:background="?attr/colorPrimary"
            />

        <!--Content-->

    </android.support.design.widget.CoordinatorLayout>

    <com.progressifff.materialsearchview.SearchView
        android:id="@+id/searchView"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        custom:hint="@string/search_hint"
        />

</FrameLayout>
```
**Create a menu resource file and add the search item into the menu file:**
```
<?xml version="1.0" encoding="utf-8"?>
<menu xmlns:android="http://schemas.android.com/apk/res/android"
      xmlns:app="http://schemas.android.com/apk/res-auto">

    <item
        android:id="@+id/action_search"
        android:title="@string/search_title"
        android:icon="@drawable/ic_search_white"
        app:showAsAction="always"
        />
</menu>
```
**Set search MenuItem and it`s index (toolbar action button order number from right side) to MaterialSearchView. Index is needed for animation.**
```
override fun onCreateOptionsMenu(menu: Menu): Boolean {
    menuInflater.inflate(R.menu.main_menu, menu)
    val item = menu.findItem(R.id.action_search)
    searchView.setMenuItem(item)
    searchView.searchMenuItemIndex = 0
}
```
**Also set MaterialSearchView events listener (searchViewListener property).**
```
searchView.searchViewListener = object : SearchView.SearchViewListener{
    override fun onVisibilityChanged(visible: Boolean) {
        Log.v(MainActivity::javaClass.name, "onVisibilityChanged, visible $visible")
    }
    override fun onQueryTextChange(query: String) {
        Log.v(MainActivity::javaClass.name, "onQueryTextChange: $query")
    }
    override fun onQueryTextSubmit(query: String): Boolean {
        Log.v(MainActivity::javaClass.name, "onQueryTextSubmit: $query")
        return true
    }
    override fun onSuggestionSelected(suggestion: String) {
        Log.v(MainActivity::javaClass.name, "onSuggestionSelected $suggestion")
    }
}
```
**Set search suggestions (suggestionsData property) to view if needed.**
```
searchView.suggestionsData = arrayListOf("One", "Two", "Three")
```
**MaterialSearchView also has property isAlwaysVisible. Set it to true if you want to make View always visible. In this case set search MenuItem is not needed. Process onBackButtonPressed callback to exit from searching regime.**
**Also you can customize appearance of MaterialSearchView. Threre two  themes: Dark and Light. Also supported following attributes:**
```
    <attr name="isAlwaysVisible" format="boolean"/>
    <attr name="query" format="string|reference"/>
    <attr name="textColor" format="color|reference"/>
    <attr name="hint" format="string|reference"/>
    <attr name="hintColor" format="color|reference"/>
    <attr name="backgroundColor" format="color|reference"/>
    <attr name="overlayColor" format="color|reference"/>
    <attr name="textSize" format="dimension"/>
    <attr name="clickEffect" format="reference"/>
    <attr name="circleClickEffect" format="reference"/>
    <attr name="backBtnIcon" format="reference"/>
    <attr name="clearBtnIcon" format="reference"/>
    <attr name="recentIcon" format="reference"/>
```
**Sprecify your own style and set it to searchViewStyle attribute of MaterialSearchView:**
```
    <style name="AppTheme" parent="Theme.AppCompat.NoActionBar">
        <item name="colorPrimary">@color/colorPrimary</item>
        <item name="colorPrimaryDark">@color/colorPrimaryDark</item>
        <item name="colorAccent">@color/colorAccent</item>
        <item name="searchViewStyle">@style/mySearchViewStyle</item>
    </style>

    <style name="mySearchViewStyle" parent="SearchView.Light">
        <item name="backgroundColor">@color/colorPrimaryDark</item>
    </style>
```
**You can find the sample in 'sample' module of repository.** 
