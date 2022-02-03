package dev.monosoul.kotlin.samissue

fun interface SomeFunctionalInterface {
    fun returnOtherString(input: String): SomeValueClass
}

val implementationsDefinedInTheSameFile = mutableListOf<SomeFunctionalInterface>().also {
    it.add(
        { value ->
            SomeValueClass(mapOf(
                value to value
            ))
        }
    )
}
