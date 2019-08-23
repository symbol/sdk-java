# nem2-sdk-openapi-gen
Project in charge of generating the nem2-sdk clients using openapi.

The open-api-generate.sh script will create and install different flavours of java rest clients according to the current NEM Open API specification.

The official NEM Api descriptor can be found here:

https://github.com/nemtech/nem2-docs/blob/master/source/resources/collections/openapi3.yaml

# Requirements

* Java 8 - One way of installing is using [sdkman](https://sdkman.io/)

# How to use it

Once tools have been installed, just run

`` ../gradlew clean generate install ``

The script will:

1. Generate one lib per flavor/framework/library. Currently vertx, jersey2 and okhttp-gson (see possible frameworks here https://openapi-generator.tech/docs/generators/java)
2. Build each library
3. Install the libraries locally using gradle

# Notes:

* Running the generator is required to build the branched [nem2-sdk-java](https://github.com/fboucquez/nem2-sdk-java) . Eventually, the generated libs will be published into the maven central repository and the nem2-sdk-java will depend on those libraries like any other third party dependency.
* The generated lib version (artifact version) should be consistent with the openapi3 YAML. Current version is 0.7.17 so the generated libraries should have the version 0.7.17. If the descriptor changes and the version is updated, the libraries should be upgraded, regenerated and deployed.
* The generator uses a patched version of the descriptor due to the AnyOf open [API bug](https://github.com/OpenAPITools/openapi-generator/issues/634)
* Generated code must not be changed not committed!!! target folder is gitignored. If there is something wrong with the generated code, you need to [customize the generator](https://openapi-generator.tech/docs/customization.html)

# TODOs:

* Move this folder to it's own github repository.
* Read version from the apenapi3 YAML file. At the moment the version needs to be copied and pasted from the descriptor to the script.
* Extend the generation to publish related libraries to maven central. Revisit the necessary data like author, license, etc.
* User real YAML from the web once the any of generation is fixed
* At the moment the build uses mvn as the generated Gradle configuration doesn't include mavenLocal to install the snapshot locally. Maybe we should upgrade the Gradle generator
* ~~Should this project also generate and release non-java libraries (like typescript)?~~ Rename repo to `nem2-sdk-java-openapi-generator`
* Configure ci (travis?) to automatically detect the official openpai3.yml changes, update the generator version, run it and deploy the generated aritifacts to the maven central. Then, everytime there is new descriptor push (to the master branch for example), ci will do the deployment automatically (either as a snapshot version or a release once the processed is working well).
