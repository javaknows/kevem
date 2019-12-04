# Notes related to the EVM

## Yellow Paper
https://ethereum.github.io/yellowpaper/paper.pdf

## Gas Cost Spreadsheet
https://docs.google.com/spreadsheets/d/1n6mRqkBz3iWcOlRem_mO09GtSKEKrAsfO7Frgx18pNU/

## Missing Features

* null vs empty data from return, revert
* precompiled contracts
* all operaitons don't work for large numbers


## Compatibiliy / Correctness Tests Needed

* Refund recpient for nested calls - is it the tx.origin or sender?
* Suicide called twice in same transaction 