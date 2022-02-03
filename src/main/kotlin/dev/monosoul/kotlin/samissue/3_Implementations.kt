package dev.monosoul.kotlin.samissue

val implementationsInFileAfter = mutableListOf<SomeFunctionalInterface>().also {
    it.add(
        { value ->
            SomeValueClass(mapOf(
                value to value
            ))
        }
    )
}
