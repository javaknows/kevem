# Kevem - Kotlin Ethereum Virtual Machine

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Build Status](https://travis-ci.org/wjsrobertson/kevem.svg?branch=master)](https://travis-ci.org/wjsrobertson/kevem)
[![Coverage Status](https://codecov.io/gh/wjsrobertson/kevem/branch/master/graph/badge.svg)](https://codecov.io/gh/wjsrobertson/kevem)

Kevem is an implementation of the [Ethereum Virtual Machine](https://www.ethereum.org/) written in Kotlin.

It is a test EVM client in the style of [Ganache](https://www.trufflesuite.com/ganache) written natively for the JVM in Kotlin. 

Kevem allows running web3j tests completely in-process without using a separate Ganache instance. Alternatively it can be run as a standalone RPC server.

Kevem supports most EVM / Ethereum JSON-RPC behaviour but a list of missing features and shortcomings can be found in [the missing feature issues list](https://github.com/wjsrobertson/kevem/labels/missing%20feature). 

## Running in-process 

Ensure `kevem-dsl-nodep.jar` is on your classpath along with `kotlin-stdlib` plus `web3j` version `5.3.x` and it's transitive dependencies.  

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

Use java 1.8 or greater to execute the `kevem-app-0.1.0-SNAPSHOT.jar` jar 

e.g. start on localhost, port 8545 generating 10 accounts:

```bash
java -jar kevem-app-0.1.0-SNAPSHOT.jar
```

e.g. start on port 8888, generate two accounts using a specific mnemonic phrase:

```bash
java -jar kevem-app-0.1.0-SNAPSHOT.jar -p 8888 -n 2 -m "stay jeans limb improve struggle return predict flower assume giraffe mother spring"
```

At startup Kevem prints out the balance and private keys of the generated accounts in similar format to Ganache. These accounts will all be unlocked.

### All command line arguments

| flag | long flag                 | description |
|----|-----------------------------|-------------------------------------------------|
| -p | --port <arg>                |  port web RPC service listens on (8545) |
| -h | --host <arg>                |  hostname web RPC service listens on (localhost) |
| -n | --numAccounts <arg>         |  number of accounts to create when using (10) |  mnemonic |
| -m | --mnemonic <arg>            |  bip39 mnemonic phrase for account generation (hard-coded default) |
| -e | --defaultBalanceEther <arg> |  balance for each generated account (100) |
| -g | --gasPrice <arg>            |  block gas price in wei |
| -l | --gasLimit <arg>            |  block gas limit in wei |
| -c | --chainId <arg>             |  chain ID (0) |
| -i | --networkId <arg>           |  network ID (1) |
| -v | --verbose                   |  print extra output including stack traces for startup errors |
|    | --help                      |  display help then exit |
|    | --version                   |  display version then exit |

## Building

Building Kevem requires Java 1.8 or better and Gradle 5 or better

At the project root:

```bash
gradle shadow
```

This will create two main artifacts:

* `app/build/libs/kevem-app.jar` - a runnable fat jar containing Kevem classes and all dependencies
* `dsl/build/libs/kevem-dsl-nodep.jar` - a jar containing Kevem classes required to use the DSL feature
