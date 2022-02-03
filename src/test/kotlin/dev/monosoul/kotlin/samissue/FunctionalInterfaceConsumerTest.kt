package dev.monosoul.kotlin.samissue

import org.junit.jupiter.api.Test

class FunctionalInterfaceConsumerTest {

    @Test
    fun `should not fail calling a lambda when it is defined in the file before interface definition`() {
        implementationsInFileBefore.forEach {
            it.returnOtherString("asd")
        }
    }

    @Test
    fun `should not fail calling a lambda when it is defined in the file after interface definition`() {
        implementationsInFileAfter.forEach {
            it.returnOtherString("asd")
        }
    }
}
