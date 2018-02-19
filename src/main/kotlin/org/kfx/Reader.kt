@file:Suppress("UNCHECKED_CAST")

package org.kfx

interface Reader<in In, T> : Monad<Reader<*, *>, T>, Functor<Reader<*, *>, T> {

    val run: (In) -> T

    operator fun invoke(arg: In): T = run(arg)

    override fun <R> flatMap(bind: (T) -> Monad<Reader<*, *>, R>): Reader<In, R> = Reader { arg: In ->
        (bind(run(arg)) as Reader<In, R>).run(arg)
    }

    override fun <R> map(transform: (T) -> R): Reader<In, R> = Reader { arg: In ->
        run(arg).let(transform)
    }

    companion object {
        operator fun <I, T> invoke(func: (I) -> T): Reader<I, T> = object : Reader<I, T> {
            override val run: (I) -> T = func
        }
    }

}

fun <I> askFor() = Reader<I, I> { it }
