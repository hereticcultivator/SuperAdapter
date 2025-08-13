package com.heretic_cultivator.super_adapter.holder

import androidx.viewbinding.ViewBinding

/**
 * 绑定 ViewBinding 的 ViewHolder
 */
abstract class ViewBindingViewHolder<T : Any, V : ViewBinding>(val binding: V) : BaseViewHolder<T>(binding.root)