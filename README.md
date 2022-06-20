# `com.tomuvak.optional` – a multi-platform Kotlin library for the Optional type
This library is licensed under the [MIT License](https://en.wikipedia.org/wiki/MIT_License);
see [LICENSE.txt](LICENSE.txt).

## Table of contents
* [The Optional type](#the-optional-type)
  * [Further reading](#further-reading)
* [Rationale](#rationale)
  * [Kotlin's built-in nullable types](#kotlins-built-in-nullable-types)
  * [Java's java.util.optional](#javas-javautiloptional)
  * [Any other multi-platform Kotlin library](#any-other-multi-platform-kotlin-library)
* [Usage](#usage)
  * [Including the library in a Kotlin project](#including-the-library-in-a-kotlin-project)
  * [Using the `Optional` type](#using-the-optional-type)
    * [Basic values](#basic-values)
    * [Extracting the underlying `value`](#extracting-the-underlying-value)
    * [The "Elvis" operator](#the-elvis-operator)
    * [Mapping](#mapping)
    * [Monadic operations](#monadic-operations)
  * [Testing](#testing)

## The Optional type
The `Optional` type (also known as _Option_ or _Maybe_) represents values which are optional, that is there may or may
not be an actual value.
More concretely, a value of type `Optional<T>` is either:
* a (wrapped) value of type `T`; or
* `None` (= no value).

### Further reading
* [Wikipedia](https://en.wikipedia.org/wiki/Option_type)
* [nLab](https://ncatlab.org/nlab/show/maybe+monad)

## Rationale
There are countless uses for the `Optional` type, and they need not be explored here.
But this might be a reasonable place to address the issue of why use specifically `com.tomuvak.optional` rather than any
other existing solution, such as:

### Kotlin's [built-in nullable types](https://kotlinlang.org/docs/null-safety.html)
Kotlin's built-in distinction between nullable and non-null types is a very good thing in and of itself (certainly when
compared to the situation in some other programming languages, where many types are nullable and there's no way to force
non-nullability statically), and the use of nullable types and values addresses many of the use cases of the `Optional`
type.
However, one place where Kotlin's built-in nullable types fall short is in their inability to be nested:
"doubly-nullable" types are indistinguishable from their "singly-nullable" counterparts, and cannot make the often
important distinction between not having a value at all and having a value which is itself `null`.

For example, when `k` is some value of type `K` and `m` is of type `Map<K, V>`, what does it mean for
[`m[k]`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/get.html) to be `null`?
If `V` is a non-null type it means `m` does not contain the key `k`, but if `V` is a nullable type then this does not
distinguish between `m` not containing the key `k` and `m` containing the key `k` with an associated value of `null`.

For another example,
[`generateSequence`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.sequences/generate-sequence.html), which uses
`null`s to signal end of sequence, cannot be used to generate sequences containing `null`s (and, in fact, is declared to
only generate sequences of non-null types).

In contrast, the `Optional` type can be nested (allowing types such as `Optional<Optional<T>>`, and, indeed,
`Optional<Optional<Optional<T>>>`, `Optional<Optional<Optional<Optional<T>>>>` etc.), allowing analogous usages to the
ones above which make use of `Optional` rather than of nullable types to be fully generic and cater also for use cases
where underlying types could potentially themselves be `Optional`.

Note: nullable types are superior to `Optional` in their performance and in their memory consumption, and they enjoy the
significant benefit of being standard and built into the language. The author of `com.tomuvak.optional` does not at all
advocate against their use for specific use cases where the underlying type could not conceivably itself be nullable, or
when the distinction between having no value and having a value of `null` is _really_ not important. But for all other
cases `Optional` seems to be the better model.

Note also that it is possible to use nested types with a mix of `Optional`s and nullable types. In cases where a `T??`
type distinct from `T?` would be nice, alternatives to consider include `Optional<Optional<T>>`, `Optional<T?>` and
`Optional<T>?`.

### Java's [java.util.Optional](https://docs.oracle.com/javase/8/docs/api/java/util/Optional.html)
Regardless of any difference or similarity in functionality or features, one major distinguishing factor between
`com.tomuvak.optional` and any Java solution is that `com.tomuvak.optional` is multi-platform and so can be used
seamlessly in any Kotlin project, including on non-JVM platforms.

### Any other multi-platform Kotlin library
The author of `com.tomuvak.optional` has not explored any other multi-platform Kotlin library which offers support for
the `Optional` type, and makes no claim regarding any advantage `com.tomuvak.optional` may or may not have over other
such libraries.

## Usage

### Including the library in a Kotlin project
To add the library from
[GitHub Packages](https://docs.github.com/en/packages/learn-github-packages/introduction-to-github-packages), a
reference to this repository's GitHub Packages
[Maven repository](https://maven.apache.org/guides/introduction/introduction-to-repositories.html) needs to be added
inside the `repositories { ... }` block in the project's `build.gradle.kts` file:

```kotlin
    maven {
        url = uri("https://maven.pkg.github.com/tomuvak/optional")
        credentials { // See note below
            username = "<GitHub user name>"
            password = "<GitHub personal access token>"
        }
    }
```

and the dependency should be declared for the relevant source set(s) inside the relevant `dependencies { ... }` block(s)
inside the `sourceSet { ... }` block, e.g.

```kotlin
        val commonMain by getting {
            dependencies {
                implementation("com.tomuvak.optional:optional:0.0.1")
            }
        }
```

to add it for all platforms in a multi-platform project.

Note about credentials: it seems that even though this repository is public and everyone can download this library from
GitHub Packages, one still needs to supply credentials for some reason. Any GitHub user should work, when provided with
a [personal access
token](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/creating-a-personal-access-token)
for the user with (at least) the `read:packages` scope.

**You might want to keep the credentials private**, for example in case the GitHub user has access to private packages
(as GitHub personal access tokens can be restricted in the type of operations they're used for, but not in the
repositories they can access), or all the more so in case the token has a wider scope (and note also that one can change
a token's scope after its creation, so it's possible that at some future point the user might inadvertently grant a
token which was meant to be restricted more rights).

See [`com.tomuvak.optional-test`](https://github.com/tomuvak/optional-test), and specifically
[commit 0a7d166](https://github.com/tomuvak/optional-test/commit/0a7d166), for an example of one way this could be done
(by means of storing private information in a local file which is not source-controlled). Note that that commit was
accompanied by the creation of a file called `local.properties`, containing two lines like the following:

```properties
githubUser=<user name>
githubToken=<personal access token for the user above, with the `read:packages` scope>
```

### Using the `Optional` type

#### Basic values
A value of type `Optional<T>` can be either:
* `Optional.None` (corresponds to `null` when using [nullable types](https://kotlinlang.org/docs/null-safety.html) to
  model optionals); or
* an instance of `Optional.Value`, with an underlying value (accessible through the `value` property) of type `T`.

For brevity, one can `import com.tomuvak.optional.Optional.None` and `import com.tomuvak.optional.Optional.Value`, thus
allowing the use of the unqualified forms `None` and `Value`.

Just like one cannot simply use a value of type `T?` as if its type was `T`, the same is true for values of type
`Optional<T>`. One can check for there (not) being a value by comparing an `Optional` value (or its type) to `None`,
e.g. `if (optional == None)`, `if (optional != None)`, `if (optional is None)`, `if (optional !is None)`. Unlike the
situation with the built-in nullable types, where the compiler accepts code which treats a value of type `T?` as a value
of type `T` in contexts where it's established that the value is not `null`, establishing that an `Optional` value is
not `None` is not enough to permit accessing its `value` property. But establishing it is a `Value` is:

```kotlin
if (optional is Value) {
    // it is possible (and safe) to use optional.value here
}
```

One idiom for using `Optional` values is to use `when`:

```kotlin
when (optional) {
    /*is*/ None -> // what to do when there's no value
    is Value -> // it is possible (and safe) to use optional.value here
}
```

#### Extracting the underlying `value`
To force the extraction of the underlying `value` one can use the `forcedValue` property. It assumes there actually is
an underlying value and returns it not wrapped by an `Optional`. Of course, this operation throws if called on `None`.

This property is similar to the [`!!` operator](https://kotlinlang.org/docs/null-safety.html#the-operator) for nullable
types, and its use is similarly discouraged. Some programmers might sometimes find it useful under some circumstances,
but it is generally advised to use `if` or `when` like above, or the more idiomatic ways to work with `Optional`s
described below.

#### The "Elvis" operator
Similarly to the built-in [`?:` operator](https://kotlinlang.org/docs/null-safety.html#elvis-operator) for nullable
types (known as the ["Elvis" operator](https://en.wikipedia.org/wiki/Elvis_operator)), `optional or default` will take
the `value` out of `optional` if there is any, but resort to using `default` otherwise. Pass a function/lambda to avoid
computing the default value unnecessarily:
`optional or { compute default /* will only be called if optional is None */ }`. Use `orMaybe` instead of `or` when the
default is itself an `Optional`.

#### Mapping
As a [functor](https://en.wikipedia.org/wiki/Functor_(functional_programming)), the `Optional` type supports `map`ping:

```kotlin
Value(3).map { it + 2 } // Value(5)
None.map { it + 2 } // None
```

This is similar to the use of `?.let` for nullable types.

#### Monadic operations
As a [monad](https://en.wikipedia.org/wiki/Monad_(functional_programming)), the `Optional` type supports `flatten`ing
and `flatMap`ping:

```kotlin
Value(Value(3)).flatten() // Value(3)
Value(None).flatten() // None
None.flatten() // None

Value(4).flatMap { when {
    it % 2 == 0 -> Value(it / 2)
    else -> None
} } // Value(2)

Value(3).flatMap { when {
    it % 2 == 0 -> Value(it / 2)
    else -> None
} } // None

None.flatMap { when {
    it % 2 == 0 -> Value(it / 2)
    else -> None
} } // None
```

Note the difference from built-in nullable types, where everything is automatically flattened, and there's no
differentiation between a single level of nullability and nested levels thereof (so `3` is just `3`, without any
construct parallelling `Value(3)` or `Value(Value(3))`, and `null` is just `null`, with no distinction parallel to the
distinction between `None` and `Value(None)`).

### Testing
The sister library [`com.tomuvak.optional-test`](https://github.com/tomuvak/optional-test) provides some utilities
designed to facilitate testing code which uses the `Optional` type, specifically assertions over values of the
`Optional` type. [The test suite](src/commonTest/kotlin/OptionalTest.kt) makes use of some of them.
