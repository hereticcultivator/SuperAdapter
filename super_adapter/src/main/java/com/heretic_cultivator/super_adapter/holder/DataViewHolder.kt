package com.heretic_cultivator.super_adapter.holder

import androidx.annotation.CallSuper

/**
 * 绑定具体数据类型的 ViewHolder
 */
interface DataViewHolder<in T : Any> {
    @CallSuper
    fun bindDataInternal(data: T, payloads: List<Any>) {
        onBindData(data, payloads)
    }

    fun onBindData(data: T, payloads: List<Any>) {
        onBindData(data)
    }

    fun onBindData(data: T)
}