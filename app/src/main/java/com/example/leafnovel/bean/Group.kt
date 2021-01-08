package com.example.leafnovel.bean

class Group(name:String) : Item() {
    companion object{
        private const val TYPE_GROUP = 0xfa01
    }
    var title: String? = null
    override fun getType(): Int {
        return TYPE_GROUP
    }
    constructor():this("未分類"){}
}