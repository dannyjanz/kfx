package org.kfx

import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.ShouldSpec

class ReaderUTest : ShouldSpec() {
    init {

        val plus2 = Reader { x: Int -> x + 2 }

        "Reader as a Functor" {
            should("transform the output of the stored function and return a Reader of a new function") {

                val plus2AsString = plus2.map { it.toString() }

                plus2(1) shouldBe 3
                plus2AsString(1) shouldBe "3"

            }
        }

        "Reader as a Monad" {
            should("bind the argument of the Reader to a function returning a new Reader depending on that argument " +
                    "and return a Reader with a new function applying both Readers sequentially") {

                val timesY = { y: Int -> Reader { x: Int -> x * y } }

                val plus2TimesResult = plus2.flatMap { timesY(it) }

                plus2(3) shouldBe 5

                plus2TimesResult(1) shouldBe 3
                plus2TimesResult(2) shouldBe 8
                plus2TimesResult(3) shouldBe 15

            }
        }


    }
}