import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


//fun main(args: Array<String>) {
////    val url="https://cse.google.com/cse/element/v1?"
//    val url="https://cse.google.com/cse/element/v1?rsz=filtered_cse&num=10&hl=zh-TW&source=gcsc&gss=.com&cselibv=26b8d00a7c7a0812&cx=partner-pub-7553981642580305:sba9ctxj5fy&q=%E6%96%97%E7%BE%85&safe=active&cse_tok=AJvRUv1Yipy5bowdCsZW5GPQm5L_:1604490961218&exp=csqr,cc&oq=%E6%96%97%E7%BE%85&gs_l=partner-generic.3...2005.6028.0.21466.0.0.0.0.0.0.0.0..0.0.csems%2Cnrl%3D13...0.5557j2992859j32j5...1j4.34.partner-generic..0.0.0.&callback=google.search.cse.api11122"
//    val doc : Document = Jsoup.connect(url).get();
//    print(doc.toString())
//
//    class RetrofitManager private constructor() {
//        val api: MyAPIService
//        init {
//            // 設置baseUrl即要連的網站，addConverterFactory用Gson作為資料處理Converter
//            val retrofit = Retrofit.Builder()
//                .baseUrl("https://jsonplaceholder.typicode.com/")
//                .addConverterFactory(GsonConverterFactory.create())
//                .build()
//            api = retrofit.create(MyAPIService::class.java)
//        }
////        companion object {
////            // 以Singleton模式建立
////            val instance = RetrofitManager()
////        }
//    }
//
//}


