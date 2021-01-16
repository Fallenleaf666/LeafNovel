package com.example.leafnovel.data.model

import android.os.Parcelable
import androidx.annotation.NonNull
import androidx.room.*
import androidx.room.ForeignKey.CASCADE
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(foreignKeys = [ForeignKey(entity = StoredBook::class,parentColumns = ["bookid"],childColumns = ["bookid"],onDelete = CASCADE)],
    indices = [Index("bookid",unique = true)])
//ForeignKey(entity = StoredBookFolder::class,parentColumns = ["parent"],childColumns = ["parent"],onDelete = CASCADE)
data class BookFavorite(
    @NonNull
    @ColumnInfo(name = "parent")
    var parentfolderid: Long,
    @NonNull
    @ColumnInfo(name = "bookid")
    var bookid: String,
    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "id")
    var id: Long = 0,
    @ColumnInfo(name = "creattime")
    var creattime: Long
) : Parcelable {
//    constructor():this(-3, "", creattime = System.currentTimeMillis())
}

