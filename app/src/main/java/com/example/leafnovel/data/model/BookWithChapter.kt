package com.example.leafnovel.data.model

import androidx.room.Embedded
import androidx.room.Relation

data class BookWithChapter(
    @Embedded val storedBook:StoredBook,
    @Relation(
        parentColumn = "bookid",
        entityColumn = "bookId"
    )
    val bookChapters:List<StoredChapter>
)