package com.example.leafnovel
//import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
//import com.sun.tools.javac.Main
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Deferred
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import retrofit2.Retrofit
//import retrofit2.converter.gson.GsonConverterFactory
//import retrofit2.http.GET
//import retrofit2.http.Path
//
//import retrofit2.http.Query
//
//val url="https://cse.google.com/cse/element/v1"
////http://api1998.cn/api/uu/so/link?id=%E9%87%8D%E7%94%9F%E5%AE%8C%E7%BE%8E%E6%97%B6%E4%BB%A3&key=6fe4f0454325424251ebae094318f83f
//var searchText="斗羅"
//var search="cx=partner-pub-7553981642580305:sba9ctxj5fy&q="+searchText+"&cse_tok=AJvRUv1knGZFUgYo-n41FhSbORqe:1604561078813&exp=csqr,cc&oq=%E5%9F%BA%E5%9B%A0&gs_l=partner-generic.3...65382.68532.0.69713.0.0.0.0.0.0.0.0..0.0.csems%2Cnrl%3D13...0.4184j6108804j16j3...1j4.34.partner-generic..0.0.0.&callback=google.search.cse.api10323"
//val requestur = "https://cse.google.com/cse/element/v1"
//
//fun main(){
//    val novelService:NovelService by lazy{
//        Retrofit.Builder()
//            .baseUrl(url)
//            .addConverterFactory(GsonConverterFactory.create())
//            .addCallAdapterFactory(CoroutineCallAdapterFactory())
//            .build().create(NovelService::class.java)
//    }
//
//    CoroutineScope(Dispatchers.IO).launch {
//
//        // Retrofit return Differed<Response<BookStore>>
//        val novelResult = novelService.getNovels(searchText)
//        print(novelResult?.size.let{print(it)})
//        print(novelResult)
////        val response = result.body();
//    }}
//interface NovelService{
//    @GET("?cx=partner-pub-7553981642580305:sba9ctxj5fy&q={searchText}&cse_tok=AJvRUv1knGZFUgYo-n41FhSbORqe:1604561078813&exp=csqr,cc&oq=%E5%9F%BA%E5%9B%A0&gs_l=partner-generic.3...65382.68532.0.69713.0.0.0.0.0.0.0.0..0.0.csems%2Cnrl%3D13...0.4184j6108804j16j3...1j4.34.partner-generic..0.0.0.&callback=google.search.cse.api10323")
//    suspend fun  getNovels(@Path("searchText") searchText:String) : NovelResult?
//
//}

//fun getSearchNovel():String{
//
//}
