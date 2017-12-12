@file:Suppress("UNCHECKED_CAST")

package org.kfx

interface Option<T> : Monad<Option<*>, T>, Container<T> {

    companion object {
        operator fun <T> invoke(maybe: T?): Option<T> {
            return if (maybe != null) Some(maybe) else None as Option<T>
        }
    }


    override fun <R> map(transform: (T) -> R): Option<R>
    override fun <R> flatMap(bind: (T) -> Monad<Option<*>, R>): Option<R>

}

class Some<T>(private val value: T) : Option<T>, Container<T> by StandardFullContainer(value) {

    override infix fun <R> flatMap(bind: (T) -> Monad<Option<*>, R>): Option<R> = value.let(bind) as Option<R>
    override infix fun <R> map(transform: (T) -> R): Option<R> = value.let(transform).let { Some(it) }

    override fun toString(): String = "Some(${value.toString()})"

    override fun equals(other: Any?): Boolean = other ifNotNull {
        when (it) {
            is Some<*> -> it.value == value
            else -> false
        }
    } ?: false

    override fun hashCode(): Int = value!!.hashCode()

}

sealed class None<T> : Option<T>, StandardEmptyContainer<T> {

    override fun <R> flatMap(bind: (T) -> Monad<Option<*>, R>): Option<R> = None as Option<R>
    override infix fun <R> map(transform: (T) -> R): Option<R> = None as Option<R>

    override fun toString(): String = "None"

    companion object : None<Nothing>() {
        operator fun <T> invoke(): None<T> = this as None<T>
    }

    override fun equals(other: Any?): Boolean = other ifNotNull {
        when (it) {
            is None<*> -> true
            else -> false
        }
    } ?: false

    override fun hashCode(): Int = 0

}

fun <T> T?.asOption(): Option<T> = Option(this)

