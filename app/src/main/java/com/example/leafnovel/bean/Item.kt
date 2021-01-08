package com.example.leafnovel.bean

import java.util.*
import kotlin.collections.ArrayList

abstract class Item {
    var id = 0
    var position = 0
    var isExpendable = true
    var parentUniqueId :String? = null
    val uniqueId = UUID.randomUUID().toString()
    val items = arrayListOf<Item>()

    abstract fun getType():Int

    fun addSubItem(item: Item){
        item.parentUniqueId = uniqueId
        items.add(item)
    }
    fun clearSubItems(){
        items.clear()
    }
    fun getSubItems():ArrayList<Item>{
        return items
    }
}
