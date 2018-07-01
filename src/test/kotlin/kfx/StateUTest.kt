package kfx

import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.ShouldSpec

class StateUTest : ShouldSpec() {
    init {

        class AddingMachine(val initialState: Int) {
            fun operate(more : Int): Pair<Int, AddingMachine> {
                val newState = initialState + more
                return Pair(newState, AddingMachine(newState))
            }
        }


        "State as a Functor" {
            should("allow to transform the result of the state run") {

                val addOne = State { m: AddingMachine -> m.operate(1) }
                val testMachine = AddingMachine(0)

                val transformingOperation = addOne.map { i -> i.toString() }

                val result = addOne.run(testMachine)
                val transformedResult = transformingOperation.run(testMachine)

                result.first shouldBe 1
                result.second.initialState shouldBe 1

                transformedResult.first shouldBe "1"
                transformedResult.second.initialState shouldBe 1
            }
        }

        "State as a Monad" {
            should("allow to chain operations on the state") {

                val addOne = State { m: AddingMachine -> m.operate(1) }
                val add = {i : Int -> State { m: AddingMachine -> m.operate(i) }}
                val testMachine = AddingMachine(0)


                val result = addOne.flatMap { add(2) }.flatMap { add(3) }.run(testMachine)
                val other = addOne.flatMap { addOne }.run(testMachine)

                other.first shouldBe 2
                other.second.initialState shouldBe 2

                result.first shouldBe 6
                result.second.initialState shouldBe 6

            }
        }


    }
}

