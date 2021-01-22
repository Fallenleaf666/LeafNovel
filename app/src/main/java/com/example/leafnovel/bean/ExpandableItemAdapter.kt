package com.example.leafnovel.bean

import android.text.TextUtils
import android.view.ViewGroup


abstract class ExpandableItemAdapter: ItemAdapter() {

    abstract override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder

    abstract override fun onBindViewHolder(holder: ItemViewHolder, position: Int)

    private fun expand(item: Item){
        var position = -1
        for(i in 0 until itemCount){
            val it = getItem(i)
            if(TextUtils.equals(it.uniqueId, item.uniqueId)){
                position = i
            }
        }
        getAllItem()[position] = item

        val iterator : MutableIterator<Item> = getAllItem().iterator()
        while (iterator.hasNext()){
            val it :Item = iterator.next()
            if(TextUtils.equals(it.parentUniqueId, item.uniqueId)){
                iterator.remove()
            }
        }
        if(item.getSubItems().size>0){
            getAllItem().addAll(position + 1, item.getSubItems())
        }
        item.isExpendable = true

        mExpandableToggleListener?.onExpand(item)
    }

    private fun collapse(item: Item){
        val iterator : MutableIterator<Item> = getAllItem().iterator()
        while (iterator.hasNext()){
            val it :Item = iterator.next()
            if(TextUtils.equals(it.parentUniqueId, item.uniqueId)){
                iterator.remove()
            }
        }

        item.isExpendable = false

        mExpandableToggleListener?.onCollapse(item)

    }

    interface ExpandableToggleListener{
        fun onExpand(item: Item)
        fun onCollapse(item: Item)
    }

    private var mExpandableToggleListener :ExpandableToggleListener? = null

    fun setExpandableToggleListener(listener: ExpandableToggleListener){
        this.mExpandableToggleListener = listener
    }

    fun toggle(item: Item){
        item.isExpendable = !item.isExpendable
        if(item.isExpendable){
            expand(item)
        }else{
            collapse(item)
        }
    }

}