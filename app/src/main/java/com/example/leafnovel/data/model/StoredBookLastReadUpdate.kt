package com.example.leafnovel.data.model

import androidx.room.*

@Entity
data class StoredBookLastReadUpdate(
@ColumnInfo(name = "lastread")val lastRead:String
)


