package dev.monosoul.kotlin.samissue

import org.junit.jupiter.api.Test

class FunctionalInterfaceConsumerTest {

    @Test
    fun `should not fail when calling the hardcoded instance`() {
        implementations.forEach {
            it.callFunctionalInterfaceImplementation()
        }
    }
}
