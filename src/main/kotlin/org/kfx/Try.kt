@file:Suppress("UNCHECKED_CAST")

package org.kfx

interface Try<T> : Monad<Try<*>, T>, Container<T> {

    companion object {

        operator fun <T> invoke(func: () -> T): Try<T> {
            return try {
                Success(func())
            } catch (e: Throwable) {
                Failure(e) as Failure<T>
            }
        }
    }

    fun isSuccess(): Boolean

    override fun <R> map(transform: (T) -> R): Try<R>
    override fun <R> flatMap(bind: (T) -> Monad<Try<*>, R>): Try<R>
}

class Success<T>(val value: T) : Try<T>, Container<T> by StandardFullContainer(value) {

    override fun <R> flatMap(bind: (T) -> Monad<Try<*>, R>): Try<R> = try {
        value.let(bind)
    } catch (e: Throwable) {
        Failure(e)
    } as Try<R>

    override fun <R> map(transform: (T) -> R): Try<R> = Try { value.let(transform) }

    override fun isSuccess(): Boolean = true

    override fun equals(other: Any?): Boolean = other ifNotNull {
        when (it) {
            is Success<*> -> it.value == value
            else -> false
        }
    } ?: false

    override fun hashCode(): Int = value?.hashCode() ?: 0
}

class Failure<T> private constructor(val error: Throwable) : Try<T>, StandardEmptyContainer<T> {

    override fun <R> flatMap(bind: (T) -> Monad<Try<*>, R>): Try<R> = Failure(error)
    override fun <R> map(transform: (T) -> R): Try<R> = Failure(error)

    override fun isSuccess(): Boolean = false

    override fun equals(other: Any?): Boolean = other ifNotNull {
        when (it) {
            is Failure<*> -> it.error == error
            else -> false
        }
    } ?: false

    override fun hashCode(): Int = error.hashCode()

    companion object {
        operator fun invoke(error: Throwable): Failure<Nothing> = Failure(error)
    }

}