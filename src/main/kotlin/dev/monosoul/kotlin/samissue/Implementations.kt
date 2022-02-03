package dev.monosoul.kotlin.samissue

val implementationsDefinedInADifferentFile = listOf<IFunctionalInterfaceConsumer>(
    FunctionalInterfaceConsumer(
        string = "asd",
        implementation = { value ->
            SomeValueClass(mapOf(
                value to value
            ))
        }
    )
)
