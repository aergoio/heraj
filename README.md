[![MIT License](https://img.shields.io/badge/license-MIT-blue.svg)](https://opensource.org/licenses/MIT)
[![LoC](https://tokei.rs/b1/github/aergoio/heraj)](https://github.com/aergoio/heraj)
[![Travis_ci](https://travis-ci.org/aergoio/heraj.svg?branch=master)](https://travis-ci.org/aergoio/heraj)
[![codecov.io](http://codecov.io/github/aergoio/heraj/coverage.svg?branch=master)](http://codecov.io/github/aergoio/heraj?branch=master)

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

# Integration
TBD

# Build
## Prerequisites
* [JDK 8](http://openjdk.java.net/)
* [NPM](https://www.npmjs.com/)

## Build and package
* Update submodule
```console
$ git submodule update
```

* Clean
```console
$ ./build.sh clean
```

* Create apm web ui
```console
$ cd client/apm-web ; npm install
$ cd $PROJECT_HOME ; ./build.sh npm
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
