package com.heretic_cultivator.super_adapter.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.heretic_cultivator.super_adapter.holder.ViewBindingViewHolder
import java.lang.reflect.Method
import java.util.concurrent.ConcurrentHashMap

private val inflateMethodCache: MutableMap<Class<*>, Method> = ConcurrentHashMap()

@Suppress("UNCHECKED_CAST")
private fun <T : ViewBinding> Class<T>.inflate(inflater: LayoutInflater, parent: ViewGroup, attachToParent: Boolean): T {
    val method = inflateMethodCache.getOrPut(this) {
        getMethod("inflate", LayoutInflater::class.java, ViewGroup::class.java, Boolean::class.java).apply {
            isAccessible = true
        }
    }
    return method.invoke(null, inflater, parent, attachToParent) as T
}

fun <T : ViewBindingViewHolder<*, *>> Class<T>.newInstance(binding: ViewBinding): T {
    val constructor = getConstructor(binding::class.java).apply {
        isAccessible = true
    }
    return constructor.newInstance(binding)
}

@JvmOverloads
inline fun <reified D : Any, reified B : ViewBinding, reified VH : ViewBindingViewHolder<in D, in B>> SuperAdapter.registerViewHolder(
    viewHolderClass: Class<out VH>,
    noinline onBindViewHolder: ((viewHolder: VH, data: D, payloads: List<Any>) -> Unit)? = null,
    noinline onViewHolderCreated: ((viewHolder: VH) -> Unit)? = null,
): SuperAdapter {
    return registerViewHolder(D::class.javaObjectType, viewHolderClass, onBindViewHolder, onViewHolderCreated)
}

@JvmOverloads
inline fun <D : Any, reified B : ViewBinding, reified VH : ViewBindingViewHolder<in D, in B>> SuperAdapter.registerViewHolder(
    dataClass: Class<out D>,
    viewHolderClass: Class<out VH>,
    noinline onBindViewHolder: ((viewHolder: VH, data: D, payloads: List<Any>) -> Unit)? = null,
    noinline onViewHolderCreated: ((viewHolder: VH) -> Unit)? = null,
): SuperAdapter {
    return registerViewHolder(dataClass, BindingViewHolderFactory(B::class.java, viewHolderClass, onBindViewHolder, onViewHolderCreated)) as SuperAdapter
}

class BindingViewHolderFactory<D : Any, B : ViewBinding, VH : ViewBindingViewHolder<in D, in B>>(
    private val bindClass: Class<B>,
    private val viewHolderClass: Class<out VH>,
    private val onBindViewHolder: ((viewHolder: VH, data: D, payloads: List<Any>) -> Unit)? = null,
    private val onViewHolderCreated: ((viewHolder: VH) -> Unit)? = null,
) : SuperAdapter.ViewHolderFactory<D, VH> {

    override fun createViewHolder(parent: ViewGroup, viewType: Int): VH? {
        val inflater = LayoutInflater.from(parent.context)
        val binding = bindClass.inflate(inflater, parent, false)
        return viewHolderClass.newInstance(binding)
    }

    override fun onBindViewHolder(viewHolder: VH, data: D, payloads: List<Any>) {
        viewHolder.bindDataInternal(data, payloads)
        onBindViewHolder?.invoke(viewHolder, data, payloads)
    }

    override fun onViewHolderCreated(viewHolder: VH) {
        onViewHolderCreated?.invoke(viewHolder)
    }
}

fun SuperAdapter.setupWithRecyclerView(recyclerView: RecyclerView) {
    recyclerView.adapter = this
}