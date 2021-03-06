package com.example.leafnovel.data.api

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import com.example.leafnovel.data.DownloadNovelService
import com.example.leafnovel.data.model.*
import com.example.leafnovel.receiver.DownloadResultReceiver
import com.github.houbb.opencc4j.util.ZhConverterUtil.toTraditional
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import java.io.IOException
import java.lang.Exception
import kotlin.system.measureTimeMillis


class NovelApi {
    companion object {
        fun requestChapterText(url: String, bookChTitle: String, bookTitle: String): String{
            try{
                val doc: Document = Jsoup.connect("https://tw.uukanshu.com$url").get()
                val contentBox: Element = doc.getElementById("contentbox")
                contentBox.children().select("div.ad_content").remove()
//            println("處理前")
//            println(contentBox)
//            println("處理中")
//            println(contentBox)
                val rawtext = contentBox.text()
                val dataText = allToHelfText(rawtext, bookChTitle, bookTitle)
//            println("處理後")
//            print(dataText)
                return dataText
            }catch (e :IOException){
                return "error"
            }
        }
        const val TAG = "NovelApi"

        fun requestSearchNovel(searchContent: String): WebSearchBookResult {
//            val doc : Document = Jsoup.connect("https://t.uukanshu.com/search.aspx?k="+searchContent)
//            繁體
//            val doc: Document = Jsoup.connect("https://t.uukanshu.com/search.aspx")
//            簡體
            try{
                val doc: Document = Jsoup.connect("https://sj.uukanshu.com/search.aspx")
                    .data("k", searchContent)
                    .userAgent("Mozilla/5.0 (Windows NT 6.3; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.111 Safari/537.36")
                    .referrer("https://t.uukanshu.com")
//                    .timeout(5000)
                    .get()
                val listBox: Element = doc.getElementById("bookList")
                val searchBooks: Elements = listBox.getElementsByTag("li")

                val booksResults = BooksResults()
                for (i in searchBooks) {
//                val bookNum = i.getElementsByClass("book_num").text()
                    val tempDoc = i.getElementsByClass("name")
                    val bookTitle = tempDoc.attr("title")
                    val bookId = tempDoc.attr("href").split("=")[1]
                    val bookUrl = tempDoc.attr("href")
                    val author = i.getElementsByClass("aut").text().split(" ")[0]
                    var bookDescribe = i.getElementsByTag("p").first().getElementsByTag("a").first().text()
                    bookDescribe = clearString(bookDescribe)
                    val updateTime = i.getElementsByTag("p").first().getElementsByTag("span").first().text()

                    val tempBook = Book()
                    tempBook.apply {
                        this.bookUrl = bookUrl
                        this.bookId = bookId
                        //簡體轉繁體
                        this.bookDescripe = toTraditional(bookDescribe)
                        this.author = toTraditional(author)
                        this.booktitle = toTraditional(bookTitle)
                        this.updateTime = updateTime
//                        this.author = author
//                        this.booktitle = bookTitle
//                        this.bookDescripe = bookDescribe
                    }
                    booksResults.add(tempBook)
                }
                booksResults.sortBy { it.booktitle.length }

                return WebSearchBookResult(SearchResult.SUCCESS, booksResults, SearchSource.UU)
            }catch (e: IOException){
                return WebSearchBookResult(SearchResult.FAIL,BooksResults(),SearchSource.UU)
            }
        }


        fun requestChapterList(id: String): BookChsResults {
            //get dictery
            //val doc : Document = Jsoup.connect("https://tw.uukanshu.com/b/141809/").get()
            val doc: Document = Jsoup.connect("https://tw.uukanshu.com/b/$id/").get()
            //get chlisttag
            val chapterList: Element? = doc.getElementById("chapterList")
            val chapters: Elements = chapterList!!.getElementsByTag("li")

            //get chapters
            val chList: MutableList<Elements> = mutableListOf()
            for (i in chapters) {
                val c = i.getElementsByTag("a")
                if (c.toString() != "")
                    chList.add(c)
            }
            val chNum = chList.size
//            println("總共 $chNum 章")

            val bookChsResults = BookChsResults()
            var title: String
            var partHref: String
            //將網站上讀取的倒序轉為正序
            for (i in chList.size - 1 downTo 0) {
                title= chList[i].text()
                partHref = chList[i].attr("href")
                bookChsResults.add(BookChapter().apply {
                    chtitle = title
                    chUrl = partHref
                    chIndex = (chList.size - 1 - i)
                })
            }
            Log.d(TAG,"$bookChsResults")
            return bookChsResults
        }

        private fun allToHelfText(text: String, bookChTitle: String, bookTitle: String): String {
            return text.replace("ｗ", "w")
                .replace("Ｕ", "U")
                .replace("ｕ", "u")
                .replace("ｋ", "k")
                .replace("ａ", "a")
                .replace("ｓ", "s")
                .replace("ｎ", "n")
                .replace("ｈ", "h")
                .replace("．", ".")
                .replace("ｃ", "c")
                .replace("ｏ", "o")
                .replace("ｍ", "m")
                .replace("UU看書", "")
                .replace("www.uukanshu.com", "")
                .replace("您可以在百度里搜索“" + bookTitle + "小說酷筆記()”查找最新章節！", "")
                .replace("請記住本書首發域名：。手機版更新最快網址：", "")
                .replace("為了方便下次閱讀，你可以點擊下方的\"收藏\"記錄本次（$bookChTitle）閱讀記錄，下次打開書架即可看到！", "")
                .replace("喜歡《$bookTitle》請向你的朋友（QQ、博客、微信等方式）推薦本書，謝謝您的支持！！()", "")
                .replace("”$bookTitle 新()”查找最新章節！", "")
                .replace("。 ", "。\n\n")
        }

        fun requestNovelDetail(bookId: String, bookTitle: String): MutableMap<String, String> {
            val doc: Document = Jsoup.connect("https://tw.uukanshu.com/b/$bookId/")
                .userAgent("Mozilla/5.0 (Windows NT 6.3; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.111 Safari/537.36")
                .referrer("https://tw.uukanshu.com")
                .get()
            val nodeDoc = doc.select("dl.jieshao")
            val novelState = nodeDoc.select("span.status-text").first().text()
            val newChapter = nodeDoc.select("div.zuixin>a").first().text()
            var bookDescripe = nodeDoc.select("dd.jieshao_content>h3").first().text().replace(" www.uukanshu.com ", "")
                .replace("http://www.uukanshu.com", "").replace("－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－", "")
                .replace(bookTitle + "簡介：", "")
            bookDescripe = clearStringForDetail(bookDescripe)
            val novelState2 = nodeDoc.select("div.shijian").first()
            novelState2.select("span#Span1").remove()
            novelState2.select("a").remove()
//            val updateTime = novelState2.text().replace("更新時間","")
            val updateTime = novelState2.text().replace("更新時間：", "").replace(" ", "")
            val imgUrl = nodeDoc.select("img[src$=.jpg]").attr("src")
            val bookMap = mutableMapOf(
                "novelState" to novelState,
                "newChapter" to newChapter,
                "bookDescripe" to bookDescripe,
                "updateTime" to updateTime,
                "imgUrl" to imgUrl
            )
            return bookMap
        }

        fun requestNovelDetailForRefresh(bookId: String, bookTitle: String): MutableMap<String, String> {
            val doc: Document = Jsoup.connect("https://tw.uukanshu.com/b/$bookId/")
                .userAgent("Mozilla/5.0 (Windows NT 6.3; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.111 Safari/537.36")
                .referrer("https://tw.uukanshu.com")
                .get()
            val nodeDoc = doc.select("dl.jieshao")
            val novelState = nodeDoc.select("span.status-text").first().text()
            val newChapter = nodeDoc.select("div.zuixin>a").first().text()
//            val bookDescripe = nodeDoc.select("dd.jieshao_content>h3").first().text().replace(" www.uukanshu.com ", "")
//                .replace("http://www.uukanshu.com", "").replace("－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－", "")
//                .replace(bookTitle + "簡介：", "")
            val novelState2 = nodeDoc.select("div.shijian").first()
            novelState2.select("span#Span1").remove()
            novelState2.select("a").remove()
            val updateTime = novelState2.text().replace("更新時間：", "").replace(" ", "")
            val imgUrl = nodeDoc.select("img[src$=.jpg]").attr("src")
            val bookMap = mutableMapOf(
                "novelState" to novelState,
                "newChapter" to newChapter,
//                "bookDescripe" to bookDescripe,
                "updateTime" to updateTime,
                "imgUrl" to imgUrl
            )
            return bookMap
        }

        //清理簡介的內容
        private fun clearString(s: String?): String {
            var clearString = s ?: "該小說無簡介內容"
            if (clearString == "" || clearString == " ") {
                clearString = "該小說無簡介內容"
            } else if (clearString[0] == ';') {
                clearString = clearString.substring(1, clearString.length)
            }else if(clearString[0] == '　'&& clearString[1] == '　'){
                clearString = clearString.substring(2, clearString.length)
            }
            clearString = clearString.replace(" ", "").replace("　　", "")
            return clearString
        }

        //清理簡介的內容，用在小說詳細介紹頁面用
        private fun clearStringForDetail(s: String?): String {
            var clearString = s ?: "該小說無簡介內容"
            if (clearString == "" || clearString == " ") {
                clearString = "該小說無簡介內容"
            } else if (clearString[0] == ';') {
                clearString = clearString.substring(1, clearString.length)
            }else if(clearString[0] == '　'&& clearString[1] == '　'){
                clearString = clearString.substring(2, clearString.length)
            }
            clearString = clearString.replace("。", "。\n\n").replace("　　", "").replace(" ", "")
            return clearString
        }

//        fun downloadBookChapter(bookDownloadInfo: BookDownloadInfo): ArrayList<StoredChapter> {
//            val chapters = bookDownloadInfo.download
//            val bookTitle = bookDownloadInfo.bookName
//            val bookId = bookDownloadInfo.bookId
//            val chapterContents = arrayListOf<StoredChapter>()
//            for (i in chapters) {
//                val chapterContentText = requestChapterText(i.chUrl, i.chtitle, bookTitle)
//                chapterContents.add(StoredChapter(bookId, i.chtitle, chapterContentText, i.chIndex, 0, false))
//            }
//            return chapterContents
//        }

//        fun downloadBookChapterBeta(singleBookDownloadInfo: SingleBookDownloadInfo): StoredChapter {
//            val bookChapter = singleBookDownloadInfo.bookChapter
//            val bookTitle = singleBookDownloadInfo.bookName
//            val bookId = singleBookDownloadInfo.bookId
//            val chapterContentText = requestChapterText(bookChapter.chUrl, bookChapter.chtitle, bookTitle)
//            return StoredChapter(bookId, bookChapter.chtitle, chapterContentText, bookChapter.chIndex, 0, false)
//        }

//        fun downloadBookChapterThread(bookDownloadInfo: BookDownloadInfo): ArrayList<StoredChapter> {
//            val chapters = bookDownloadInfo.download
//            val bookTitle = bookDownloadInfo.bookName
//            val bookId = bookDownloadInfo.bookId
//            val chapterContents = arrayListOf<StoredChapter>()
//            for (i in chapters) {
//                Executor{
//                    val chapterContentText = requestChapterText(i.chUrl, i.chtitle, bookTitle)
//                    chapterContents.add(StoredChapter(bookId, i.chtitle, chapterContentText, i.chIndex, 0, false))
//                }
//            }
//            return chapterContents
//        }
    }
}