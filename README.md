# heraj - Java client framework for aergo

[![MIT License](https://img.shields.io/badge/license-MIT-blue.svg)](https://opensource.org/licenses/MIT)
[![LoC](https://tokei.rs/b1/github/aergoio/heraj)](https://github.com/aergoio/heraj)
[![Travis_ci](https://travis-ci.org/aergoio/heraj.svg?branch=master)](https://travis-ci.org/aergoio/heraj)
[![codecov.io](http://codecov.io/github/aergoio/heraj/coverage.svg?branch=master)](http://codecov.io/github/aergoio/heraj?branch=master)
[![Maintainability](https://api.codeclimate.com/v1/badges/a0aa6cecd0067bddc770/maintainability)](https://codeclimate.com/github/aergoio/heraj/maintainability)

The hera is the client-side framework for the aergo.
This repository, heraj is java implementation for hera.

## Lastest

v0.11.0

build with aergo-protobuf v0.11.0

## Compatibility

* Aergo : v0.11.x
* Java : JDK 6 or higher
* Android : Android 3.0 (API 11) or higher

## Download

There are 3 kind of library:

* heraj-transport : minimum library including all of the base
* heraj-wallet : nonce handling wallet library (depends on `heraj-transport`)
* heraj-smart-contract : simple client to call smart contract with a java interface (depends on `heraj-wallet`)

If you just want a minimum one, use `heraj-transport`. Or need more feature, use `heraj-wallet` or `heraj-smart-contract`.

### Maven

```sh
<repositories>
  <repository>
    <id>jcenter</id>
    <url>https://jcenter.bintray.com</url>
  </repository>
</repositories>

...

<dependencies>
  <dependency>
    <groupId>io.aergo</groupId>
    <artifactId>heraj-transport</artifactId>
    <version>0.11.0</version>
  </dependency>
  <dependency>
    <groupId>io.aergo</groupId>
    <artifactId>heraj-wallet</artifactId>
    <version>0.11.0</version>
  </dependency>
  <dependency>
    <groupId>io.aergo</groupId>
    <artifactId>heraj-smart-contract</artifactId>
    <version>0.11.0</version>
  </dependency>
</dependencies>
```

### Gradle

```sh
repositories {
  jcenter()
}

...

dependencies {
  implementation 'io.aergo:heraj-transport:0.11.0'
  implementation 'io.aergo:heraj-wallet:0.11.0'
  implementation 'io.aergo:heraj-smart-contract:0.11.0'
}
```

## Modules

The repository contains next:

* core/annotation
* core/util
* core/common
* core/protobuf
* core/transport
* client/wallet
* client/smart-contract
* examples

# Integration

TBD

# Build

## Prerequisites

* JDK 7 or higher is recommanded because of gradle.

## Clone

```console
$ git clone --recurse-submodule https://github.com/aergoio/heraj.git
```

## Build and package

* Initialize submodule (if not initialized)

```console
$ git submodule init
```

* Update submodule

```console
$ git submodule update
```

* Clean

```console
$ ./build.sh clean
```

* Run gradle

```console
$ ./build.sh gradle
```

* Install to maven local

```console
$ ./build.sh install
```

# Test

## Kind of test

### Unit test

They are classes with 'Test' suffix.

### Integration test

They are classes with 'IT' suffix meaning integration test.

### Benchmark test

They are classes with 'Benchmark' suffix, which using jmh.

## Run tests

```console
$ ./build.sh test
```

# Documentation

We provides next in https://aergoio.github.io/heraj

* JavaDoc
* Test Coverage

## How to build documents

```console
$ ./build.sh docs
```

# Contribution

Guidelines for any code contributions:

1. Any changes should be accompanied by tests. It's guaranteed by travis ci.
2. Code coverage should be maintained. Any requests dropping down code coverage significantly will be not confirmed.
3. All contributions must be licensed MIT and all files must have a copy of statement indicating where license is (can be copied from an existing file).
4. All java files should be formatted according to [Google's Java style guide](http://google.github.io/styleguide/javaguide.html). You can use checkstyle plugin for [eclipse](https://checkstyle.org/eclipse-cs/#!/) or [IntelliJ](https://plugins.jetbrains.com/plugin/1065-checkstyle-idea). And you can check by running `./build.sh gradle`
5. Please squash all commits for a change into a single commit (this can be done using git rebase -i). Make sure to have a meaningful commit message for the change.
