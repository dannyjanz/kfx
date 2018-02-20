package org.kfx

interface Applicative<Self, T> {

    infix fun <R> apply(func: Applicative<Self, (T) -> R>): Applicative<Self, R>

}