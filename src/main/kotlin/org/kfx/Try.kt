@file:Suppress("UNCHECKED_CAST")

package org.kfx

interface Try<T> : Monad<Try<*>, T>, Filterable<Try<*>, T>, Container<T> {

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
    fun isFailure(): Boolean

    fun <R> recover(func: (Throwable) -> R): Try<R>
    fun <R> recoverWith(func: (Throwable) -> Try<R>): Try<R>

    override fun <R> map(transform: (T) -> R): Try<R>
    override fun <R> flatMap(bind: (T) -> Monad<Try<*>, R>): Try<R>
    override fun filter(pred: (T) -> Boolean): Try<T>
}

class Success<T>(val value: T) : Try<T>, Container<T> by StandardFullContainer(value) {


    override fun <R> map(transform: (T) -> R): Try<R> = Try { value.let(transform) }

    override fun <R> flatMap(bind: (T) -> Monad<Try<*>, R>): Try<R> = try {
        value.let(bind)
    } catch (e: Throwable) {
        Failure(e)
    } as Try<R>

    override fun filter(pred: (T) -> Boolean): Try<T> =
            if (pred(value)) this
            else Failure(NoSuchElementException("Predicate does not hold for $value")) as Try<T>

    override fun <R> recover(func: (Throwable) -> R): Try<R> = this as Try<R>
    override fun <R> recoverWith(func: (Throwable) -> Try<R>): Try<R> = this as Try<R>

    override fun isSuccess(): Boolean = true
    override fun isFailure(): Boolean = false

    override fun equals(other: Any?): Boolean = other ifNotNull {
        when (it) {
            is Success<*> -> it.value == value
            else -> false
        }
    } ?: false

    override fun hashCode(): Int = value?.hashCode() ?: 0

    override fun toString(): String = "Success(${value.toString()})"
}

class Failure<T> private constructor(val error: Throwable) : Try<T>, StandardEmptyContainer<T> {

    override fun <R> map(transform: (T) -> R): Try<R> = this as Try<R>
    override fun <R> flatMap(bind: (T) -> Monad<Try<*>, R>): Try<R> = this as Try<R>
    override fun filter(pred: (T) -> Boolean): Try<T> = this

    override fun isSuccess(): Boolean = false
    override fun isFailure(): Boolean = true

    override fun <R> recover(func: (Throwable) -> R): Try<R> = Try { func(error) }
    override fun <R> recoverWith(func: (Throwable) -> Try<R>): Try<R> = try {
        func(error)
    } catch (e: Throwable) {
        Failure(e)
    }

    override fun equals(other: Any?): Boolean = other ifNotNull {
        when (it) {
            is Failure<*> -> it.error == error
            else -> false
        }
    } ?: false

    override fun hashCode(): Int = error.hashCode()

    override fun toString(): String = "Failure($error)"

    companion object {
        operator fun invoke(error: Throwable): Failure<Nothing> = Failure(error)
    }

}

fun <T> T.asSuccess(): Success<T> = Success(this)
fun <T> Throwable.asFailure(): Failure<T> = Failure(this) as Failure<T>