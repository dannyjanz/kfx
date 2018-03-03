package kfx

interface Lift<F> {

    fun <T, R> lift(func: (T) -> R): (Functor<F, T>) -> Functor<F, R> = { a: Functor<F, T> -> a.map(func) }

}


fun <T, R, F : Functor<F, *>> Functor<F, T>.lift(func: (T) -> R): (Functor<F, T>) -> Functor<F, R> = { a: Functor<F, T> -> a.map(func) }

