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
