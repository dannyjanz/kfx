package org.kfx

import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.ShouldSpec

class TryUtilUTest : ShouldSpec() {
    init {

        val error = Throwable("too bad :(")
        val otherError = Throwable("woops")

        "partitioning a List of Try" {
            should("return a Pair of Lists with the Successes and Failures") {
                val test = listOf<Try<Int>>(1.asSuccess(), 2.asSuccess(), error.asFailure(), error.asFailure())

                val (successes, failures) = test.partition()

                successes shouldBe listOf(1.asSuccess(), 2.asSuccess())
                failures shouldBe listOf<Try<Int>>(error.asFailure(), error.asFailure())
            }
        }

        "calling sequence on a List of Try" {
            should("be a Success of a List if all Trys are Successes") {

                val list = listOf(1.asSuccess(), 2.asSuccess(), 3.asSuccess())

                list.sequence() shouldBe Success(listOf(1, 2, 3))
            }

            should("be a Failure if any of the items in the List is a Failure") {

                val list = listOf(1.asSuccess(), 2.asSuccess(), error.asFailure<Int>())

                list.sequence() shouldBe Failure(error)

            }

            should("retain the first error") {

                val list = listOf(1.asSuccess(), 2.asSuccess(), error.asFailure<Int>(), otherError.asFailure())

                list.sequence() shouldBe Failure(error)

            }

            should("be a Success in case of an empty list") {

                listOf<Try<Int>>().sequence() shouldBe Success(listOf<Try<Int>>())

            }
        }

        "Traversing a list with Try" {
            should("produce a Success of the resulting List if all operations succeed") {

                listOf(1, 2, 3, 4).traverse(Try, { x -> Try { x * 2 } }) shouldBe Success(listOf(2, 4, 6, 8))
            }

            should("produce a Failure if any operation results in a Failure") {

                listOf(1, 2, 3, 4).traverse(Try, { x -> Try { if (x < 2) x * 2 else throw error } }) shouldBe Failure(error)
            }
        }
    }
}