package org.kfx

interface Functor<Self, T> : Pointable<Self, T> {

    infix fun <R> map(transform: (T) -> R): Functor<Self, R>

}


//    fun <R> lift(function: (T) -> R): (Functor<Self, T>) -> Functor<Self, R>


/*
consider the following

scala> Functor[List].lift {(_: Int) * 2}
res45: List[Int] => List[Int] = <function1>




 */

