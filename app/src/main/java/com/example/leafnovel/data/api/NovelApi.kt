package com.example.leafnovel.data.api

import com.example.leafnovel.data.model.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements


class NovelApi() {

    companion object{
//fun RequestChText(url:String):ChapterContents{
//    val doc : Document = Jsoup.connect("https://tw.uukanshu.com"+url).get();
////    val doc : Document = Jsoup.connect("https://tw.uukanshu.com/b/"+url+"/").get();
////    println(doc.text())
//    val contentbox : Element? = doc.getElementById("contentbox");
//    val contents : Elements = contentbox!!.getElementsByTag("p")
//    var chapterContents = ChapterContents()
//    for(i in contents){
//        val chapterContent = ChapterContent(0,"")
//        chapterContent.chapterLine = i.elementSiblingIndex()+1
//        chapterContent.chapterLineContent=i.text()
//        chapterContents.add(chapterContent)
//        println(i.text())
//        println()
//    }
//    return chapterContents
//}

fun RequestChTextBETA(url:String,bookChTitle:String,bookTitle:String):String{
    val doc : Document = Jsoup.connect("https://tw.uukanshu.com"+url).get();
    val contentbox : Element = doc.getElementById("contentbox")
    contentbox.children().select("div.ad_content").remove()
    println("處理前")
    println(contentbox)
//    contentbox.children().select("p").remove()
    println("處理中")
    println(contentbox)
    val rawtext = contentbox.text()
    val datatext = allToHelfText(rawtext,bookChTitle,bookTitle)
    println("處理後")
    print(datatext)
    return datatext
}


fun RequestSearchNovel(searchContent:String): BooksResults {
//    val doc : Document = Jsoup.connect("https://sj.uukanshu.com/search.aspx?k="+searchContent).get();
    val doc : Document = Jsoup.connect("https://t.uukanshu.com/search.aspx?k="+searchContent).get();
    val listbox : Element = doc.getElementById("bookList")
    val searchBooks : Elements = listbox.getElementsByTag("li")

    val booksResults = BooksResults()
    for(i in searchBooks){
        val bookNum = i.getElementsByClass("book_num").text()
        val tempDoc = i.getElementsByClass("name")
        val booktitle = tempDoc.attr("title")
        val bookId = tempDoc.attr("href")
        val bookUrl = tempDoc.attr("href").split("=")[1]
        val author =i.getElementsByClass("aut").text().split(" ")[0]
        val bookDescripe =i.getElementsByTag("p").first().getElementsByTag("a").first().text()
        val updateTime =i.getElementsByTag("p").first().getElementsByTag("span").first().text()

        var tempBook = Book("預設","000000",
        "","匿名","這是一本書","2000/01/01/19:36)")
        tempBook.bookUrl = bookUrl
        tempBook.author = author
        tempBook.booktitle = booktitle
//        println("順序"+bookNum)
//        println("書名"+booktitle)
//        println("Id"+bookId)
//        println("bookurl"+bookUrl)
//        println("作者"+author)
//        println("描述"+bookDescripe)
//        println("最後更新時間"+updateTime)
        println("book : "+tempBook)
        booksResults.add(tempBook)
    }
    return booksResults
}
        fun RequestSearchNovelBeta(searchContent:String): BooksResults {
//            val doc : Document = Jsoup.connect("https://t.uukanshu.com/search.aspx?k="+searchContent)
            val doc : Document = Jsoup.connect("https://t.uukanshu.com/search.aspx")
                .data("k",searchContent)
                .userAgent("Mozilla/5.0 (Windows NT 6.3; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.111 Safari/537.36")
                .referrer("https://t.uukanshu.com")
//                .timeout(5000)
                .get();
            val listbox : Element = doc.getElementById("bookList")
            val searchBooks : Elements = listbox.getElementsByTag("li")

            val booksResults = BooksResults()
            for(i in searchBooks){
                val bookNum = i.getElementsByClass("book_num").text()
                val tempDoc = i.getElementsByClass("name")
                val booktitle = tempDoc.attr("title")
                val bookId = tempDoc.attr("href").split("=")[1]
                val bookUrl = tempDoc.attr("href")
                val author =i.getElementsByClass("aut").text().split(" ")[0]
                val bookDescripe =i.getElementsByTag("p").first().getElementsByTag("a").first().text()
                val updateTime =i.getElementsByTag("p").first().getElementsByTag("span").first().text()
//                val updateTime =i.getElementsByClass("")

                var tempBook = Book("預設","000000",
                    "","匿名","這是一本書","2000/01/01/19:36)")
                tempBook.apply {
                    this.bookUrl = bookUrl
                    this.bookId = bookId
                    this.bookDescripe = bookDescripe
                    this.author = author
                    this.booktitle = booktitle
                    this.updateTime = updateTime
                }
                println("book : "+tempBook)
                booksResults.add(tempBook)
            }
            return booksResults
        }

fun RequestChList(id:String): BookChsResults {
    //    get dictery
//    val doc : Document = Jsoup.connect("https://tw.uukanshu.com/b/141809/").get();
    val doc : Document = Jsoup.connect("https://tw.uukanshu.com/b/"+id+"/").get();
//    get chlisttag
    val chapterList : Element ?= doc.getElementById("chapterList");
    val chapters :Elements = chapterList!!.getElementsByTag("li")

//    get chapters
    val chList:MutableList<Elements> = mutableListOf()
    for(i in chapters){
        val c=i.getElementsByTag("a")
        if(c.toString()!="")
            chList.add(c)
    }
    val chNum = chList.size
    println("總共 $chNum 章")
//    get ch title

    val bookChsResults = BookChsResults()
//    for(i in chList){
//    從網站上讀取的是倒序
    for(i in chList.size -1 downTo 0){
        val tempBookCh = BookChapter("預設","0000","")
        val title : String = chList[i].text()
        tempBookCh.chtitle = title
        val partHref : String = chList[i].attr("href")
        tempBookCh.chUrl = partHref
        tempBookCh.chId = (chList.size - 1 - i).toString()
//        chId尚未實作
        println(title+partHref)
        bookChsResults.add(tempBookCh)
    }
//
//    bookChsResults = ArrayList(bookChsResults.reversed()) as BookChsResults
    return bookChsResults
//    RequestChText(chList.shuffled().first().attr("href"));
}

fun allToHelfText(text :String,bookChTitle:String,bookTitle:String):String{
    val changeText = text.replace("ｗ","w")
        .replace("Ｕ","U")
        .replace("ｕ","u")
        .replace("ｋ","k")
        .replace("ａ","a")
        .replace("ｓ","s")
        .replace("ｎ","n")
        .replace("ｈ","h")
        .replace("．",".")
        .replace("ｃ","c")
        .replace("ｏ","o")
        .replace("ｍ","m")
        .replace("UU看書","")
        .replace("www.uukanshu.com","")
        .replace("您可以在百度里搜索“"+bookTitle+"小說酷筆記()”查找最新章節！","")
        .replace("請記住本書首發域名：。手機版更新最快網址：","")
        .replace("為了方便下次閱讀，你可以點擊下方的\"收藏\"記錄本次（"+bookChTitle+"）閱讀記錄，下次打開書架即可看到！","")
        .replace("喜歡《"+bookTitle+"》請向你的朋友（QQ、博客、微信等方式）推薦本書，謝謝您的支持！！()","")
        .replace("”"+bookTitle+" 新()”查找最新章節！","")
        .replace("。 ","。\n\n")
    return changeText
}
        fun RequestNovelDetail(bookId:String,bookTitle:String): MutableMap<String, String> {
            val doc : Document = Jsoup.connect("https://tw.uukanshu.com/b/"+bookId+"/")
                .userAgent("Mozilla/5.0 (Windows NT 6.3; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.111 Safari/537.36")
                .referrer("https://tw.uukanshu.com")
                .get()
            val nodeDoc = doc.select("dl.jieshao")
            val novelState = nodeDoc.select("span.status-text").first().text()
            val newChapter = nodeDoc.select("div.zuixin>a").first().text()
            val bookDescripe =nodeDoc.select("dd.jieshao_content>h3").first().text().replace(" www.uukanshu.com ","")
                .replace("http://www.uukanshu.com","").replace("－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－","")
                .replace(bookTitle+"簡介：","")
            val novelState2 = nodeDoc.select("div.shijian").first()
            novelState2.select("span#Span1").remove()
            novelState2.select("a").remove()
//            val updateTime = novelState2.text().replace("更新時間","")
            val updateTime = novelState2.text().replace("更新時間：","").replace(" ","")
            val imgUrl =nodeDoc.select("img[src$=.jpg]").attr("src")
            val bookMap = mutableMapOf("novelState" to novelState,
                "newChapter" to newChapter,
                "bookDescripe" to bookDescripe,
                "updateTime" to updateTime,
                "imgUrl" to imgUrl)
            println("book : "+bookMap)
            return bookMap
        }

        fun downloadBookChapter(bookDownloadInfo: BookDownloadInfo):ArrayList<StoredChapter> {
            val chapters = bookDownloadInfo.download
            val bookName = bookDownloadInfo.bookName
            val bookId = bookDownloadInfo.bookId
            val chapterContents = arrayListOf<StoredChapter>()
            for(i in chapters){
            val doc : Document = Jsoup.connect("https://tw.uukanshu.com"+i.chUrl).get();
            val contentBox : Element = doc.getElementById("contentbox")
            contentBox.children().select("div.ad_content").remove()
            val rawtext = contentBox.text()
            val datatext = allToHelfText(rawtext,i.chtitle,bookName)
                chapterContents.add(StoredChapter(bookId,i.chtitle,datatext,i.index,0,false))
            }
            return chapterContents
        }
    }
}