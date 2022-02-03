package dev.monosoul.kotlin.samissue

import org.junit.jupiter.api.Test

class FunctionalInterfaceConsumerTest {

    @Test
    fun `should not fail calling the consumer when it is defined in the other file`() {
        implementationsDefinedInADifferentFile.forEach {
            it.callFunctionalInterfaceImplementation()
        }
    }

    @Test
    fun `should not fail calling the consumer when it is defined in the same file`() {
        implementationsDefinedInTheSameFile.forEach {
            it.callFunctionalInterfaceImplementation()
        }
    }
}
