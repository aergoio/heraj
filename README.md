# heraj - Java client framework for aergo

[![MIT License](https://img.shields.io/badge/license-MIT-blue.svg)](https://opensource.org/licenses/MIT)
[![LoC](https://tokei.rs/b1/github/aergoio/heraj)](https://github.com/aergoio/heraj)
[![github build](https://github.com/aergoio/heraj/actions/workflows/gradle.yml/badge.svg)](https://github.com/aergoio/aergo/actions/workflows/gradle.yml/badge.svg)
[![codecov.io](https://codecov.io/github/aergoio/heraj/coverage.svg?branch=develop)](https://codecov.io/github/aergoio/heraj?branch=develop)

The hera is the client-side framework for the aergo.
This repository, heraj is java implementation for hera.

## Latest

v1.4.2

build with aergo-protobuf [6ce439c7600ae7b167c6c61e66ce2ac38bb2bef9](https://github.com/aergoio/aergo-protobuf/commits/6ce439c7600ae7b167c6c61e66ce2ac38bb2bef9)

## Compatibility

- Aergo : v2.2.x
- Java : JDK 8 or higher
- Android : Android 3.0 (API 11) or higher

## Download

There are 3 kind of library:

- heraj-transport : minimum library including all of the base
- heraj-wallet : nonce handling wallet library (depends on `heraj-transport`)
- heraj-smart-contract : simple client to call smart contract with a java interface (depends on `heraj-wallet`)

If you just want a minimum one, use `heraj-transport`. Or need more feature, use `heraj-wallet` or `heraj-smart-contract`.

## Module Structure

- core
  - annotation : Store annotations used within heraj.
  - common : Store models used within heraj.
  - protobuf : Keeps java files generated from *.proto.
  - transport : Transport module interacting with aergo node using grpc.
  - util : Utils used within heraj.
- client
  - wallet : Provides KeyStore to store aergo key. Provides WalletApi to interacting with KeyStore.
  - smart-contract : Modules for interface-based smart contract interaction.

### Maven

```sh

...

<dependencies>
  <dependency>
    <groupId>io.aergo</groupId>
    <artifactId>heraj-transport</artifactId>
    <version>${herajVersion}</version>
  </dependency>
  <dependency>
    <groupId>io.aergo</groupId>
    <artifactId>heraj-wallet</artifactId>
    <version>${herajVersion}</version>
  </dependency>
  <dependency>
    <groupId>io.aergo</groupId>
    <artifactId>heraj-smart-contract</artifactId>
    <version>${herajVersion}</version>
  </dependency>
</dependencies>
```

### Gradle

```sh
...

dependencies {
  implementation "io.aergo:heraj-transport:${herajVersion}"
  implementation "io.aergo:heraj-wallet:${herajVersion}"
  implementation "io.aergo:heraj-smart-contract:${herajVersion}"
}
```

## Build from source

### Prerequisites

- [JDK8](https://openjdk.java.net/projects/jdk8/)

### Clone

```console
$ git clone --recurse-submodule https://github.com/aergoio/heraj.git
```

### Submodule

Initialize submodule (if not initialized)

```console
$ git submodule init
```

Update submodule

```console
$ git submodule update
```

### Build

- Clean: `./gradlew clean`
- Generate protobuf based files: `./gradlew :core:protobuf:build`
- Lint: `./gradlew lint`
- Test: `./gradlew test`
  - Coverage (need test task): `./gradlew test coverage` (individual), `./gradlew test allcoverage` (all)
  - Integration Test: `./test/run-it.sh` (need docker running)
  - Benchmark Test: `./gradlew {target_project}:jmh`
- Docs: `./gradlew javadoc` (individual), `./gradlew alljavadoc` (all)
- Build (also lint, test): `./gradlew build`
- Shadow Jar: `./gradlew shadowJar` (generated in `./assembly/build/libs`)
- Install to local: `./gradlew publishToMavenLocal`

## Kind of test

### Unit test

They are classes with 'Test' suffix.

### Integration test

They are classes with 'IT' suffix meaning integration test.

### Benchmark test

They are classes with 'Benchmark' suffix, which using jmh.

## Contribution

Guidelines for any code contributions:

1. Any changes should be accompanied by tests. It's guaranteed by travis ci.
2. Code coverage should be maintained. Any requests dropping down code coverage significantly will be not confirmed.
3. All contributions must be licensed MIT and all files must have a copy of statement indicating where license is (can be copied from an existing file).
4. All java files should be formatted according to [Google's Java style guide](http://google.github.io/styleguide/javaguide.html). You can use checkstyle plugin for [eclipse](https://checkstyle.org/eclipse-cs/#!/) or [IntelliJ](https://plugins.jetbrains.com/plugin/1065-checkstyle-idea). And you can check by running `./gradlew lint`
5. Please squash all commits for a change into a single commit (this can be done using git rebase -i). Make sure to have a meaningful commit message for the change.
