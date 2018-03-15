package kfx

interface FunctorWithContext<Self, out T, in C> : Functor<Self, T> {

    fun <R> map(context: C, transform: (T) -> R): FunctorWithContext<Self, R, C>

}