# nem2-sdk for Java/Kotlin/Scala
#

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.nem/sdk/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.nem/sdk)
[![Build Status](https://api.travis-ci.org/nemtech/nem2-sdk-java.svg?branch=master)](https://travis-ci.org/nemtech/nem2-sdk-java)
[![Coverage Status](https://coveralls.io/repos/github/nemtech/nem2-sdk-java/badge.svg?branch=master)](https://coveralls.io/github/nemtech/nem2-sdk-java?branch=master)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

The FORKED ~~official~~ nem2-sdk for Java, Kotlin and Scala to work with the NEM2 (a.k.a Catapult).

This fork has been created to tackle the following github [issue](https://github.com/nemtech/nem2-sdk-java/issues/85)

Other issues addressed:
* https://github.com/nemtech/nem2-sdk-java/issues/87
* https://github.com/nemtech/nem2-sdk-java/issues/86
* https://github.com/nemtech/nem2-sdk-java/issues/83
* https://github.com/nemtech/nem2-sdk-java/issues/88
* https://github.com/nemtech/nem2-sdk-java/issues/8 (okhttp and unnecessary lib clean-up)


## Requirements

- Java 8
- Java 9 has not been tested yet

## Installation

### Open API Generated Libraries

This forked version has moved the generated code to their own subfolder. Before building the project, execute

```
./gradlew -b ./nem2-sdk-openapi-generator/build.gradle clean generate
./gradlew -b ./nem2-sdk-openapi-generator/build.gradle install
```

Currently you need to generate and install the clients manually. Once the refactoring is completed, the generated clients will be released and deployed into maven central. You can then depend on the the generated library like any other open source third party lib.

### Changes from the official trunk:

* OpenAPI generated code has been removed
* Dependency to the generated lib has been added.
* Moved Repository to the API interface level
* Added RepositoryFactory.
* Cleaned unsued and duplicated lib dependencies
* Created Vertx repositories implementation that use the generated vertx client.
* Moved legacy repositories to their own package
* Created 3 repository factories (vertx, okhttp, legacy). Vertx and okhttp are being kept up to date with the new open api 3 yml definition.
* Refactored integration tests so they test the Repository Interfaces (like AccountRepository) and not the implementation (like the legacy AccountHttp). The integration tests are now parametetrized test. They will run once per implementation.
* The project is now multimodule. Modules are
    - nem2-sdk-java-api: Just dtos, interfaces and common utilty classes. It should be vertx, ok-http, gson, etc agnostic
    - nem2-sdk-java-vertx: Implementation nem2-sdk-java-api that uses vertx and generated nem2-sdk-java-openapi-vertx lib and dtos.
    - nem2-sdk-java-vertx-legacy: Implementation nem2-sdk-java-api that uses vertx and the generated nem2-sdk-java-openapi-vertx lib dtos.
    - nem2-sdk-java-okhttp: Implementation nem2-sdk-java-api that uses ok-http and the generated nem2-sdk-java-openapi-okhttp-gson lib dtos.
    - nem2-sdk-java-integration-test: Integreation tests that test the different repositories against a running catapult localhost server.


Each sdk user can depend on the best library for its need (example, nem2-sdk-java-okhttp for android users).

The JAVA sdk modules and the generated clients will grow and be released independently. The generated client will be changed, released and deployed only when the openapi descriptor changes (current version 0.7.17 with ongoing progress). This project/multimodule java sdk will be changed everytime a new feature has been implemented or a bug has been fixed (0.13.0). If you change the api or any of the implementation, it's pretty likely you need to change another implementation. 2 github projects, 2 releases,  the generated clients and the this multimodule one.


## TODOs
* Move nem2-sdk-openapi-generator to its own github repo.
* Fix central maven deployments so the generated libs can be published there and you don't need to build them manually.
* Fix integration tests. Make them reproducible.
* Remove legacy implementation. They manual mappers are not being kept up to date. It's still in the repo for reference.

### Maven

```xml
<dependency>
    <groupId>io.nem</groupId>
    <artifactId>nem2-sdk-java-vertx</artifactId>
    <version>0.13.0</version>
</dependency>
```

```xml
<dependency>
    <groupId>io.nem</groupId>
    <artifactId>nem2-sdk-java-okhttp</artifactId>
    <version>0.13.0</version>
</dependency>
```

```xml
<dependency>
    <groupId>io.nem</groupId>
    <artifactId>nem2-sdk-java-vertex-legacy</artifactId>
    <version>0.13.0</version>
</dependency>
```

### Gradle

```compile 'io.nem:nem2-sdk-java-vertx:0.13.0```

```compile 'io.nem:nem2-sdk-java-okhttp:0.13.0```

```compile 'io.nem:nem2-sdk-java-vertx-legacy:0.13.0```

### SBT

```libraryDependencies += "io.nem" % "nem2-sdk-java-vertx" % "0.13.0"```

```libraryDependencies += "io.nem" % "nem2-sdk-java-okhttp" % "0.13.0"```

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
