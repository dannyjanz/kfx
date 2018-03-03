@file:Suppress("UNCHECKED_CAST")

package kfx

interface Reader<In, T> : Monad<Reader<In, *>, T>, Functor<Reader<*, *>, T>, Applicative<Reader<In, *>, T> {

    val run: (In) -> T

    operator fun invoke(arg: In): T = run(arg)

    override fun <R> flatMap(bind: (T) -> Monad<Reader<In, *>, R>): Reader<In, R> = Reader { arg: In ->
        (bind(run(arg)) as Reader<In, R>).run(arg)
    }

    override fun <R> map(transform: (T) -> R): Reader<In, R> = Reader { arg: In ->
        run(arg).let(transform)
    }

    override fun <R> apply(func: Applicative<Reader<In, *>, (T) -> R>): Reader<In, R> = Reader { arg: In ->
        val result = run(arg)
        val function = (func as Reader<In, (T) -> R>)(arg)
        function(result)
    }

    companion object {
        operator fun <I, T> invoke(func: (I) -> T): Reader<I, T> = object : Reader<I, T> {
            override val run: (I) -> T = func
        }
    }

}

fun <I> askFor() = Reader<I, I> { it }
