package org.kfx

interface Functor<Self, out T> {

    fun <R> map(transformation: (T) -> R): Functor<Self, R>
}

