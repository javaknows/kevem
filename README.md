# Kevem - Kotlin Ethereum Virtual Machine

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Documentation Status](https://readthedocs.org/projects/kevem/badge/?version=latest)](https://kevem.readthedocs.io/en/latest/?badge=latest)
[![Build Status](https://travis-ci.org/wjsrobertson/kevem.svg?branch=master)](https://travis-ci.org/wjsrobertson/kevem)
[![Coverage Status](https://codecov.io/gh/wjsrobertson/kevem/branch/master/graph/badge.svg)](https://codecov.io/gh/wjsrobertson/kevem)

Kevem is an Ethereum client for testing web3j-based Kotlin/Java/JVM code.

It is an implementation of the [Ethereum Virtual Machine](https://www.ethereum.org/) written in Kotlin.

It allows running web3j tests completely in-process using JUnit etc. Alternatively it can be run as a standalone RPC server.

It supports most EVM / Ethereum JSON-RPC behaviour but a list of missing features and limitations can be found in [the missing feature issues list](https://github.com/wjsrobertson/kevem/labels/missing%20feature). 

## Documentation

The latest documentation can be found on the [Kevem readthedocs site](https://kevem.readthedocs.io/en/latest/). 

## Running in-process 

:large_blue_diamond: For full information and options on running in-process see the [kotlin DSL](https://kevem.readthedocs.io/en/latest/kotlin-dsl.html) documentation on readthedocs.

Ensure `kevem-dsl-nodep.jar` is on your classpath along with `kotlin-stdlib` plus `web3j` version `5.3.x` and its transitive dependencies.  

Then use the Kotlin DSL to create a web3j instance. This example creates one account with one ETH balance based on a specified primary key:

```kotlin
import org.kevem.dsl.*

val web3j = kevem {
   account {
        balance = eth(1)
        privateKey = "0x68598e3adfd9904dbefaa024153e7c05fa2e95ccfc8846d80bd7f973cbce5395"
    }
}.toWeb3j()
```

It can then be used as you would use a normal web3j instance backed by a remote server - e.g. to deploy a HelloWorld contract and call a hello function on it: 

```kotlin
val txManager = ...
val gasProvider = ...

val contract = HelloWorld.deploy(web3j, txManager, gasProvider).send()
val txHash = contract.callHello().send().transactionHash
```

## Running as a standalone application

:large_blue_diamond: For full standalone execution instructions and options see the [running standalone](https://kevem.readthedocs.io/en/latest/running-standalone.html) documentation on readthedocs.

Use java 1.8 or greater to execute the `kevem-app-0.1.0-SNAPSHOT.jar` jar 

e.g. start on localhost, port 8545 generating 10 accounts:

```bash
java -jar kevem-app-0.1.0-SNAPSHOT.jar
```

## Building

Building Kevem requires Java 1.8 or better and Gradle 5 or better

At the project root:

```bash
gradle shadow
```

This will create two main artifacts:

* `app/build/libs/kevem-app.jar` - a runnable fat jar containing Kevem classes and all dependencies
* `dsl/build/libs/kevem-dsl-nodep.jar` - a jar containing Kevem classes required to use the DSL feature
