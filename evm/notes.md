# Notes related to the EVM

## Yellow Paper
https://ethereum.github.io/yellowpaper/paper.pdf

## EIP List
https://eips.ethereum.org/all

## RLP encoding
https://github.com/ethereum/wiki/wiki/RLP

## Gas Cost Spreadsheet
https://docs.google.com/spreadsheets/d/1n6mRqkBz3iWcOlRem_mO09GtSKEKrAsfO7Frgx18pNU/

## Missing Features

* null vs empty data from return, revert
* precompiled contracts
* all operations don't work for large numbers
* EIPS around gas costs
* in JSON-RPC fail if block number is too large for type as per parity
* transaction signing (including in JSON-RPC)
* sendTransaction, call, estimateGas in StandardRPC
* rlp encoding - 

## Compatibiliy / Correctness Tests Needed

* Refund recpient for nested calls - is it the tx.origin or sender?
* Suicide called twice in same transaction 




    private fun toKevinBytes(bytes: List<kotlin.Byte>) = bytes.map { it.toInt() and 0xFF }.map { Byte(it) }

    private fun toKotlinBytes(bytes: List<Byte>): List<kotlin.Byte> = bytes.map { it.javaByte() }Conv