package com.example.leafnovel.data.model

import android.os.Parcelable
import androidx.annotation.NonNull
import androidx.room.*
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity
class LastReadProgress(
    @PrimaryKey
    @NonNull
    var bookId: String,
    @NonNull
    @ColumnInfo(name = "title")
    var chapterTitle: String,
    @NonNull
    var chapterIndex: Int,
    @NonNull
    var chapterUrl: String,
    @NonNull
    var readProgress: Float = 0f,
) : Parcelable {
    constructor():this("","",0,"",0f)
}


