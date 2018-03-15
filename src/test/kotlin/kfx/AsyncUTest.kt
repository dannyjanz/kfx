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

                soon {
                    executions should containsInOrder(expectedExecutionOrder)
                }

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

                val asynAplusB = asyncB.apply(asyncA.map { { n: Int -> it + n } })

                runBlocking {
                    asynAplusB.await() shouldBe Success(22)
                }

            }
        }

    }
}