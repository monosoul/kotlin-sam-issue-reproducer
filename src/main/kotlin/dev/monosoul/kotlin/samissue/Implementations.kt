package dev.monosoul.kotlin.samissue

val implementations = listOf<IFunctionalInterfaceConsumer>(
    FunctionalInterfaceConsumer(
        string = "asd",
        implementation = { value ->
            SomeValueClass(mapOf(
                value to value
            ))
        }
    )
)
