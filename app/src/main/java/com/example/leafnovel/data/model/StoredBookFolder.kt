package com.example.leafnovel.data.model

import android.os.Parcelable
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity
data class StoredBookFolder(
    @NonNull
    var foldername:String,
    @NonNull
    var creattime:Long,
    @NonNull
    @PrimaryKey(autoGenerate = true)
    var folderid:Long = 0) : Parcelable

