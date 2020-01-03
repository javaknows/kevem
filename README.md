# KEV-M

*The Kotlin Ethereum Virtual Machine*

[![Build Status](https://travis-ci.org/wjsrobertson/kevin.svg?branch=master)](https://travis-ci.org/wjsrobertson/kevin)
[![Coverage Status](https://codecov.io/gh/wjsrobertson/kevin/branch/master/graph/badge.svg)](https://codecov.io/gh/wjsrobertson/kevin)

This is an standalone implementation of the [Ethereum Virtual Machine](https://www.ethereum.org/) written in Kotlin.

It is a test EVM in the style of [Ganache](https://www.trufflesuite.com/ganache) written natively for the JVM in Kotlin. 

It allows running web3j tests completely in-process without using a separate Ganache instance.

It is currently in a development state and not suitable for production usage yet. A list of missing features and shortcomings can be found in [evm/notes.md](evm/notes.md)
