package com.example.leafnovel

import RequestSearchNovel
import RequestChText

fun main() {
    var search=readLine().toString()
    RequestSearchNovel(search)
    var searchId=readLine().toString()
    RequestChText(searchId)
}