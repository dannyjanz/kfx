package org.kfx

interface Point<Self, T> {

    fun point(init: () -> T): Point<Self, T>

}