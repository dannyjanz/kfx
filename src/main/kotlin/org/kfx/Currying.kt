package org.kfx

operator fun <A, B, Z> Function2<A, B, Z>.invoke(a: A): (B) -> Z = curry(this)(a)

operator fun <A, B, C, Z> Function3<A, B, C, Z>.invoke(a: A): (B) -> (C) -> Z = curry(this)(a)

operator fun <A, B, C, D, Z> Function4<A, B, C, D, Z>.invoke(a: A): (B) -> (C) -> (D) -> Z = curry(this)(a)


fun <A, B, Z> curry(func: (A, B) -> Z): (A) -> (B) -> Z = { a: A -> { b: B -> func(a, b) } }

fun <A, B, C, Z> curry(func: (A, B, C) -> Z): (A) -> (B) -> (C) -> Z = { a: A -> { b: B -> { c: C -> func(a, b, c) } } }

fun <A, B, C, D, Z> curry(func: (A, B, C, D) -> Z): (A) -> (B) -> (C) -> (D) -> Z = { a: A -> { b: B -> { c: C -> { d: D -> func(a, b, c, d) } } } }
