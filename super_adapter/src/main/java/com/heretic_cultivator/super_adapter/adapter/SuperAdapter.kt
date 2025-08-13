package com.heretic_cultivator.super_adapter.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.heretic_cultivator.super_adapter.util.ObjectItemCallback

/**
 * 支持多种类型数据的Adapter
 */
class SuperAdapter(diffCallback: DiffUtil.ItemCallback<*> = ObjectItemCallback<Any>()) : BaseSuperAdapter<Any>(diffCallback as DiffUtil.ItemCallback<Any>) {
    interface ViewHolderFactory<T : Any, VH : ViewHolder> {
        fun createViewHolder(parent: ViewGroup, viewType: Int): VH?

        fun onViewHolderCreated(viewHolder: VH) {}

        fun onBindViewHolder(viewHolder: VH, data: T, payloads: List<Any>)
    }

    override fun submitList(list: List<Any>?) {
        super.submitList(list)
    }

    override fun submitList(list: List<Any>?, commitCallback: Runnable?) {
        super.submitList(list, commitCallback)
    }

    interface ViewHolderFactory2 : ViewHolderFactory<Any, ViewHolder> {
        fun getViewType(data: Any): Int?
    }
}