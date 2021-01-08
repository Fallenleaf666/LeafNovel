package com.example.leafnovel.bean

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    abstract fun getType():Int
}