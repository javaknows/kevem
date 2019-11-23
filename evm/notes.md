# Notes related to the EVM

## Yellow Paper
https://ethereum.github.io/yellowpaper/paper.pdf

## Gas Cost Spreadsheet
https://docs.google.com/spreadsheets/d/1n6mRqkBz3iWcOlRem_mO09GtSKEKrAsfO7Frgx18pNU/

## Cleanup

* max stack size of 1024
* stack underflow
* null vs empty data from return, revert
* self destruct set - apply at end of tx & apply refund
* gas refund for storage set back to 0 
* use substate object to track refunds and self destructs accounts
* account nonce
* use naming more consistent with the yellow paper
* precompiled contracts
* halt call if not enough funds
