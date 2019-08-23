# nem2-sdk for Java/Kotlin/Scala
#

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.nem/sdk/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.nem/sdk)
[![Build Status](https://api.travis-ci.org/nemtech/nem2-sdk-java.svg?branch=master)](https://travis-ci.org/nemtech/nem2-sdk-java)
[![Coverage Status](https://coveralls.io/repos/github/nemtech/nem2-sdk-java/badge.svg?branch=master)](https://coveralls.io/github/nemtech/nem2-sdk-java?branch=master)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

The official nem2-sdk for Java, Kotlin and Scala to work with the NEM2 (a.k.a Catapult).

## Requirements

- Java 8
- Java 9 has not been tested yet

## Installation

### Open API Generated Libraries

The SDK libs depend on Open API 3 generated clients. Before building the project, execute the following to generate and install the libraries.

```
./gradlew -b ./nem2-sdk-openapi-generator/build.gradle clean generate install
```

Currently you need to generate and install the clients manually. The generated clients will be released and deployed into maven central. You can then depend on the the generated library like any other open source third party lib.

This step will be optional, only if you are working on the open api 3 spec or you want to change the configuration of generated libraries.

## Usage

Each SDK user can depend on the best library for its need (example, ``nem2-sdk-java-vertx`` for server developers or ``nem2-sdk-java-okhttp`` for android developers).

### Maven

```xml
<dependency>
    <groupId>io.nem</groupId>
    <artifactId>nem2-sdk-java-vertx</artifactId>
    <version>0.13.0</version>
</dependency>
```

OR

```xml
<dependency>
    <groupId>io.nem</groupId>
    <artifactId>nem2-sdk-java-okhttp</artifactId>
    <version>0.13.0</version>
</dependency>
```

OR

```xml
<dependency>
    <groupId>io.nem</groupId>
    <artifactId>nem2-sdk-java-vertex-legacy</artifactId>
    <version>0.13.0</version>
</dependency>
```

### Gradle

```compile 'io.nem:nem2-sdk-java-vertx:0.13.0```

OR

```compile 'io.nem:nem2-sdk-java-okhttp:0.13.0```

OR

```compile 'io.nem:nem2-sdk-java-vertx-legacy:0.13.0```

### SBT

```libraryDependencies += "io.nem" % "nem2-sdk-java-vertx" % "0.13.0"```

OR

```libraryDependencies += "io.nem" % "nem2-sdk-java-okhttp" % "0.13.0"```

OR

```libraryDependencies += "io.nem" % "nem2-sdk-java-vertx-legacy" % "0.13.0"```

## Documentation and Getting Started

Get started and learn more about nem2-sdk-java, check the [official documentation][docs].

Check SDK reference [here][sdk-ref]

## nem2-sdk Releases

The release notes for the nem2-sdk can be found [here](CHANGELOG.md).

## Contributing

This project is developed and maintained by NEM Foundation. Contributions are welcome and appreciated. You can find [nem2-sdk on GitHub][self];
Feel free to start an issue or create a pull request. Check [CONTRIBUTING](CONTRIBUTING.md) before start.

## Getting help

We use GitHub issues for tracking bugs and have limited bandwidth to address them.
Please, use the following available resources to get help:

- [nem2-cli documentation][docs]
- If you found a bug, [open a new issue][issues]

## License

Copyright (c) 2018 NEM
Licensed under the [Apache License 2.0](LICENSE)

[self]: https://github.com/nemtech/nem2-sdk-java
[docs]: http://nemtech.github.io/getting-started/setup-workstation.html
[issues]: https://github.com/nemtech/nem2-sdk-java/issues
[sdk-ref]: http://nemtech.github.io/nem2-sdk-java
