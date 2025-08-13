package com.heretic_cultivator.super_adapter.util

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil

/**
 * 默认的 DiffUtil.ItemCallback，仅使用 equals 比较对象是否相等
 */
open class ObjectItemCallback<T : Any> : DiffUtil.ItemCallback<T>() {
    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem == newItem
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem == newItem
    }
}