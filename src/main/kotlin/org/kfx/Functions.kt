package org.kfx

fun <T> not(func: (T) -> Boolean) = { arg: T -> !func(arg) }

infix fun <T> Function1<T, Boolean>.and(other: (T) -> Boolean) = { arg: T ->
    if (this(arg)) other(arg) else false
}

infix fun <T> Function1<T, Boolean>.or(other: (T) -> Boolean) = { arg: T ->
    if (this(arg)) true else other(arg)
}

infix fun <T, R, X> Function1<T, R>.andThen(other: (R) -> X): (T) -> X = { arg: T -> other(this(arg)) }


infix fun <T, R> T?.ifNotNull(func: (T) -> R?): R? = if (this != null) func(this) else null

infix fun <T, R, X> Function1<T, R?>.andIfDefined(other: (R) -> X?): (T) -> X? = { arg: T ->
    this(arg) ifNotNull { other(it) }
}


