package com.example.leafnovel.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.leafnovel.bean.Child
import com.example.leafnovel.bean.Group
import com.example.leafnovel.data.model.*

@Dao
interface StoredBookDao{

    @Transaction
    @Query("select * from BookFavorite")
    fun getAllFavoriteBook():List<BookFavorite>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addFavoriteBook(bookFavorite: BookFavorite)

    @Transaction
    fun addStoredBookAndFavoriteBook(storedBook:StoredBook,bookFavorite:BookFavorite){
        insert(storedBook)
        addFavoriteBook(bookFavorite)
    }

    @Query(value = "delete from BookFavorite where bookid = :bookId")
    fun removeFavoriteBook(bookId:String)

    @Query(value = "select * from BookFavorite where bookid = :bookId")
    fun getFavoriteBook(bookId:String) : LiveData<BookFavorite>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(storedBook: StoredBook)

    @Query(value = "select * from storedbook where bookid = :storedBookId")
    fun getStoredBook(storedBookId:String):StoredBook

    @Query("UPDATE storedbook SET newchapter = :newChapter where bookid = :bookId")
    fun updateStoredBook(bookId:String,newChapter:String)


    @Query(value = "select * from StoredBookFolder order by creattime desc")
    fun getAllStoredBookFolder() : LiveData<List<StoredBookFolder>>

    @Query(value = "select * from StoredBookFolder order by creattime desc")
    fun getAllStoredBookFolderNoLive() : List<StoredBookFolder>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addBookFolder(storedBookFolder: StoredBookFolder):Long

    @Query("DELETE from storedBookFolder where folderid = :folderid")
    fun deleteBookFolder(folderid: Long)

    @Transaction
    fun deleteBookFolderAndUpdate(folderid: Long){
        deleteBookFolder(folderid)
//        updateBookParentFolderBeta(folderid,System.currentTimeMillis())
        updateBookParentFolder(folderid)
    }

    @Query("UPDATE storedBookFolder SET foldername = :updateName where folderid = :storedBookFolderId")
    fun updateBookFolder(updateName:String,storedBookFolderId:Long)

    @Query("UPDATE storedbook SET lastread = :lastReadChapter where bookid = :bookId")
    fun updateStoreBookLastReadProgress(lastReadChapter:String,bookId:String)

    @Query("UPDATE storedbook SET parent = -5 where parent = :oldFolderId")
    fun updateBookParentFolder(oldFolderId:Long)

    @Query("UPDATE BookFavorite SET parent = -5 , creattime = :creattime where parent = :oldFolderId")
    fun updateBookParentFolderBeta(oldFolderId:Long,creattime:Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveChapter(storedChapter: StoredChapter)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveLastReadProgress(lastReadProgress: LastReadProgress)

    @Transaction
    fun updateLastReadProgress(lastReadProgress: LastReadProgress){
        saveLastReadProgress(lastReadProgress)
        updateStoreBookLastReadProgress(lastReadProgress.chapterTitle,lastReadProgress.bookId)
    }

    @Query(value = "select * from LastReadProgress where bookId = :bookId")
    fun getLastReadProgress(bookId:String) : LiveData<LastReadProgress>

    @Query(value = "select * from storedBook")
    fun getAll() : LiveData<List<StoredBook>>

    @Query(value = "delete from storedBook")
    fun deleteAll()

    @Query(value = "delete from StoredChapter")
    fun deleteAllChapter()

    @Delete
    fun delete(storedbook: StoredBook)

//    @Query("DELETE from storedBook where bookid = :bookId")
//    fun deleteBookById(bookId: String)
    @Query("DELETE from BookFavorite where bookid = :bookId")
    fun deleteBookById(bookId: String)

    @Transaction
    @Query("select * from storedBook where bookId IN(:bookId) ")
    fun getChapterWithBook(bookId:String):List<BookWithChapter>

    @Query("select * from StoredChapter where bookId = :bookId and [index] = :index")
    fun getChapterWithBookIdAndIndex(bookId:String,index:Int):StoredChapter

    @Query("select [index] from StoredChapter where bookId = :bookId")
    fun queryBookChpapterIndexes(bookId:String):LiveData<List<ChapterIndex>>

    @Query("select * from StoredBook where parent = :folderId")
    fun getBooksByFolderId(folderId:Long):List<StoredBook>

    @Transaction
    @Query("select BookFavorite.bookid ,StoredBook.* from BookFavorite inner join StoredBook on BookFavorite.bookid = StoredBook.bookid where BookFavorite.parent = :folderId order by BookFavorite.creattime desc")
//    @Query("select * from BookFavorite,StoredBook where BookFavorite.parent = :folderId")
//    @Query("select * from BookFavorite,StoredBook where BookFavorite.parent = :folderId and BookFavorite.bookid = StoredBook.bookid ")
    fun getFavoriteBooksByFolderId(folderId:Long):List<BookFavoriteWithStoredBook>

    @Query("select * from StoredBookFolder order by creattime desc")
    fun getBookFolders():List<StoredBookFolder>

    @Query("select * from StoredBookFolder  where folderid = :id")
    fun getSingleBookFolder(id:Long): StoredBookFolder?

    @Transaction
    fun getAllBookFolders():List<StoredBookFolder>{
        if(getSingleBookFolder(-5) == null){
            addBookFolder(StoredBookFolder("未分類",0,-5))
        }
        return getBookFolders()
    }

    @Transaction
    fun getFolderWithBook():ArrayList<Group>{
        //之後可改成relation
//        val folderList = getAllStoredBookFolderNoLive().toMutableList()
        val folderList = getAllBookFolders().toMutableList()
        //      預設未分類
//        folderList.add(StoredBookFolder("未分類",0,-5))
        val folderIdList = arrayListOf<Long>()
        for(i in folderList.indices){
            folderIdList.add(folderList[i].folderid)
        }
        val folderWithBook = ArrayList<Group>()
        for(j in folderList.indices){
            val folder = Group().apply {
                id = folderList[j].folderid.toInt()
                isExpendable = j == 0
                title = folderList[j].foldername
            }
            val storedBookList = getBooksByFolderId(folderList[j].folderid)
            for (k in storedBookList.indices) {
                val storedBook = Child()
                storedBook.bookUrl = storedBookList[k].bookUrl
                storedBook.newChapter= storedBookList[k].newchapter
                storedBook.lastRead= storedBookList[k].lastread
                storedBook.bookAuthor = storedBookList[k].bookauthor
                storedBook.bookName = storedBookList[k].bookname
                storedBook.isMostLike = storedBookList[k].ismostlike
                storedBook.bookId = storedBookList[k].bookid
                storedBook.position = k
                storedBook.group = folder
                folder.addSubItem(storedBook)
            }
            folderWithBook.add(folder)
        }
        return folderWithBook
    }

//    @Query("UPDATE storedbook SET parent = :folderId where bookid = :bookId")
//    fun moveBook(bookId: String, folderId: Long)

    @Query("UPDATE BookFavorite SET parent = :folderId where bookid = :bookId")
    fun moveBook(bookId: String, folderId: Long)

    @Transaction
    fun getFolderWithBookBeta():ArrayList<Group>{
        //之後可改成relation
//        val folderList = getAllStoredBookFolderNoLive().toMutableList()
        val folderList = getAllBookFolders().toMutableList()
        //預設未分類
//        folderList.add(StoredBookFolder("未分類",0,-5))
        val folderIdList = arrayListOf<Long>()
        for(i in folderList.indices){
            folderIdList.add(folderList[i].folderid)
        }
        val folderWithBook = ArrayList<Group>()
        for(j in folderList.indices){
            val folder = Group().apply {
                id = folderList[j].folderid.toInt()
                isExpendable = j == 0
                title = folderList[j].foldername
            }
            val storedBookList = getFavoriteBooksByFolderId(folderList[j].folderid)
            for (k in storedBookList.indices) {
                val bookInfo = storedBookList[k]
                val storedBook = Child()
                storedBook.bookUrl = bookInfo.bookUrl
                storedBook.newChapter= bookInfo.newchapter
                storedBook.lastRead= bookInfo.lastread
                storedBook.bookAuthor = bookInfo.author
                storedBook.bookName = bookInfo.bookname
                storedBook.bookId = bookInfo.bookid
                storedBook.position = k
                storedBook.group = folder
                folder.addSubItem(storedBook)
            }
            folderWithBook.add(folder)
        }
        return folderWithBook
    }

}