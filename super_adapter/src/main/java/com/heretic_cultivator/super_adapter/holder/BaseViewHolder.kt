package com.heretic_cultivator.super_adapter.holder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.heretic_cultivator.super_adapter.util.LifecycleAware

/**
 * 配合 [com.heretic_cultivator.super_adapter.adapter.SuperAdapter] 使用的 ViewHolder 基类，目前提供以下能力:
 *
 * 1. 使用泛型绑定数据类型，通过 [DataViewHolder.onBindData] 回调
 * 2. 支持生命周期感知，业务方实现 [LifecycleAware] 接口后，自动回调 [LifecycleAware.onViewAttachedToWindow] [LifecycleAware.onViewDetachedFromWindow] 方法，
 *    同时提供 [LifecycleOwner] 对象
 * 3. 提供轻量依赖注入，通过 [com.heretic_cultivator.super_adapter.adapter.BaseSuperAdapter.inject] 方法注入，在 ViewHolder 中通过 by [inject] 获取
 */
abstract class BaseViewHolder<T : Any>(view: View) : RecyclerView.ViewHolder(view), DataViewHolder<T>,
    InjectableViewHolder by InjectableViewHolder.Delegate() {

    protected lateinit var data: T

    init {
        if (this is LifecycleAware) {
            LifecycleAware.watch(itemView, this)
        }
    }

    override fun bindDataInternal(data: T, payloads: List<Any>) {
        this.data = data
        super.bindDataInternal(data, payloads)
    }
}