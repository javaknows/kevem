# KEV-M

*The Kotlin Ethereum Virtual Machine*

[![Build Status](https://travis-ci.org/wjsrobertson/kevm.svg?branch=master)](https://travis-ci.org/wjsrobertson/kevm)
[![Coverage Status](https://codecov.io/gh/wjsrobertson/kevm/branch/master/graph/badge.svg)](https://codecov.io/gh/wjsrobertson/kevm)

This is an standalone implementation of the [Ethereum Virtual Machine](https://www.ethereum.org/) written in Kotlin.

It is a test EVM client in the style of [Ganache](https://www.trufflesuite.com/ganache) written natively for the JVM in Kotlin. 

It allows running web3j tests completely in-process without using a separate Ganache instance.

It is currently in a development state and not suitable for production usage yet. A list of missing features and shortcomings can be found in [evm/notes.md](evm/notes.md)

## Example Usage

Create a web3j instance with an account with one ETH balance based on a primary key:

```kotlin
import org.kevm.dsl.*

val web3j = kevm {
   account {
        balance = eth(1)
        privateKey = "0x68598e3adfd9904dbefaa024153e7c05fa2e95ccfc8846d80bd7f973cbce5395"
    }
}.toWeb3j()
```

It can then be used as normal - e.g. to deploy a HelloWorld contract and call a hello function on it: 

```kotlin
val txManager = ...
val gasProvider = ...

val contract = HelloWorld.deploy(web3j, txManager, gasProvider).send()
val txHash = contract.callHello().send().transactionHash
```

