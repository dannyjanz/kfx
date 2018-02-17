@file:Suppress("UNCHECKED_CAST")

package org.kfx

fun <T> List<Try<T>>.partition(): Pair<List<Success<T>>, List<Failure<T>>> =
        this.partition { it.isSuccess() } as Pair<List<Success<T>>, List<Failure<T>>>


fun <T> List<Try<T>>.sequence(): Try<List<T>> {
    val (success, failures) = this.partition()
    return when {
        failures.isEmpty() and success.isNotEmpty() -> Success(success.map { it.value })
        failures.isNotEmpty() -> Failure(failures.first().error) as Try<List<T>>
        else -> Success(listOf())
    }
}

object TryTraverse : TraverseWith<Try<*>> {
    override fun <T, R> traverse(list: List<T>, function: (T) -> Monad<Try<*>, R>): Try<List<R>> {
        var newList = listOf<R>()
        for (e in list) {
            val result = function(e)
            when (result) {
                is Success<*> -> newList += result.value as R
                is Failure<*> -> return result as Failure<List<R>>
            }
        }
        return Success(newList)
    }
}