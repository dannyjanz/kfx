package org.kfx

import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.ShouldSpec


interface Context {
    fun test(): String
}

class RealContext : Context {
    override fun test() = "Real Context"
}

class MockContext : Context {
    override fun test() = "Mock Context"
}

class ReaderUTest : ShouldSpec() {
    init {

        val plus2 = Reader { x: Int -> x + 2 }

        val loadStuff = Reader { c: Context -> "loading with ${c.test()}" }
        val bindMe = { s: String -> Reader { c: Context -> s + " and then dumping " + c.test() } }

        "Reader as a Functor" {
            should("transform the output of the stored function and return a Reader of a new function") {

                val plus2AsString = plus2.map { it.toString() }

                plus2(1) shouldBe 3
                plus2AsString(1) shouldBe "3"

            }
        }

        "Reader as a Monad" {
            should("bind the output of a Readers function to a function producing a new Reader with the same input type " +
                    "and return a Reader with a new function applying both Readers function sequentially") {

                val timesY = { y: Int -> Reader { x: Int -> x * y } }

                val plus2TimesResult = plus2.flatMap { timesY(it) }

                plus2(3) shouldBe 5

                plus2TimesResult(1) shouldBe 3
                plus2TimesResult(2) shouldBe 8
                plus2TimesResult(3) shouldBe 15

                loadStuff.flatMap(bindMe)(RealContext()) shouldBe "loading with Real Context and then dumping Real Context"
                loadStuff.flatMap(bindMe)(MockContext()) shouldBe "loading with Mock Context and then dumping Mock Context"

            }
        }


    }
}