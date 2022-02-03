package dev.monosoul.kotlin.samissue

interface IFunctionalInterfaceConsumer {
    fun callFunctionalInterfaceImplementation(): SomeValueClass?
}

data class FunctionalInterfaceConsumer(
    private val string: String,
    private val implementation: SomeFunctionalInterface
) : IFunctionalInterfaceConsumer {
    override fun callFunctionalInterfaceImplementation() = implementation.returnOtherString(string)
}

fun interface SomeFunctionalInterface {
    fun returnOtherString(input: String): SomeValueClass
}

val implementationsDefinedInTheSameFile = listOf<IFunctionalInterfaceConsumer>(
    FunctionalInterfaceConsumer(
        string = "asd",
        implementation = { value ->
            SomeValueClass(mapOf(
                value to value
            ))
        }
    )
)
