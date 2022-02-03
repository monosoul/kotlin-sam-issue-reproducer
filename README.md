# kotlin-sam-issue-reproducer
A repository to reproduce an issue with SAM in Kotlin 1.6.10

The exception being thrown is:
```java
Receiver class dev.monosoul.kotlin.samissue.ImplementationsKt$implementationsDefinedInADifferentFile$1 does not define or inherit an implementation of the resolved method 'abstract java.util.Map returnOtherString-H7iO9-4(java.lang.String)' of interface dev.monosoul.kotlin.samissue.SomeFunctionalInterface.
        java.lang.AbstractMethodError: Receiver class dev.monosoul.kotlin.samissue.ImplementationsKt$implementationsDefinedInADifferentFile$1 does not define or inherit an implementation of the resolved method 'abstract java.util.Map returnOtherString-H7iO9-4(java.lang.String)' of interface dev.monosoul.kotlin.samissue.SomeFunctionalInterface.
```

## There are at least 3 issues here:

### #1 Two objects declared the same way in different places have different behavior

Values `1_ImplementationsKt.getImplementationsInFileBefore` and  
`3_ImplementationsKt.getImplementationsInFileAfter` are declared exactly the same way.

From my observation it looks like the file order plays a role here, since lambdas declared in `3_Implementations.kt`
fail with the exception and lambdas declared in `1_Implementations.kt` work fine.

#### Steps to reproduce:
 - run `./gradlew clean`
 - run `./gradlew test`

#### Expected behavior:
Calls to elements of `3_ImplementationsKt.getImplementationsInFileAfter` and 
`1_ImplementationsKt.getImplementationsInFileBefore` behave the same.

#### Actual behavior:
Calls to elements of `3_ImplementationsKt.getImplementationsInFileAfter` and
`1_ImplementationsKt.getImplementationsInFileBefore` behave differently.

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
   --- a/src/main/kotlin/dev/monosoul/kotlin/samissue/SomeValueClass.kt	(revision 76f38dfd574959ee92e937ec4b81deaf61b240eb)
   +++ b/src/main/kotlin/dev/monosoul/kotlin/samissue/SomeValueClass.kt	(date 1643904653388)
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
   Index: src/main/kotlin/dev/monosoul/kotlin/samissue/3_Implementations.kt
   IDEA additional info:
   Subsystem: com.intellij.openapi.diff.impl.patch.CharsetEP
   <+>UTF-8
   ===================================================================
   diff --git a/src/main/kotlin/dev/monosoul/kotlin/samissue/3_Implementations.kt b/src/main/kotlin/dev/monosoul/kotlin/samissue/3_Implementations.kt
   --- a/src/main/kotlin/dev/monosoul/kotlin/samissue/3_Implementations.kt	(revision 76f38dfd574959ee92e937ec4b81deaf61b240eb)
   +++ b/src/main/kotlin/dev/monosoul/kotlin/samissue/3_Implementations.kt	(date 1643904709898)
   @@ -2,7 +2,7 @@
    
    val implementationsInFileAfter = mutableListOf<SomeFunctionalInterface>().also {
        it.add(
   -        { value ->
   +        SomeFunctionalInterface { value ->
                SomeValueClass(mapOf(
                    value to value
                ))
   
   ```
- run `./gradlew test` (the test passes)
- remove explicit SAM-constructor declaration:
   ```diff
   Index: src/main/kotlin/dev/monosoul/kotlin/samissue/3_Implementations.kt
   IDEA additional info:
   Subsystem: com.intellij.openapi.diff.impl.patch.CharsetEP
   <+>UTF-8
   ===================================================================
   diff --git a/src/main/kotlin/dev/monosoul/kotlin/samissue/3_Implementations.kt b/src/main/kotlin/dev/monosoul/kotlin/samissue/3_Implementations.kt
   --- a/src/main/kotlin/dev/monosoul/kotlin/samissue/3_Implementations.kt	(revision e04dd09487da52adab212fc3ca899345e5f9eab4)
   +++ b/src/main/kotlin/dev/monosoul/kotlin/samissue/3_Implementations.kt	(date 1643904743798)
   @@ -2,7 +2,7 @@
    
    val implementationsInFileAfter = mutableListOf<SomeFunctionalInterface>().also {
        it.add(
   -        SomeFunctionalInterface { value ->
   +        { value ->
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
