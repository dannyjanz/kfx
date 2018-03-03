@file:Suppress("UNCHECKED_CAST", "EXPERIMENTAL_FEATURE_WARNING")

package kfx

import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch

abstract class Async<T> : Monad<Async<*>, T>, Applicative<Async<*>, T>, Functor<Async<*>, T> {

    protected abstract val deferred: Deferred<Try<T>>

    override fun <R> map(transform: (T) -> R): Async<R> =
            async {
                deferred.await().map(transform)
            }.asAsync()


    override fun <R> flatMap(bind: (T) -> Monad<Async<*>, R>): Async<R> =
            async {
                val value = deferred.await()
                when (value) {
                    is Success<T> -> (bind(value.value) as Async<R>).deferred.await()
                    else -> value as Try<R>
                }
            }.asAsync()


    override fun <R> apply(func: Applicative<Async<*>, (T) -> R>): Applicative<Async<*>, R> =
            async {
                val value = deferred.await()
                val function = (func as Async<(T) -> R>).deferred.await()
                value.apply(function)
            }.asAsync()


    fun onComplete(handler: (Try<T>) -> Unit) {
        launch {
            deferred.await().let(handler)
        }
    }

    suspend fun await(): Try<T> = deferred.await()

    companion object {
        operator fun <T> invoke(operation: () -> T): Async<T> = object : Async<T>() {
            override val deferred: Deferred<Try<T>>
                get() = async { Try { operation() } }
        }
    }
}

fun <T> Deferred<Try<T>>.asAsync() = this.let { self ->
    object : Async<T>() {
        override val deferred: Deferred<Try<T>>
            get() = self
    }
}
