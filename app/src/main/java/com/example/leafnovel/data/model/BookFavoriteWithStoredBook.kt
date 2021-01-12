package com.example.leafnovel.data.model

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Relation
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


data class BookFavoriteWithStoredBook(
    var parent: Long,var bookid: String,var bookname: String,var author: String,var source: String,
    var newchapter: String,var lastread: String,var bookUrl: String)
//    @Embedded(prefix = "bookFavorite_")
//    val bookFavorite:BookFavorite,
//    @Embedded(prefix = "storedBook_")
//    val bookInfo:StoredBook
