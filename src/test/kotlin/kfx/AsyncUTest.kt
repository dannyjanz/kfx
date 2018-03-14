package kfx

import io.kotlintest.Duration
import io.kotlintest.eventually
import io.kotlintest.matchers.containsAll
import io.kotlintest.matchers.containsInOrder
import io.kotlintest.matchers.should
import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.ShouldSpec
import java.lang.Error
import java.util.concurrent.TimeUnit

class AsyncUTest : ShouldSpec() {
    init {

        val soon = { func: () -> Unit -> eventually(Duration(200, TimeUnit.MILLISECONDS), Error::class.java, func) }

        "Async" {
            should("execute a given operation parallel to the invoking Thread") {

                val expectedExecutionOrder = listOf("A", "B")

                var executions = listOf<String>()

                val asyncResult = Async {
                    Thread.sleep(100)
                    executions += "B"
                    1 + 1
                }

                executions += "A"

                executions should containsAll("A")

                soon {
                    executions should containsInOrder(expectedExecutionOrder)
                }

                asyncResult.onComplete {
                    it shouldBe Success(2)
                }

            }
        }

    }
}