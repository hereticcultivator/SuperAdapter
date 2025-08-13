package com.heretic_cultivator.super_adapter.util

import com.heretic_cultivator.super_adapter.holder.InstanceContainer

/**
 * 一个简单的依赖注入容器
 */
class SimpleInstanceContainer : InstanceContainer {
    private val instanceMap = LinkedHashMap<Class<*>, Any?>()

    override fun <T> inject(type: Class<in T>, instance: T?) {
        instanceMap[type] = instance
    }

    override fun <T> findInstance(type: Class<T>): T? {
        val instanceOf = instanceMap[type]
        if (instanceOf != null) {
            @Suppress("UNCHECKED_CAST")
            return instanceOf as T
        }

        instanceMap.entries.reversed().forEach { (key, value) ->
            if (type.isAssignableFrom(key) && value != null) {
                @Suppress("UNCHECKED_CAST")
                return value as T
            }
        }
        return null
    }
}