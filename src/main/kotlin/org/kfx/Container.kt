package org.kfx

interface Container<T> {

    infix fun forEach(f: (T) -> Unit)
    fun getOr(func: () -> T): T
    fun get(): T?
    fun asList(): List<T>
}

class StandardFullContainer<T>(private val value: T) : Container<T> {

    override fun get(): T? = value
    override fun getOr(func: () -> T): T = value
    override fun asList(): List<T> = listOf(value)
    override fun forEach(f: (T) -> Unit) = f(value)
}

interface StandardEmptyContainer<T> : Container<T> {

    override fun get(): T? = null
    override fun getOr(func: () -> T): T = func()
    override fun asList(): List<T> = listOf()
    override fun forEach(f: (T) -> Unit) = Unit
}
