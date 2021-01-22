package com.example.leafnovel.bean

import android.text.TextUtils
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.example.leafnovel.bean.MyBookAdapter.Companion.TYPE_CHILD
import com.example.leafnovel.bean.MyBookAdapter.Companion.TYPE_GROUP

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
            if (TextUtils.equals(it.parentUniqueId, item.uniqueId)) {
                iterator.remove()
            }
        }
        if (item.isExpendable) {
            if (item.getSubItems().size > 0) {
                getAllItem().addAll(position + 1, item.getSubItems())
            }
        }
    }

    fun removeItem(removeGroup: Item) {
        var oldPosition = findItemPosition(removeGroup, TYPE_GROUP)
        var newPosition = -1
//      清除struct的item
        val iterator: MutableIterator<Item> = getAllItem().iterator()
        while (iterator.hasNext()) {
            val it: Item = iterator.next()
            if (TextUtils.equals(it.parentUniqueId, removeGroup.uniqueId) ||
                TextUtils.equals(it.uniqueId, removeGroup.uniqueId)
            ) {
                iterator.remove()
            }
        }
        if (removeGroup.isExpendable) {
            notifyItemRangeRemoved(oldPosition, removeGroup.getSubItems().size + 1)
        } else {
            notifyItemRemoved(oldPosition)
        }
        for (j in 0 until itemCount) {
            val it = getItem(j)
            if (it.id == -5) {
                newPosition = j
            }
        }


        val oldSubItems = newPosition + getAllItem()[newPosition].getSubItems().size
        for (i in removeGroup.getSubItems()) {
            getAllItem()[newPosition].addSubItem(i)
        }

        if (getAllItem()[newPosition].isExpendable) {
            if (getAllItem()[newPosition].getSubItems().size > 0) {
                getAllItem().addAll(removeGroup.getSubItems())
                notifyItemRangeInserted(oldSubItems + 1, removeGroup.getSubItems().size)
            }
        }
    }

    fun moveChildItem(moveChildItem: Child, targetGroupId: Long) {
        //要移往的Group位置
        var newParentFolderPosition = -1

        //item原本的位置
        val oldItemPosition = findItemPosition(moveChildItem, TYPE_CHILD)

        //將item從adapter的list中移除
        removeItemFromList(moveChildItem)

        //item原本所屬parent group的位置
        val oldParentFolderPosition = findItemParentPosition(moveChildItem)

        //將item從原本所屬parent group中移除
        getAllItem()[oldParentFolderPosition].removeSubItem(moveChildItem)

        //更新索引值
        notifyItemRemoved(oldItemPosition)
        notifyItemRangeChanged(oldItemPosition, itemCount - oldItemPosition)

        //item要移往的 group 位置
        for (i in 0 until itemCount) {
            val it = getItem(i)
            if (it.getType() == TYPE_GROUP && it.id == targetGroupId.toInt()) {
                newParentFolderPosition = i
            }
        }
        //將item的parent屬性改成要移往的 groupId
        moveChildItem.parentUniqueId = getAllItem()[newParentFolderPosition].uniqueId

        //將item加入要移往的group中
        getAllItem()[newParentFolderPosition].addSubItem(moveChildItem)

        //確認group當前展開的狀態
        if (getAllItem()[newParentFolderPosition].getType() == TYPE_GROUP && getAllItem()[newParentFolderPosition].isExpendable) {
            //將item加入adapter list中欲移往的group的底端
            getAllItem().add(newParentFolderPosition + getAllItem()[newParentFolderPosition].getSubItems().size, moveChildItem)
            //更新索引值
            notifyItemInserted(newParentFolderPosition + getAllItem()[newParentFolderPosition].getSubItems().size)
            notifyItemRangeChanged(
                newParentFolderPosition + getAllItem()[newParentFolderPosition].getSubItems().size,
                itemCount - newParentFolderPosition - getAllItem()[newParentFolderPosition].getSubItems().size
            )
        }
    }

    fun removeChildItem(removeItem: Item) {
        var position = -1
        var parentPosition = -1

        position = findItemPosition(removeItem, TYPE_CHILD)
        removeItemFromList(removeItem)
        parentPosition = findItemParentPosition(removeItem)
        if(parentPosition != -1){
            getAllItem()[parentPosition].removeSubItem(removeItem)
        }
        if(position != RecyclerView.NO_POSITION){
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, itemCount - position)
        }
    }

    fun findItemPosition(childItem: Item, type: Int): Int {
        when (type) {
            TYPE_GROUP -> {
                for (i in 0 until itemCount) {
                    val it = getItem(i)
                    if (it.getType() == TYPE_GROUP && TextUtils.equals(it.uniqueId, childItem.uniqueId)) {
                        return i
                    }
                }
            }
            TYPE_CHILD -> {
                for (i in 0 until itemCount) {
                    val it = getItem(i)
                    if (it.getType() == TYPE_CHILD && TextUtils.equals(it.uniqueId, childItem.uniqueId)) {
                        return i
                    }
                }
            }
        }
        return -1
    }

    private fun findItemParentPosition(childItem: Item): Int {
        for (i in 0 until itemCount) {
            val it = getItem(i)
            if (it.getType() == TYPE_GROUP && TextUtils.equals(it.uniqueId, childItem.parentUniqueId)) {
                return i
            }
        }
        return -1
    }

    private fun removeItemFromList(childItem: Item){
        val iterator: MutableIterator<Item> = getAllItem().iterator()
        while (iterator.hasNext()) {
            val it: Item = iterator.next()
            if (TextUtils.equals(it.uniqueId, childItem.uniqueId)) {
                iterator.remove()
            }
        }
    }

    fun findAllGroupItemPosition(): List<Item> {
        return getAllItem().filter {
            it.getType() == TYPE_GROUP
        }
    }

}