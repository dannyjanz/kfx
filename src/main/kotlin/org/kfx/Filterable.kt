package org.kfx

interface Filterable<Self, out T> {

    infix fun filter(pred: (T) -> Boolean): Filterable<Self, T>
}