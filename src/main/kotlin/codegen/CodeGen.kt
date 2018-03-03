package codegen

import kfx.init

fun main(args: Array<String>) {

    data class AnonymousFunction(val argument: String, val type: String, val body: String) {
        fun render() = "{ $argument : $type -> $body }"
    }

    data class ArgumentAndType(val argument : String, val type: String)

    val functions = buildString {
        (3..22).map { n ->

            val types = (1..n).map { ArgumentAndType("a$it", "T$it") }
            val typeArguments = types.map { it.type }
            val inputTypes = types.init()

            val uncurriedFunctionType = "(${typeArguments.init().joinToString()}) -> ${types.last().type}"
            val curriedFunctionType = typeArguments.init().map { "($it) ->" }.joinToString(separator = " ") + " ${typeArguments.last()}"

            val inner = AnonymousFunction(inputTypes.last().argument, inputTypes.last().type, "func(${inputTypes.map { it.argument }.joinToString()})")
            val body = inputTypes.init().reversed().fold(inner, { body, (arg, type) -> AnonymousFunction(arg, type, body.render()) })

            appendln("""

            fun <${typeArguments.joinToString()}> curry(func: $uncurriedFunctionType ) : $curriedFunctionType = ${body.render()}

            """.trimIndent())

        }

    }

    println(functions)


}