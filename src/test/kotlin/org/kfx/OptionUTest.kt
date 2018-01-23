package org.kfx

import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.FunSpec

class OptionUTest : FunSpec() {
    init {
        test("Invoking an Option from a value should return a Some of that value") {
            Option(1) shouldBe Some(1)
        }

        test("Invoking an Option from a null value should result in a None") {
            Option(null) shouldBe None
        }

        test("mapping an Option should transform the value and result in a new Option") {
            val original = Option(1)
            val mapped = original map { it.toString() }

            original shouldBe Option(1)
            mapped shouldBe Option("1")
        }

        test("binding an Option to another Option should result in the mapped type if both Options are Some") {
            val optionA = Option(1)
            val optionB = Option("hello")

            optionA flatMap { a -> optionB map { b -> Pair(a, b) } } shouldBe Option(Pair(1, "hello"))
        }

        test("binding an Option to another Option should result in a None if either Option is None") {
            val optionA = Option(1)
            val optionB = None

            optionA flatMap { a -> optionB map { b -> Pair(a, b) } } shouldBe None
            optionB flatMap { b -> optionA map { a -> Pair(a, b) } } shouldBe None
        }

        test("filtering an Option should result in a Some if the Option is defined and the predicate holds") {
            Option(1) filter { it < 2 } shouldBe Option(1)
        }

        test("filtering and Option should result in a None if the predicate does not hold") {
            Option(2) filter { it < 2 } shouldBe None
        }
    }
}