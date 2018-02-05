@file:Suppress("UNREACHABLE_CODE")

package org.kfx

import io.kotlintest.matchers.beOfType
import io.kotlintest.matchers.should
import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.FunSpec

class TryUTest : FunSpec() {
    init {

        val error = Throwable("too bad :(")
        val otherError = IllegalAccessError("You shouldn't be here..")

        test("invoking a Try with a succeeding operation should result in a Success of the result") {

            Try { 1 + 1 } shouldBe Success(2)
        }

        test("invoking a Try with a failing operation should result in a Failure with the occurred exception") {

            Try { throw error } shouldBe Failure(error)
        }

        test("mapping the value of a Try should result in a new Try with the mapped value") {

            val original = Try { 1 }
            val mapped = original map { it.toString() }

            original shouldBe Success(1)
            mapped shouldBe Success("1")
        }

        test("an error occurring in mapping should be caught by the Try and result in a Failure") {

            Try { 1 } map { throw error } shouldBe Failure(error)
        }

        test("binding a Try to another Try should result in a Success of the result if both are a Success") {

            val try1 = Try { 1 }
            val try2 = Try { "1" }

            try1 flatMap { a -> try2 map { b -> Pair(a, b) } } shouldBe Success(Pair(1, "1"))
            try2 flatMap { a -> try1 map { b -> Pair(a, b) } } shouldBe Success(Pair("1", 1))

        }

        test("binding a Try to another Try should result in a Failure if any of them is Failure") {

            val try1 = Try { 1 }
            val try2 = Failure(error)

            try1 flatMap { a -> try2 map { b -> Pair(a, b) } } shouldBe Failure(error)
            try2 flatMap { a -> try1 map { b -> Pair(a, b) } } shouldBe Failure(error)
        }

        test("an error occurring in the binding should result in a Failure") {

            Try { 1 } flatMap { throw error; Try { "1" } } shouldBe Failure(error)

        }

        test("filtering a Try should result in a Success if the original was a Success and the predicate is satisfied") {

            Try { 2 } filter { it < 3 } shouldBe Success(2)
        }

        test("filtering a Try should result in a Failure if the predicate does not hold or the original is a Failure") {

            Try { 2 } filter { it == 3 } should beOfType<Failure<*>>()
            Failure(error) filter { true } shouldBe Failure(error)
        }

        test("recover should transform the error in a Failure into a new Try") {

            Failure(error).recover { 10 } shouldBe Success(10)
            Failure(error).recover { throw otherError } shouldBe Failure(otherError)
        }

        test("recover should return 'this' in case it is a Success") {

            Success(1).recover { 2 } shouldBe Success(1)
            Success(1).recover { throw error } shouldBe Success(1)
        }

        test("binding a Try to a Failure via recoverWith should result in a new Try with the transformation of the error") {

            Failure(error).recoverWith { Success(2) } shouldBe Success(2)
            Failure(error).recoverWith { Failure(otherError) } shouldBe Failure(otherError)
        }

        test("recoverWith should return 'this' in case it was a Success") {

            Success(1).recoverWith { Success(2) } shouldBe Success(1)
            Success(1).recoverWith { Failure(error) } shouldBe Success(1)
        }
    }
}