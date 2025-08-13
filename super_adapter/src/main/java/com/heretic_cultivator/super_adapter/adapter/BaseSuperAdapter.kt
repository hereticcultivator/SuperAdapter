package com.heretic_cultivator.super_adapter.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.heretic_cultivator.super_adapter.util.SimpleInstanceContainer
import com.heretic_cultivator.super_adapter.holder.InjectableViewHolder
import com.heretic_cultivator.super_adapter.holder.InstanceContainer
import java.lang.reflect.Modifier
import kotlin.collections.set

/**
 * 支持多类型数据和 ViewHolder
 */
abstract class BaseSuperAdapter<T: Any>(diffCallback: DiffUtil.ItemCallback<T>) : ListAdapter<T, ViewHolder>(diffCallback) {

    companion object {

        fun Class<*>.asViewType() = hashCode()
    }

    private val holderFactories: HashMap<Int, SuperAdapter.ViewHolderFactory<Any, ViewHolder>> = HashMap()

    private var interceptViewHolderFactory: SuperAdapter.ViewHolderFactory2? = null

    private val instanceContainer: InstanceContainer = SimpleInstanceContainer()

    override fun getItemViewType(position: Int): Int {

        val item = getItem(position) ?: return 0

        return getItemViewType(item)
    }

    internal fun getItemViewType(item: T): Int {
        return interceptViewHolderFactory?.getViewType(item) ?: item::class.javaObjectType.asViewType()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return createViewHolderForType(parent, viewType)
    }

    internal fun createViewHolderForType(parent: ViewGroup, viewType: Int): ViewHolder {
        var viewHolder: ViewHolder? = interceptViewHolderFactory?.createViewHolder(parent, viewType)

        if (viewHolder == null) {
            val factory = getViewHolderFactory<T, ViewHolder>(viewType)

            //create
            viewHolder = factory.createViewHolder(parent, viewType)
                ?: throw kotlin.IllegalStateException("ViewHolderFactory: $factory must return a ViewHolder")

            //onCreate callback
            factory.onViewHolderCreated(viewHolder)
        }

        //inject
        if (viewHolder is InjectableViewHolder) {
            viewHolder.setContainer(instanceContainer)
        }

        return viewHolder
    }

    internal fun hasViewHolderFactoryForType(viewType: Int): Boolean {
        return holderFactories.containsKey(viewType)
    }

    private fun <T : Any, VH : ViewHolder> getViewHolderFactory(viewType: Int): SuperAdapter.ViewHolderFactory<T, VH> {
        @Suppress("UNCHECKED_CAST")
        return holderFactories.get(viewType) as? SuperAdapter.ViewHolderFactory<T, VH>
            ?: throw kotlin.IllegalStateException("no holder factory for viewType: $viewType")
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data: T = getItem(position) ?: return
        bindDataToViewHolder(holder, data)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        val data: T = getItem(position) ?: return
        bindDataToViewHolder(holder, data, payloads)
    }

    internal fun bindDataToViewHolder(holder: ViewHolder, data: T, payloads: List<Any> = emptyList()) {
        if (interceptViewHolderFactory?.getViewType(data) != null) {
            interceptViewHolderFactory?.onBindViewHolder(holder, data, payloads)
        } else {
            val factory: SuperAdapter.ViewHolderFactory<T, ViewHolder> = getViewHolderFactory(getItemViewType(data))
            factory.onBindViewHolder(holder, data, payloads)
        }
    }

    fun <T : Any, VH : ViewHolder> registerViewHolder(
        dataClass: Class<T>,
        viewHolderFactory: SuperAdapter.ViewHolderFactory<in T, VH>,
    ): BaseSuperAdapter<*> {
        if (dataClass.isPrimitive) {
            throw kotlin.IllegalArgumentException("primitive type is not supported: $dataClass")
        }

        if (dataClass.isInterface) {
            throw kotlin.IllegalArgumentException("interface type is not supported: $dataClass")
        }

        if (Modifier.isAbstract(dataClass.modifiers)) {
            throw kotlin.IllegalArgumentException("abstract type is not supported: $dataClass")
        }

        val viewType = dataClass.asViewType()
        @Suppress("UNCHECKED_CAST")
        holderFactories[viewType] = viewHolderFactory as SuperAdapter.ViewHolderFactory<Any, ViewHolder>
        return this
    }

    inline fun <reified T: Any> inject(instance: T?): BaseSuperAdapter<*> {
        return inject(T::class.java, instance)
    }

    fun <T: Any> inject(type: Class<in T>, instance: T?): BaseSuperAdapter<*> {
        instanceContainer.inject(type, instance)
        return this
    }

    fun setInterceptViewHolderFactory(viewHolderFactory: SuperAdapter.ViewHolderFactory2?): BaseSuperAdapter<*> {
        this.interceptViewHolderFactory = viewHolderFactory
        return this
    }

}