package org.kfx

interface Functor<Self, T> : Pointable<Self, T>{

    infix fun <R> map(transform: (T) -> R): Functor<Self, R>

}