# kotlin-sam-issue-reproducer
A repository to reproduce an issue with SAM in Kotlin 1.6.10

The exception being thrown is:
```java
Receiver class dev.monosoul.kotlin.samissue.ImplementationsKt$implementationsDefinedInADifferentFile$1 does not define or inherit an implementation of the resolved method 'abstract java.util.Map returnOtherString-H7iO9-4(java.lang.String)' of interface dev.monosoul.kotlin.samissue.SomeFunctionalInterface.
        java.lang.AbstractMethodError: Receiver class dev.monosoul.kotlin.samissue.ImplementationsKt$implementationsDefinedInADifferentFile$1 does not define or inherit an implementation of the resolved method 'abstract java.util.Map returnOtherString-H7iO9-4(java.lang.String)' of interface dev.monosoul.kotlin.samissue.SomeFunctionalInterface.
```

## There are at least 3 issues here:

### #1 The same object declared in different places have different behavior

Value `ImplementationsKt.implementationsDefinedInADifferentFile` and value 
`FunctionalInterfaceConsumerKt.implementationsDefinedInTheSameFile` are declared exactly the same way. 

Yet in the test they behave differently - items of `implementationsDefinedInTheSameFile` does not throw an exception 
when calling `SomeFunctionalInterface#returnOtherString`, while `implementationsDefinedInADifferentFile` do  throw
an exception.

#### Steps to reproduce:
 - run `./gradlew clean`
 - run `./gradlew test`

#### Expected behavior:
Calls to elements of `ImplementationsKt.implementationsDefinedInADifferentFile` and 
`FunctionalInterfaceConsumerKt.implementationsDefinedInTheSameFile` behave the same.

#### Actual behavior:
Calls to elements of `ImplementationsKt.implementationsDefinedInADifferentFile` and
`FunctionalInterfaceConsumerKt.implementationsDefinedInTheSameFile` behave differently.

---

### #2 Improper value class handling

The exception thrown in the issue #1 could be fixed by changing `SomeValueClass` into a data class, while that shouldn't
cause the issue above.

#### Steps to reproduce:
 - apply the following patch
   ```diff
   Index: src/main/kotlin/dev/monosoul/kotlin/samissue/SomeValueClass.kt
   IDEA additional info:
   Subsystem: com.intellij.openapi.diff.impl.patch.CharsetEP
   <+>UTF-8
   ===================================================================
   diff --git a/src/main/kotlin/dev/monosoul/kotlin/samissue/SomeValueClass.kt b/src/main/kotlin/dev/monosoul/kotlin/samissue/SomeValueClass.kt
   --- a/src/main/kotlin/dev/monosoul/kotlin/samissue/SomeValueClass.kt	(revision 8e287ef8293eaa058e791cebad516c6c7cabb29e)
   +++ b/src/main/kotlin/dev/monosoul/kotlin/samissue/SomeValueClass.kt	(date 1643895135455)
   @@ -1,4 +1,3 @@
   package dev.monosoul.kotlin.samissue
   
   -@JvmInline
   -value class SomeValueClass(val value: Map<String, String>)
   +data class SomeValueClass(val value: Map<String, String>)
   
   ```

- run the tests with:

   `./gradlew clean test`

#### Expected behavior:
The test result shouldn't be affected by the change (i.e. value class should not cause the issue).

#### Actual behavior:
The test starts to pass after the change mentioned above.

---

### #3 Unexpected incremental compiler behavior

If we add explicit SAM-constructor declaration to the lambda definition the test starts to pass, BUT only if it was ran 
before.

#### Steps to reproduce:

- run `./gradlew clean`
- run `./gradlew test` (the test fails)
- add explicit SAM-constructor declaration:
   ```diff
   Index: src/main/kotlin/dev/monosoul/kotlin/samissue/Implementations.kt
   IDEA additional info:
   Subsystem: com.intellij.openapi.diff.impl.patch.CharsetEP
   <+>UTF-8
   ===================================================================
   diff --git a/src/main/kotlin/dev/monosoul/kotlin/samissue/Implementations.kt b/src/main/kotlin/dev/monosoul/kotlin/samissue/Implementations.kt
   --- a/src/main/kotlin/dev/monosoul/kotlin/samissue/Implementations.kt	(revision 8e287ef8293eaa058e791cebad516c6c7cabb29e)
   +++ b/src/main/kotlin/dev/monosoul/kotlin/samissue/Implementations.kt	(date 1643897988381)
   @@ -3,7 +3,7 @@
    val implementationsDefinedInADifferentFile = listOf(
        FunctionalInterfaceConsumer(
            string = "asd",
   -        implementation = { value ->
   +        implementation = SomeFunctionalInterface { value ->
                SomeValueClass(mapOf(
                    value to value
                ))
   
   ```
- run `./gradlew test` (the test passes)
- remove explicit SAM-constructor declaration:
   ```diff
   Index: src/main/kotlin/dev/monosoul/kotlin/samissue/Implementations.kt
   IDEA additional info:
   Subsystem: com.intellij.openapi.diff.impl.patch.CharsetEP
   <+>UTF-8
   ===================================================================
   diff --git a/src/main/kotlin/dev/monosoul/kotlin/samissue/Implementations.kt b/src/main/kotlin/dev/monosoul/kotlin/samissue/Implementations.kt
   --- a/src/main/kotlin/dev/monosoul/kotlin/samissue/Implementations.kt	(revision c3b40a447c9c1b7a1ac064566a52caec3b2d5925)
   +++ b/src/main/kotlin/dev/monosoul/kotlin/samissue/Implementations.kt	(date 1643898124252)
   @@ -3,7 +3,7 @@
    val implementationsDefinedInADifferentFile = listOf(
        FunctionalInterfaceConsumer(
            string = "asd",
   -        implementation = SomeFunctionalInterface { value ->
   +        implementation = { value ->
                SomeValueClass(mapOf(
                    value to value
                ))
   
   ```
- run `./gradlew test` (the test passes)

#### Expected behavior:
After following the steps from above the test should still fail.

#### Actual behavior:
The test starts to pass even though the code is exactly the same as before following the steps.

---
