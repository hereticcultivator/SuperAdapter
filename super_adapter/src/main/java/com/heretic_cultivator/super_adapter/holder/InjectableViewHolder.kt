package com.heretic_cultivator.super_adapter.holder

/**
 * 标记一个 ViewHolder 是可注入的
 */
interface InjectableViewHolder {
    fun setContainer(container: InstanceContainer)
    fun getContainer(): InstanceContainer

    class Delegate : InjectableViewHolder {
        private var instanceContainer: InstanceContainer? = null

        final override fun setContainer(container: InstanceContainer) {
            instanceContainer = container
        }

        final override fun getContainer(): InstanceContainer = instanceContainer!!
    }
}

interface InstanceContainer {
    /**
     * 注入对应类型的实例
     */
    fun <T> inject(type: Class<in T>, instance: T?)

    /**
     * 查找指定类型的实例
     */
    fun <T> findInstance(type: Class<T>): T?
}

/**
 * 注入实例
 */
inline fun <reified T> InstanceContainer.inject(instance: T?) {
    inject(T::class.java, instance)
}

/**
 * 代理实例
 */
inline fun <reified T> InjectableViewHolder.inject(): Lazy<T> = lazy {
    getContainer().findInstance(T::class.java) as T
}
