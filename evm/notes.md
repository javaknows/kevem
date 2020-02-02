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
* EIPS around gas costs
* in JSON-RPC fail if block number is too large for type as per parity

## Compatibiliy / Correctness Tests Needed

* Refund recipient for nested calls - is it the tx.origin or sender?
* Suicide called twice in same transaction 
