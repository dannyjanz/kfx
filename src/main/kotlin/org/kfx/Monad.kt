package org.kfx

interface Monad<Self, T> : Pointable<Self, T> {

    infix fun <R> flatMap(bind: (T) -> Monad<Self, R>): Monad<Self, R>

}

