package kfx

interface Pointable<Self, T>

interface Point<Self> {

    fun <T>point(init: () -> T): Pointable<Self, T>

}
