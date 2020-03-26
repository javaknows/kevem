# Kevem

*The Kotlin Ethereum Virtual Machine*

[![Build Status](https://travis-ci.org/wjsrobertson/kevem.svg?branch=master)](https://travis-ci.org/wjsrobertson/kevem)
[![Coverage Status](https://codecov.io/gh/wjsrobertson/kevem/branch/master/graph/badge.svg)](https://codecov.io/gh/wjsrobertson/kevem)

This is an standalone implementation of the [Ethereum Virtual Machine](https://www.ethereum.org/) written in Kotlin.

It is a test EVM client in the style of [Ganache](https://www.trufflesuite.com/ganache) written natively for the JVM in Kotlin. 

It allows running web3j tests completely in-process without using a separate Ganache instance.

A list of missing features and shortcomings can be found in [the missing feature issues list](https://github.com/wjsrobertson/kevem/labels/missing%20feature)

## Example Usage

Create a web3j instance with an account with one ETH balance based on a primary key:

```kotlin
import org.kevem.dsl.*

val web3j = kevem {
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

