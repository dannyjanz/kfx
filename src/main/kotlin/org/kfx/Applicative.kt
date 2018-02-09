package org.kfx

interface Applicative<Self, T> : Pointable<Self, T> {

    infix fun <R> apply(func: Applicative<Self, (T) -> R>): Applicative<Self, R>

}