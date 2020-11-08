package com.example.leafnovel

import NovelResult
import android.os.Bundle
import android.util.Log
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

val url = "https://cse.google.com/cse/element/"
var searchText = "斗羅"

    val okHttpClientvalor = OkHttpClient.Builder()
        .connectTimeout(130, TimeUnit.SECONDS)
        .writeTimeout(130, TimeUnit.SECONDS)
        .readTimeout(130, TimeUnit.SECONDS)
        .build()

    val novelService: NovelService by lazy {
        Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .client(okHttpClientvalor)
            .build().create(NovelService::class.java)
    }
//    CoroutineScope(Dispatchers.IO).launch {
//        // Retrofit return Differed<Response<BookStore>>
////            val novelResult = novelService.getNovels(searchText)
//        val novelResult = novelService.getNovels("partner-pub-7553981642580305:sba9ctxj5fy",searchText,
//            "AJvRUv1knGZFUgYo-n41FhSbORqe:1604561078813",
//            "csqr,cc","%E5%9F%BA%E5%9B%A0&gs_l=partner-generic.3...65382.68532.0.69713.0.0.0.0.0.0.0.0..0.0.csems%2Cnrl%3D13...0.4184j6108804j16j3...1j4.34.partner-generic..0.0.0.&callback=google.search.cse.api10323")
////            val novelResult = novelService.getNovels()
//        print(novelResult?.size.let { print(it) })
//        print(novelResult)
////        Log.d(MainActivity.TAG,"onCreate:${novelResult?.size}")
////        val response = result.body();
//    }

interface NovelService {
    //    @GET("v1?cx=partner-pub-7553981642580305:sba9ctxj5fy&q={searchText}&cse_tok=AJvRUv1knGZFUgYo-n41FhSbORqe:1604561078813&exp=csqr,cc&oq=%E5%9F%BA%E5%9B%A0&gs_l=partner-generic.3...65382.68532.0.69713.0.0.0.0.0.0.0.0..0.0.csems%2Cnrl%3D13...0.4184j6108804j16j3...1j4.34.partner-generic..0.0.0.&callback=google.search.cse.api10323")
//    suspend fun getNovels(@Path("searchText") searchText: String): NovelResult?
    @GET("v1")
    suspend fun getNovels(@Query("cx")cx:String, @Query("q")q:String, @Query("cse_tok")cse_tok:String
                          , @Query("exp")exp:String, @Query("oq")oq:String): NovelResult?
}
