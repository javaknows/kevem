# Notes related to the EVM

## Yellow Paper
https://ethereum.github.io/yellowpaper/paper.pdf

## EIP List
https://eips.ethereum.org/all

## RLP encoding
https://github.com/ethereum/wiki/wiki/RLP

## Gas Cost Spreadsheet
https://docs.google.com/spreadsheets/d/1n6mRqkBz3iWcOlRem_mO09GtSKEKrAsfO7Frgx18pNU/

## RPC methods

main - https://github.com/ethereum/wiki/wiki/JSON-RPC#eth_getfilterchanges
test - https://github.com/ethereum/retesteth/wiki/RPC-Methods

## Missing Features

* null vs empty data from return, revert
* EIPS around gas costs
* in JSON-RPC fail if block number is too large for type as per parity

## Compatibiliy / Correctness Tests Needed

* Refund recipient for nested calls - is it the tx.origin or sender?
* Suicide called twice in same transaction 
* eth_estimateGas - need examples with each field missing
* eth_estimateGas - behaviour for pending block
* eth_estimateGas - from the RPC docs: "As a result the returned estimate might not be enough to executed the call/transaction when the amount of gas is higher than the pending block gas limit."
