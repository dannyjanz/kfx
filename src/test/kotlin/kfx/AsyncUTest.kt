package kfx

import io.kotlintest.Duration
import io.kotlintest.eventually
import io.kotlintest.matchers.containsAll
import io.kotlintest.matchers.containsInOrder
import io.kotlintest.matchers.should
import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.ShouldSpec
import kotlinx.coroutines.experimental.runBlocking
import java.lang.Error
import java.util.concurrent.TimeUnit

class AsyncUTest : ShouldSpec() {
    init {

        val soon = { func: () -> Unit -> eventually(Duration(200, TimeUnit.MILLISECONDS), Error::class.java, func) }

        val expectedExecutionOrder = listOf("A", "B")
        var executions = listOf<String>()

        val add = { a: Int -> { b: Int -> a + b } }

        "Async" {
            should("execute a given operation parallel to the invoking Thread") {

                val asyncResult = Async {
                    Thread.sleep(50)
                    executions += "B"
                    1 + 1
                }

                executions += "A"

                executions should containsAll("A")

                soon {
                    executions should containsInOrder(expectedExecutionOrder)
                }

                runBlocking {
                    asyncResult.await() shouldBe Success(2)
                }

            }

            should("execute all functions passed to onComplete, once the result is available") {

            }
        }

        "Async as a Functor" {
            should("transform the value in Async once it is available, resulting in a new Async") {

                val original = Async { 1 + 1 }

                val mapped = original.map {
                    Thread.sleep(50)
                    executions += "B"
                    (it * 10).toString()
                }

                executions += "A"

                soon { executions should containsInOrder(expectedExecutionOrder) }

                runBlocking {
                    original.await() shouldBe Success(2)
                    mapped.await() shouldBe Success("20")
                }

            }
        }

        "Async as an Applicative" {
            should("apply the function wrapped in an Async to the wrapped value once both " +
                    "are available and result in a new Async containing the result") {

                val asyncA = Async { 1 + 1 }
                val asyncB = Async { 2 * 10 }

                val asyncAplusB = asyncB.apply(asyncA.map(add))

                runBlocking {
                    asyncAplusB.await() shouldBe Success(22)
                }

            }
        }

        "Async as a Monad" {

            should("bind the wrapped value to the given function resulting in another Async " +
                    "and return the new Async that will contain the result") {

                val asyncA = Async { 1 + 1 }
                val asyncB = Async { 2 * 10 }

                val asyncAplusB = asyncA.flatMap { asyncB.map(add(it)) }

                val asyncAtimes10 = asyncA.flatMap { a -> Async { a * 10 } }

                runBlocking {
                    asyncAplusB.await() shouldBe Success(22)
                    asyncAtimes10.await() shouldBe Success(20)
                }
            }


        }
    }

}
