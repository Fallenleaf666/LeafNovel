package com.example.leafnovel.data.model

import android.os.Parcelable
import androidx.annotation.NonNull
import androidx.room.*
import kotlinx.android.parcel.Parcelize

@Parcelize
class WebSearchBookResult(
    var state: SearchResult,
    var booksResults: BooksResults,
    var source: SearchSource,
) : Parcelable {
}
enum class SearchResult{
    SUCCESS,FAIL
}

enum class SearchSource{
    UU
}


