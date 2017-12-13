package org.kfx

import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.FunSpec

class TryUTest : FunSpec() {
    init {

        test("invoking a Try with a succeeding operation should result in a Success of the result") {

            Try { 1 + 1 } shouldBe Success(2)
        }

        test("invoking a Try with a failing operation should result in a Failure with the occurred exception") {

            val error = Throwable("too bad :(")

            Try { throw error } shouldBe Failure(error)
        }

        test("mapping the value of a Try should result in a new Try with the mapped value") {

            val original = Try { 1 }
            val mapped = original map { it.toString() }

            original shouldBe Success(1)
            mapped shouldBe Success("1")
        }

        test("an error occurring in mapping should be caught by the Try and result in a Failure") {

            val error = Throwable("woops..")

            Try { 1 } map { throw error } shouldBe Failure(error)
        }

        test("binding a Try to another Try should result in a Success of the result if both are a Success") {

            val try1 = Try { 1 }
            val try2 = Try { "1" }

            try1 flatMap { a -> try2 map { b -> Pair(a, b) } } shouldBe Success(Pair(1, "1"))
            try2 flatMap { a -> try1 map { b -> Pair(a, b) } } shouldBe Success(Pair("1", 1))

        }

        test("binding a Try to another Try should result in a Failure if any of them is Failure") {

            val error = Throwable("too bad :(")
            val try1 = Try { 1 }
            val try2 = Failure(error)

            try1 flatMap { a -> try2 map { b -> Pair(a, b) } } shouldBe Failure(error)
            try2 flatMap { a -> try1 map { b -> Pair(a, b) } } shouldBe Failure(error)
        }

        test("an error occurring in the binding should result in a Failure") {

            val error = Throwable("woops..")

            Try { 1 } flatMap { throw error; Try {"1"} } shouldBe Failure(error)

        }
    }
}