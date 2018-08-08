[![MIT License](https://img.shields.io/badge/license-MIT-blue.svg)](https://opensource.org/licenses/MIT)
[![LoC](https://tokei.rs/b1/github/aergoio/heraj)](https://github.com/aergoio/heraj).

# Introduction
The hera is the client-side framework for the aergo.
This repository, heraj is java implementation for hera.

The heraj provides the next:
* Utilities
* Aergo client(both low and high level API)
* Integration to other useful frameworks.
* Rapid development tools
* Boilerplate and examples

## Modules
The repository contains next:
* core/annotation
* core/util
* core/common
* core/transport
* client/wallet
* client/smart-contract
* client/aergo-mockup
* tool/aergo-maven-plugin
* tool/aergo-gradle-plugin
* integration/spring-aergo
* doc/getting-started
* examples/boilerplate
* examples/XXXX

# Integration
TBD

# Build
## Prerequisites
* [JDK 8](http://www.oracle.com/technetwork/java/javase/downloads/index.html)

## build and package
* Clean
```console
$ ./build.sh clean 
```

* Compile protobuf
```console
$ ./build.sh protobuf
```

* Run gradle
```console
$ ./build.sh gradle
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

