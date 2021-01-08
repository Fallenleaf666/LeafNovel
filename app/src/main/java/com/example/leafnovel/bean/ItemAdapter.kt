package com.example.leafnovel.bean

import android.text.TextUtils
import androidx.recyclerview.widget.RecyclerView

abstract class ItemAdapter : RecyclerView.Adapter<ItemViewHolder>() {
    var mItems = arrayListOf<Item>()

    override fun getItemCount(): Int {
        return mItems.size
    }

    override fun getItemViewType(position: Int): Int {
        return mItems[position].getType()
    }

    fun getItem(position: Int): Item {
        return mItems[position]
    }

    fun getAllItem(): ArrayList<Item> {
        return mItems
    }

    fun addItem(item: Item) {
        mItems.add(item)
        if (item.isExpendable) {
            for(i in 0 until item.getSubItems().size){
                mItems.add(item.getSubItems()[i])
            }
        }
    }

    fun addItemToPosition(item: Item) {
        mItems.add(0,item)
        if (item.isExpendable) {
            for(i in 0 until item.getSubItems().size){
                mItems.add(item.getSubItems()[i])
            }
        }
    }

    fun addAll(lists: ArrayList<Item>) {
        mItems.addAll(lists)
    }

    fun clear() {
        mItems.clear()
    }

    fun getItem(type: Int, id: Int): Item? {
        var mItem: Item? = null
        for (i in mItems.indices) {
            val item: Item = mItems[i]
            if (item.getType() == type && item.id == id) {
                mItem = item
            }
        }
        return mItem
    }

    fun setItem(item: Item) {
        var position = -1
        for (i in 0 until itemCount) {
            val it = getItem(i)
            if (TextUtils.equals(it.uniqueId, item.uniqueId)) {
                position = i
            }
        }
        getAllItem()[position] = item

        val iterator: MutableIterator<Item> = getAllItem().iterator()
        while (iterator.hasNext()) {
            val it: Item = iterator.next()
            if (TextUtils.equals(it.uniqueId, item.uniqueId)) {
                iterator.remove()
            }
        }
        if (item.isExpendable) {
            if (item.getSubItems().size > 0) {
                getAllItem().addAll(position + 1, item.getSubItems())
            }
        }
    }

}