package dev.monosoul.kotlin.samissue

data class FunctionalInterfaceConsumer(
    private val string: String,
    private val implementation: SomeFunctionalInterface
) {
    fun callFunctionalInterfaceImplementation() = implementation.returnOtherString(string)
}

fun interface SomeFunctionalInterface {
    fun returnOtherString(input: String): SomeValueClass
}

val implementationsDefinedInTheSameFile = listOf(
    FunctionalInterfaceConsumer(
        string = "asd",
        implementation = { value ->
            SomeValueClass(mapOf(
                value to value
            ))
        }
    )
)
