package com.example.leafnovel.data.model

import android.os.Parcelable
import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity
data class StoredBookUpdateInfo(
    @ColumnInfo(name = "newchapter")
    var newChapter: String
) : Parcelable

