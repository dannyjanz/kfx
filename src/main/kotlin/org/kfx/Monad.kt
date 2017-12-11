package org.kfx

interface Monad<Self, out T> {

    infix fun <R> flatMap(bind: (T) -> Monad<Self, R>): Monad<Self, R>

    infix fun <R> map(transform: (T) -> R): Monad<Self, R>
}
