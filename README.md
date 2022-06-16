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
This is the initial release, version `0.0.1`. It only contains the `Optional<T>` type definition itself (including
`None` and `Value<T>`). Further functionality will be added in future versions.

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

See this library's own [build.gradle.kts](build.gradle.kts) (and specifically
[commit e658676](https://github.com/tomuvak/optional/commit/e658676)) for an example of one way this could be done (by
means of storing private information in a local file which is not source-controlled).
