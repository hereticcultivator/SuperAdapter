package com.heretic_cultivator.super_adapter.util

import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import com.heretic_cultivator.super_adapter.util.LifecycleAware.Companion.watch

/**
 * 可以感知声明周期的容器，如果你使用的 ViewHolder 继承自 [BaseViewHolder]，直接实现接口即可使用
 * 如果你在其他地方中使用，需要手动调用 [watch] 方法进行监听后才能正常回调
 */
interface LifecycleAware {
    fun onViewAttachedToWindow(lifecycleOwner: LifecycleOwner) {}
    fun onViewDetachedFromWindow(lifecycleOwner: LifecycleOwner) {}

    companion object {

        @JvmStatic
        fun watch(view: View, host: LifecycleAware) {
            view.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
                private var lifecycleOwner: ViewHolderLifecycleOwner? = null

                override fun onViewAttachedToWindow(v: View) {
                    lifecycleOwner = ViewHolderLifecycleOwner().also {
                        it.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
                        host.onViewAttachedToWindow(it)
                    }
                }

                override fun onViewDetachedFromWindow(v: View) {
                    lifecycleOwner?.let {
                        it.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
                        host.onViewDetachedFromWindow(it)
                        it.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
                    }
                    lifecycleOwner = null
                }
            })
        }
    }

    class ViewHolderLifecycleOwner() : LifecycleOwner {
        private val lifecycleRegistry: LifecycleRegistry = LifecycleRegistry(this)

        override val lifecycle
            get() = lifecycleRegistry

        fun handleLifecycleEvent(event: Lifecycle.Event) {
            lifecycleRegistry.handleLifecycleEvent(event)
        }
    }
}