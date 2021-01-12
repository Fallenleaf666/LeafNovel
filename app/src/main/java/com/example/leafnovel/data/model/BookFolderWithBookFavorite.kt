package com.example.leafnovel.data.model

import androidx.room.Embedded
import androidx.room.Relation

data class BookFolderWithBookFavorite(
    @Embedded val storedBookFolder:StoredBookFolder,
    @Relation(
        parentColumn = "folderid",
        entityColumn = "parent"
    )
    val bookFavorite:List<BookFavorite>
)