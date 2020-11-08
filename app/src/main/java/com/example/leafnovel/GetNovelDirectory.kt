import com.example.leafnovel.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import java.net.URL
//import javax.print.DocFlavor



fun RequestChText(url:String):ChapterContents{
    val doc : Document = Jsoup.connect("https://tw.uukanshu.com"+url).get();
//    val doc : Document = Jsoup.connect("https://tw.uukanshu.com/b/"+url+"/").get();
//    println(doc.text())
    val contentbox : Element? = doc.getElementById("contentbox");
    val contents : Elements = contentbox!!.getElementsByTag("p")
    var chapterContents = ChapterContents()
    for(i in contents){
        val chapterContent = ChapterContent(0,"")
        chapterContent.chapterLine = i.elementSiblingIndex()+1
        chapterContent.chapterLineContent=i.text()
        chapterContents.add(chapterContent)
        println(i.text())
        println()
    }
    return chapterContents
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

        var tempBook =Book("0","預設","000000",
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

fun RequestChList(id:String):BookChsResults{
    //    get dictery
//    val doc : Document = Jsoup.connect("https://tw.uukanshu.com/b/141809/").get();
    val doc : Document = Jsoup.connect("https://tw.uukanshu.com/b/"+id+"/").get();
//    get chlisttag
    val chapterList : Element ?= doc.getElementById("chapterList");
    val chapters :Elements = chapterList!!.getElementsByTag("li")

//    get chapters
    var chList:MutableList<Elements> = mutableListOf()
    for(i in chapters){
        var c=i.getElementsByTag("a")
        if(c.toString()!="")
            chList.add(c)
    }
    val chNum = chList.size
    println("總共 $chNum 章")
//    get ch title

    val bookChsResults = BookChsResults()
    for(i in chList){
        var tempBookCh = BookChapter("預設","0000","")
        val title : String = i.text()
        tempBookCh.chtitle = title
        val partHref : String = i.attr("href")
        tempBookCh.chUrl = partHref
        println(title+partHref)
        bookChsResults.add(tempBookCh)
    }
    return bookChsResults
//    RequestChText(chList.shuffled().first().attr("href"));



}