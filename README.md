# WIP: casual-java-cli

> This is a work in progress. Everything from the casual-java-cli rest API is mocked.
> 
> Requires Casual built locally as no SDK exists today of the Casual CLI binaries.

CLI for casual-java, based on CLI for Casual. 

Casual java CLI consists of two binaries, a `.war` file that exposes a rest-api for casual-jca and casual-caller and a cpp-binary for the CLI. The CLI fetches information from casual-jca and casual-caller from a Wildfly domain via HTTP and presents the information similar to Casual CLI.

## Get started

In order to build cpp binaries, the [casual repository (revision: feature/1.9/main)](https://github.com/casualcore/casual) must be cloned and present along side this repository. It is recommended to clone everything to `$HOME/git`.

Casual must be built locally as described in it:s `README.md`.

## Build
Directories:
- `cli` - contains the cpp source code
- `app` - contains the Java source code

### Build cli with conan
Source [casual.env](./cli/example/casual.env).
```bash
$ cd $HOME/git/casual-java-cli/cli
$ source example/casual.env
```

Install dependencies with conan:
```bash
$ cd $HOME/git/casual-java-cli/cli
$ conan profile detect # if never used conan before
$ conan install conanfile.txt

# Run the following if the above fails:
$ conan install conanfile.txt --build=missing
```

Build casual-java
```bash
$ cd $HOME/git/casual-java-cli/cli
$ cmake --preset conan-release # Configure cmake, made once
$ cmake --build . --preset conan-release # Build
```

### Build cli with CLion
To import the project in CLion, the conan plugin must first be installed. 

Then make sure that CLion has the `LD_LIBRARY_PATH` to the locally built Casual binaries, for example:
```bash 
$ export LD_LIBRARY_PATH=$HOME/git/casual/cmake-build-debug/middleware/common/bin
```

All environment variables from [casual.env](./cli/example/casual.env) must be set.

### Build casual-java-cli-app
Run Gradle, requires JDK version 17.
```bash
$ cd $HOME/git/casual-java-cli/app
$ ./gradlew build
```

## Usage
The Casual java CLI must connect to a running Wildfly domain, which has casual-java-cli-app.war installed. Either by specifing an environment variabel, for example: `CASUAL_JAVA_HOST=http://localhost:8080` or using the `--host` option in the CLI.

To se all available CLI commands, run
```bash
$ casual-java --help
```