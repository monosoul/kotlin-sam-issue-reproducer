package dev.monosoul.kotlin.samissue

val implementationsInFileBefore = mutableListOf<SomeFunctionalInterface>().also {
    it.add(
        { value ->
            SomeValueClass(mapOf(
                value to value
            ))
        }
    )
}
