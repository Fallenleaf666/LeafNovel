package com.example.leafnovel.data.model

import android.os.Parcelable
import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity
data class StoredBookFolder(
    @NonNull
    var foldername: String,
    @NonNull
    var creattime: Long,
    @NonNull
    @PrimaryKey(autoGenerate = true)
    var folderid: Long = 0,
    var isexpend: Boolean
) : Parcelable{
    constructor(folderName:String,createTime:Long):this(foldername = folderName,creattime = createTime,isexpend = false)
}

