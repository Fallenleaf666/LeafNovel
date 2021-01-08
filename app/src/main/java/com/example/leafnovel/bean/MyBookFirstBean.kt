package com.example.leafnovel.bean
class MyBookFirstBean(name:String) {
    var folderName = name
    var isExpand = false
    var addedSubNum = 0
    constructor():this("未分類"){}
}