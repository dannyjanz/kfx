package kfx

interface Functor<Self, out T>{

    infix fun <R> map(transform: (T) -> R): Functor<Self, R>

}