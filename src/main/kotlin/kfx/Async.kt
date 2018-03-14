@file:Suppress("UNCHECKED_CAST", "EXPERIMENTAL_FEATURE_WARNING")

package kfx

import kotlinx.coroutines.experimental.DefaultDispatcher
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import kotlin.coroutines.experimental.CoroutineContext

abstract class Async<T> : Monad<Async<*>, T>, Applicative<Async<*>, T>, FunctorWithContext<Async<*>, T, CoroutineContext> {

    protected abstract val deferred: Deferred<Try<T>>
    protected abstract val defaultContext: CoroutineContext

    override fun <R> map(transform: (T) -> R): Async<R> = map(defaultContext, transform)

    override fun <R> map(context: CoroutineContext, transform: (T) -> R): Async<R> =
            async(context) {
                deferred.await().map(transform)
            }.asAsync(context)


    override fun <R> flatMap(bind: (T) -> Monad<Async<*>, R>): Async<R> =
            async {
                val value = deferred.await()
                when (value) {
                    is Success<T> -> (bind(value.value) as Async<R>).deferred.await()
                    else -> value as Try<R>
                }
            }.asAsync(defaultContext)


    override fun <R> apply(func: Applicative<Async<*>, (T) -> R>): Applicative<Async<*>, R> =
            async {
                val value = deferred.await()
                val function = (func as Async<(T) -> R>).deferred.await()
                value.apply(function)
            }.asAsync(defaultContext)


    fun onComplete(handler: (Try<T>) -> Unit) {
        launch {
            deferred.await().let(handler)
        }
    }

    fun onComplete(context : CoroutineContext, handler: (Try<T>) -> Unit) {
        launch(context) {
            deferred.await().let(handler)
        }
    }

    suspend fun await(): Try<T> = deferred.await()

    companion object {

        operator fun <T> invoke(operation: () -> T): Async<T> = object : Async<T>() {
            override val defaultContext: CoroutineContext = DefaultDispatcher
            override val deferred: Deferred<Try<T>> = async(defaultContext) { Try { operation() } }
        }

        operator fun <T> invoke(context: CoroutineContext, operation: () -> T): Async<T> = object : Async<T>() {
            override val defaultContext: CoroutineContext = context
            override val deferred: Deferred<Try<T>> = async(context) { Try { operation() } }
        }

        fun <T> withTry(operation: () -> Try<T>): Async<T> = object : Async<T>() {
            override val defaultContext: CoroutineContext = DefaultDispatcher
            override val deferred: Deferred<Try<T>> = async(defaultContext) { Unit.asSuccess().flatMap { operation() } }

        }
    }
}

fun <T> Deferred<Try<T>>.asAsync(context: CoroutineContext = DefaultDispatcher) = this.let { self ->
    object : Async<T>() {
        override val deferred: Deferred<Try<T>> = self
        override val defaultContext: CoroutineContext = context
    }
}
