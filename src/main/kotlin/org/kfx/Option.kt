@file:Suppress("UNCHECKED_CAST")

package org.kfx

interface Option<T> : Monad<Option<*>, T>, Functor<Option<*>, T>, Applicative<Option<*>, T>, Filterable<Option<*>, T>, Container<T> {

    companion object : Point<Option<*>> {
        override fun <T> point(init: () -> T): Option<T> = Some(init())

        operator fun <T> invoke(maybe: T?): Option<T> {
            return if (maybe != null) Some(maybe) else None as Option<T>
        }
    }

    override fun <R> map(transform: (T) -> R): Option<R>
    override fun <R> flatMap(bind: (T) -> Monad<Option<*>, R>): Option<R>
    override fun <R> apply(func: Applicative<Option<*>, (T) -> R>): Option<R>
    override fun filter(pred: (T) -> Boolean): Option<T>

}

class Some<T>(val value: T) : Option<T>, Container<T> by StandardFullContainer(value) {

    override infix fun <R> flatMap(bind: (T) -> Monad<Option<*>, R>): Option<R> = value.let(bind) as Option<R>
    override infix fun <R> map(transform: (T) -> R): Option<R> = value.let(transform).let { Some(it) }
    override fun filter(pred: (T) -> Boolean): Option<T> = if (pred(value)) Some(value) else None as Option<T>

    override fun <R> apply(func: Applicative<Option<*>, (T) -> R>): Option<R> = (func as Option<*>).let {
        when (it) {
            is Some<*> -> Some((it.value as (T) -> R).invoke(value))
            else -> None()
        }
    }

    override fun toString(): String = "Some(${value.toString()})"

    override fun equals(other: Any?): Boolean = other.let {
        when (it) {
            is Some<*> -> it.value == value
            else -> false
        }
    }

    override fun hashCode(): Int = value?.hashCode() ?: 0

}

sealed class None<T> : Option<T>, StandardEmptyContainer<T> {

    override fun <R> flatMap(bind: (T) -> Monad<Option<*>, R>): Option<R> = None as Option<R>
    override infix fun <R> map(transform: (T) -> R): Option<R> = None as Option<R>
    override fun filter(pred: (T) -> Boolean): Option<T> = None as Option<T>
    override fun <R> apply(func: Applicative<Option<*>, (T) -> R>): Option<R> = None as Option<R>

    override fun toString(): String = "None"

    companion object : None<Nothing>() {
        operator fun <T> invoke(): None<T> = this as None<T>
    }

    override fun equals(other: Any?): Boolean = other.let {
        when (it) {
            is None<*> -> true
            else -> false
        }
    }

    override fun hashCode(): Int = 0

}

fun <T> T?.asOption(): Option<T> = Option(this)

