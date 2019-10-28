Feature: Single Opcode Execution

  Check that each opcode executes correctly

  Scenario: Two numbers can be added using ADD
    Given 0x01 is pushed onto the stack
    And 0x02 is pushed onto the stack
    When opcode ADD is executed
    Then the stack contains 0x03

  Scenario: Contract address is correct using ADDRESS
    Given the contract address is 0xEE
    When opcode ADDRESS is executed
    Then the stack contains 0xEE

  Scenario: Balance of an address is retrieved with BALANCE
    Given an account with address 0xAA has balance 0x123
    And 0xAA is pushed onto the stack
    When opcode BALANCE is executed
    Then the stack contains 0x123

  Scenario: Transaction origin is correct using ORIGIN
    Given transaction origin is 0xBB
    When opcode ORIGIN is executed
    Then the stack contains 0xBB

  Scenario: Caller is returned for CALLER
    Given the current caller address is 0xABC
    And the current call type is any of
      | INITIAL |
      | CALL |
      | CALLCODE |
      | STATICCALL |
    When opcode CALLER is executed
    Then the stack contains 0xABC

  Scenario: Current caller is not used if current call is DELEGATECALL
    Given the current caller address is 0xABC
    And the current call type is DELEGATECALL
    And the previous caller address is 0xFFF
    And the previous call type is any of
      | INITIAL |
      | CALL |
      | CALLCODE |
      | STATICCALL |
    When opcode CALLER is executed
    Then the stack contains 0xFFF

  Scenario: Current call value is returned by CALLVALUE
    Given the current call value is 0x1111
    When opcode CALLVALUE is executed
    Then the stack contains 0x1111

  Scenario: Call data can be loaded with CALLDATALOAD
    Given call data is 0xfffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffe
    And 0x0 is pushed onto the stack
    When opcode CALLDATALOAD is executed
    Then the stack contains 0xfffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffe

  Scenario: Call data can be loaded with CALLDATALOAD and an offset
    Given call data is 0xfffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffe
    And 0x2 is pushed onto the stack
    When opcode CALLDATALOAD is executed
    Then the stack contains 0x0000fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffe

  Scenario: Call data length can be retrieved with CALLDATASIZE
    Given call data is 0x1234
    When opcode CALLDATASIZE is executed
    Then the stack contains 0x2

  Scenario: Zero call data length can be retrieved with CALLDATASIZE
    Given call data is empty
    When opcode CALLDATASIZE is executed
    Then the stack contains 0x0

  Scenario: Call data can be copied into memory
    Given call data is 0x12345678
    And 0x3 is pushed onto the stack
    And 0x0 is pushed onto the stack
    And 0x2 is pushed onto the stack
    When opcode CALLDATACOPY is executed
    Then 2 bytes of memory from position 3 is 0x1234
    And 3 bytes of memory from position 0 is empty
    And 100 bytes of memory from position 5 is empty

  Scenario: Contract code length can be retrieved with CODESIZE
    Given contract code is [CODESIZE, DUP1, DUP1, BLOCKHASH]
    When the next opcode in the context is executed
    Then the stack contains 0x4

  Scenario: Contract code can be coped into memory with CODECOPY
    Given contract code is [CODECOPY, DUP1, DUP1, BLOCKHASH]
    And 0x3 is pushed onto the stack
    And 0x0 is pushed onto the stack
    And 0x4 is pushed onto the stack
    When the next opcode in the context is executed
    Then 4 bytes of memory from position 3 is 0x39808040
    And 3 bytes of memory from position 0 is empty
    And 100 bytes of memory from position 7 is empty

  Scenario: External contract code can be copied into memory with EXTCODECOPY
    Given contract at address 0x12345 has code [BLOCKHASH, DUP1, DUP1, BLOCKHASH]
    And 0x12345 is pushed onto the stack
    And 0x3 is pushed onto the stack
    And 0x0 is pushed onto the stack
    And 0x4 is pushed onto the stack
    When opcode EXTCODECOPY is executed
    Then 4 bytes of memory from position 3 is 0x40808040
    And 3 bytes of memory from position 0 is empty
    And 100 bytes of memory from position 7 is empty

  Scenario: External contract code size is returned with EXTCODESIZE
    Given contract at address 0x12345 has code [BLOCKHASH, DUP1, DUP1, BLOCKHASH]
    And 0x12345 is pushed onto the stack
    When opcode EXTCODESIZE is executed
    Then the stack contains 0x4

  Scenario: Return data size is returned with RETURNDATASIZE
    Given return data is 0xABCD
    When opcode RETURNDATASIZE is executed
    Then the stack contains 0x2

  Scenario: Return data is copied into memory with RETURNDATACOPY
    Given return data is 0x10203040
    And 0x3 is pushed onto the stack
    And 0x0 is pushed onto the stack
    And 0x4 is pushed onto the stack
    When opcode RETURNDATACOPY is executed
    Then 4 bytes of memory from position 3 is 0x10203040
    And 3 bytes of memory from position 0 is empty
    And 100 bytes of memory from position 7 is empty

  Scenario: blockhash is returned by BLOCKHASH
    Given recent block 5 has hash 0x123
    And 0x5 is pushed onto the stack
    When opcode BLOCKHASH is executed
    Then the stack contains 0x123

  Scenario: coinbase is returned by COINBASE
    Given coinbase is 0x12345
    When opcode COINBASE is executed
    Then the stack contains 0x12345

  Scenario: time is returned by TIMESTAMP
    Given time is "2014-01-01T14:30:01Z"
    When opcode TIMESTAMP is executed
    Then the stack contains a timestamp of "2014-01-01T14:30:01Z"

  Scenario: current block number is returned by NUMBER
    Given current block number is 0x100
    When opcode NUMBER is executed
    Then the stack contains 0x100

  Scenario: current block difficulty is returned by DIFFICULTY
    Given current block difficulty is 0x100
    When opcode DIFFICULTY is executed
    Then the stack contains 0x100

  Scenario: current block gas limit is returned by GASLIMIT
    Given current block gas limit is 0x100
    When opcode GASLIMIT is executed
    Then the stack contains 0x100

  Scenario: element is removed from stack with POP
    Given 0x5 is pushed onto the stack
    When opcode POP is executed
    Then the stack is empty

  Scenario: only one element is removed from stack with POP
    Given 0x5 is pushed onto the stack
    And 0x6 is pushed onto the stack
    When opcode POP is executed
    Then the stack contains 0x5

  Scenario: memory is loaded onto stack with MLOAD
    Given 0x123456 is stored in memory at location 0x100
    And 0x100 is pushed onto the stack
    When opcode MLOAD is executed
    Then the stack contains 0x1234560000000000000000000000000000000000000000000000000000000000

  Scenario: memory is stored from stack with MSTORE
    Given 0x9 is pushed onto the stack
    And 0xaaffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffee is pushed onto the stack
    When opcode MSTORE is executed
    Then 32 bytes of memory from position 9 is 0xaaffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffee
    And 9 bytes of memory from position 0 is empty

  Scenario: a byte of memory is stored from stack with MSTORE8
    Given 0x9 is pushed onto the stack
    And 0xaaffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffee is pushed onto the stack
    When opcode MSTORE8 is executed
    Then 1 byte of memory from position 9 is 0xaa
    And 9 bytes of memory from position 0 is empty
    And 100 bytes of memory from position 10 is empty

  Scenario: data is loaded from storage with SLOAD
    Given 0x123456 is in storage at location 0x100
    And 0x100 is pushed onto the stack
    When opcode SLOAD is executed
    Then the stack contains 0x123456

  Scenario: data is stored in storage from stack with SSTORE
    Given 0x9 is pushed onto the stack
    And 0xaaffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffee is pushed onto the stack
    When opcode SSTORE is executed
    Then data in storage at location 9 is 0xaaffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffee
    And 9 bytes of memory from position 0 is empty

  Scenario: data is stored in storage from stack with SSTORE
    Given 0x9 is pushed onto the stack
    And 0xaaffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffee is pushed onto the stack
    When opcode SSTORE is executed
    Then data in storage at location 9 is 0xaaffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffee
    And 9 bytes of memory from position 0 is empty

  Scenario: can jump to a location in code with JUMP
    Given contract code is [JUMP, DUP1, DUP1, JUMPDEST, SSTORE, GAS]
    And 0x3 is pushed onto the stack
    When the next opcode in the context is executed
    Then the next position in code is now 3

  Scenario: fail when jumping with JUMP to a location without a JUMPDEST
    Given contract code is [JUMP, DUP1, DUP1, JUMPDEST, SSTORE, GAS]
    And 0x4 is pushed onto the stack
    When the next opcode in the context is executed
    Then the last error is now INVALID_JUMP_DESTINATION with message "Invalid jump destination"

  Scenario: fail when jumping with JUMP to a location outside the contract code
    Given contract code is [JUMP, DUP1, DUP1, JUMPDEST, SSTORE, GAS]
    And 0x400 is pushed onto the stack
    When the next opcode in the context is executed
    Then the last error is now INVALID_JUMP_DESTINATION with message "Invalid jump destination"

  Scenario: can jump to a location in code with JUMPI when condition is 1
    Given contract code is [JUMPI, DUP1, DUP1, JUMPDEST, SSTORE, GAS]
    And 0x3 is pushed onto the stack
    And 0x1 is pushed onto the stack
    When the next opcode in the context is executed
    Then the next position in code is now 3

  Scenario: can jump to a location in code with JUMPI when condition is 2
    Given contract code is [JUMPI, DUP1, DUP1, JUMPDEST, SSTORE, GAS]
    And 0x3 is pushed onto the stack
    And 0x2 is pushed onto the stack
    When the next opcode in the context is executed
    Then the next position in code is now 3

  Scenario: fail when jumping with JUMPI to a location without a JUMPDEST
    Given contract code is [JUMPI, DUP1, DUP1, JUMPDEST, SSTORE, GAS]
    And 0x4 is pushed onto the stack
    And 0x1 is pushed onto the stack
    When the next opcode in the context is executed
    Then the last error is now INVALID_JUMP_DESTINATION with message "Invalid jump destination"

  Scenario: fail when jumping with JUMPI to a location outside the contract code
    Given contract code is [JUMPI, DUP1, DUP1, JUMPDEST, SSTORE, GAS]
    And 0x5 is pushed onto the stack
    And 0x1 is pushed onto the stack
    When the next opcode in the context is executed
    Then the last error is now INVALID_JUMP_DESTINATION with message "Invalid jump destination"

  Scenario: won't jump to a location in code with JUMPI when condition is 0
    Given contract code is [JUMPI, DUP1, DUP1, JUMPDEST, SSTORE, GAS]
    And 0x3 is pushed onto the stack
    And 0x0 is pushed onto the stack
    When the next opcode in the context is executed
    Then the next position in code is now 1

  Scenario: contract position is retrieved with PC
    Given contract code is [JUMPDEST, DUP1, DUP1, PC, SSTORE, GAS]
    And the code location is 3
    When the next opcode in the context is executed
    Then the stack contains 0x3

  Scenario: max byte address in memory is returned by MSIZE
    Given 0x123456 is stored in memory at location 0x0
    When opcode MSIZE is executed
    Then the stack contains 0x3

  Scenario: remaining has is returned by GAS
    Given there is 5 gas remaining
    When opcode GAS is executed
    Then the stack contains 0x5

  Scenario: Push opcodes push the right amount of bytes onto the stack
    Given contract code ends with 0xffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff
    When the push opcode is executed it will have data on stack
     | PUSH1  | 0xff |
     | PUSH2  | 0xffff |
     | PUSH3  | 0xffffff |
     | PUSH4  | 0xffffffff |
     | PUSH5  | 0xffffffffff |
     | PUSH6  | 0xffffffffffff |
     | PUSH7  | 0xffffffffffffff |
     | PUSH8  | 0xffffffffffffffff |
     | PUSH9  | 0xffffffffffffffffff |
     | PUSH10 | 0xffffffffffffffffffff |
     | PUSH11 | 0xffffffffffffffffffffff |
     | PUSH12 | 0xffffffffffffffffffffffff |
     | PUSH13 | 0xffffffffffffffffffffffffff |
     | PUSH14 | 0xffffffffffffffffffffffffffff |
     | PUSH15 | 0xffffffffffffffffffffffffffffff |
     | PUSH16 | 0xffffffffffffffffffffffffffffffff |
     | PUSH17 | 0xffffffffffffffffffffffffffffffffff |
     | PUSH18 | 0xffffffffffffffffffffffffffffffffffff |
     | PUSH19 | 0xffffffffffffffffffffffffffffffffffffff |
     | PUSH20 | 0xffffffffffffffffffffffffffffffffffffffff|
     | PUSH21 | 0xffffffffffffffffffffffffffffffffffffffffff |
     | PUSH22 | 0xffffffffffffffffffffffffffffffffffffffffffff |
     | PUSH23 | 0xffffffffffffffffffffffffffffffffffffffffffffff |
     | PUSH24 | 0xffffffffffffffffffffffffffffffffffffffffffffffff |
     | PUSH25 | 0xffffffffffffffffffffffffffffffffffffffffffffffffff |
     | PUSH26 | 0xffffffffffffffffffffffffffffffffffffffffffffffffffff |
     | PUSH27 | 0xffffffffffffffffffffffffffffffffffffffffffffffffffffff |
     | PUSH28 | 0xffffffffffffffffffffffffffffffffffffffffffffffffffffffff |
     | PUSH29 | 0xffffffffffffffffffffffffffffffffffffffffffffffffffffffffff |
     | PUSH30 | 0xffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff |
     | PUSH31 | 0xffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff |
     | PUSH32 | 0xffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff |

   Scenario: the dup opcodes duplicate the stack element at the correct depth
     Given 0x1 is pushed onto the stack
     And 0x2 is pushed onto the stack
     And 0x3 is pushed onto the stack
     And 0x4 is pushed onto the stack
     And 0x5 is pushed onto the stack
     And 0x6 is pushed onto the stack
     And 0x7 is pushed onto the stack
     And 0x8 is pushed onto the stack
     And 0x9 is pushed onto the stack
     And 0x10 is pushed onto the stack
     And 0x11 is pushed onto the stack
     And 0x12 is pushed onto the stack
     And 0x13 is pushed onto the stack
     And 0x14 is pushed onto the stack
     And 0x15 is pushed onto the stack
     And 0x16 is pushed onto the stack
     When the DUP opcode is executed it will have data on stack
      | DUP1  | 0x16 |
      | DUP2  | 0x15 |
      | DUP3  | 0x14 |
      | DUP4  | 0x13 |
      | DUP5  | 0x12 |
      | DUP6  | 0x11 |
      | DUP7  | 0x10 |
      | DUP8  | 0x09 |
      | DUP9  | 0x08 |
      | DUP10 | 0x07 |
      | DUP11 | 0x06 |
      | DUP12 | 0x05 |
      | DUP13 | 0x04 |
      | DUP14 | 0x03 |
      | DUP15 | 0x02 |
      | DUP16 | 0x01 |

  Scenario: the swap opcodes swap the stack element at the correct depth
    Given 0x1 is pushed onto the stack
    And 0x2 is pushed onto the stack
    And 0x3 is pushed onto the stack
    And 0x4 is pushed onto the stack
    And 0x5 is pushed onto the stack
    And 0x6 is pushed onto the stack
    And 0x7 is pushed onto the stack
    And 0x8 is pushed onto the stack
    And 0x9 is pushed onto the stack
    And 0x10 is pushed onto the stack
    And 0x11 is pushed onto the stack
    And 0x12 is pushed onto the stack
    And 0x13 is pushed onto the stack
    And 0x14 is pushed onto the stack
    And 0x15 is pushed onto the stack
    And 0x16 is pushed onto the stack
    And 0xAA is pushed onto the stack
    When the SWAP opcode is executed it will have data on top of stack and 0xAA at index
      | SWAP1  | 0x16 | 1  |
      | SWAP2  | 0x15 | 2  |
      | SWAP3  | 0x14 | 3  |
      | SWAP4  | 0x13 | 4  |
      | SWAP5  | 0x12 | 5  |
      | SWAP6  | 0x11 | 6  |
      | SWAP7  | 0x10 | 7  |
      | SWAP8  | 0x09 | 8  |
      | SWAP9  | 0x08 | 9  |
      | SWAP10 | 0x07 | 10 |
      | SWAP11 | 0x06 | 11 |
      | SWAP12 | 0x05 | 12 |
      | SWAP13 | 0x04 | 13 |
      | SWAP14 | 0x03 | 14 |
      | SWAP15 | 0x02 | 15 |
      | SWAP16 | 0x01 | 16 |

  Scenario: transaction logs are raised with the LOG0 opcode
    Given 0x123456 is stored in memory at location 0x0
    And 0x0 is pushed onto the stack
    And 0x3 is pushed onto the stack
    When opcode LOG0 is executed
    Then a log has been generated with data 0x123456
    And the log has no topics

  Scenario: transaction logs are raised with the LOG1 opcode
    Given 0x123456 is stored in memory at location 0x0
    And 0x0 is pushed onto the stack
    And 0x3 is pushed onto the stack
    And 0xA is pushed onto the stack
    When opcode LOG1 is executed
    Then a log has been generated with data 0x123456
    And the log has topic data
      | 0xA |

  Scenario: transaction logs are raised with the LOG2 opcode
    Given 0x123456 is stored in memory at location 0x0
    And 0x0 is pushed onto the stack
    And 0x3 is pushed onto the stack
    And 0xA is pushed onto the stack
    And 0xB is pushed onto the stack
    When opcode LOG2 is executed
    Then a log has been generated with data 0x123456
    And the log has topic data
      | 0xA |
      | 0xB |

  Scenario: transaction logs are raised with the LOG3 opcode
    Given 0x123456 is stored in memory at location 0x0
    And 0x0 is pushed onto the stack
    And 0x3 is pushed onto the stack
    And 0xA is pushed onto the stack
    And 0xB is pushed onto the stack
    And 0xC is pushed onto the stack
    When opcode LOG3 is executed
    Then a log has been generated with data 0x123456
    And the log has topic data
      | 0xA |
      | 0xB |
      | 0xC |

  Scenario: transaction logs are raised with the LOG4 opcode
    Given 0x123456 is stored in memory at location 0x0
    And 0x0 is pushed onto the stack
    And 0x3 is pushed onto the stack
    And 0xA is pushed onto the stack
    And 0xB is pushed onto the stack
    And 0xC is pushed onto the stack
    And 0xD is pushed onto the stack
    When opcode LOG4 is executed
    Then a log has been generated with data 0x123456
    And the log has topic data
      | 0xA |
      | 0xB |
      | 0xC |
      | 0xD |

  Scenario: a contract is created and deployed with CREATE
    Given the contract address is 0xEE
    And the account with address 0xEE has balance 0x9
    And 0x123456 is stored in memory at location 0x100
    And 0x4 is pushed onto the stack
    And 0x100 is pushed onto the stack
    And 0x3 is pushed onto the stack
    And any new account gets created with address 0xFFFFFF
    When opcode CREATE is executed
    Then the balance of account 0xEE is now 5
    And the balance of account 0xFFFFFF is now 4
    And the code at address 0xFFFFFF is 0x123456
    And the stack contains 0xFFFFFF

  # CALL(gas, address, value, inLocation, inSize, outLocation, outSize)
  Scenario: a call stack element is created with CALL
    Given 0x123456 is stored in memory at location 0x100
    And 0x6A5 is pushed onto the stack
    And 0xADD8E55 is pushed onto the stack
    And 0x999 is pushed onto the stack
    And 0x100 is pushed onto the stack
    And 0x3 is pushed onto the stack
    And 0x200 is pushed onto the stack
    And 0x2 is pushed onto the stack
    And the contract address is 0xEEEEEE
    And the account with address 0xEEEEEE has balance 0x1234
    And there is 0x6A6 gas remaining
    And the account with address 0xADD8E55 has balance 0x0
    When opcode CALL is executed
    Then the call stack is now 2 deep
    And the current call now has the following:
     | type | caller address | calldata | contract address | value | gas   | out location | out size |
     | CALL | 0xEEEEEE       | 0x123456 | 0xADD8E55        | 0x999 | 0x6A5 | 0x200        | 0x2      |
    And the balance of account 0xADD8E55 is now 0x999
    And the balance of account 0xEEEEEE is now 0x89B
    And the previous call gas remaining is now 1

  # CALLCODE(gas, address, value, inLocation, inSize, outLocation, outSize)
  Scenario: a call stack element is created with CALLCODE
    Given 0x123456 is stored in memory at location 0x100
    And 0x6A5 is pushed onto the stack
    And 0xADD8E55 is pushed onto the stack
    And 0x999 is pushed onto the stack
    And 0x100 is pushed onto the stack
    And 0x3 is pushed onto the stack
    And 0x200 is pushed onto the stack
    And 0x2 is pushed onto the stack
    And the current caller address is 0xABCEF
    And the contract address is 0xEEEEEE
    And the account with address 0xEEEEEE has balance 0x1234
    And there is 0x6A6 gas remaining
    And the account with address 0xADD8E55 has balance 0x0
    When opcode CALLCODE is executed
    Then the call stack is now 2 deep
    And the current call now has the following:
      | type     | caller address | calldata | contract address | value | gas   | out location | out size |
      | CALLCODE | 0xEEEEEE       | 0x123456 | 0xEEEEEE         | 0x999 | 0x6A5 | 0x200        | 0x2      |
    And the balance of account 0xADD8E55 is now 0x999
    And the balance of account 0xEEEEEE is now 0x89B
    And the previous call gas remaining is now 1

  # DELEGATECALL(gas, address, inLocation, inSize, outLocation, outSize)
  Scenario: a call stack element is created with DELEGATECALL
    Given 0x123456 is stored in memory at location 0x100
    And 0x6A5 is pushed onto the stack
    And 0xADD8E55 is pushed onto the stack
    And 0x100 is pushed onto the stack
    And 0x3 is pushed onto the stack
    And 0x200 is pushed onto the stack
    And 0x2 is pushed onto the stack
    And the current caller address is 0xABCEF
    And the current call value is 0x111
    And the contract address is 0xEEEEEE
    And the account with address 0xEEEEEE has balance 0x1234
    And there is 0x6A6 gas remaining
    And the account with address 0xADD8E55 has balance 0x0
    When opcode DELEGATECALL is executed
    Then the call stack is now 2 deep
    And the current call now has the following:
      | type         | caller address | calldata | contract address | value | gas   | out location | out size |
      | DELEGATECALL | 0xABCEF        | 0x123456 | 0xEEEEEE         | 0x111 | 0x6A5 | 0x200        | 0x2      |
    And the balance of account 0xADD8E55 is now 0x0
    And the balance of account 0xEEEEEE is now 0x1234
    And the previous call gas remaining is now 1

  # STATICCALL(gas, address, inLocation, inSize, outLocation, outSize)
  Scenario: a call stack element is created with STATICCALL
    Given 0x123456 is stored in memory at location 0x100
    And 0x6A5 is pushed onto the stack
    And 0xADD8E55 is pushed onto the stack
    And 0x100 is pushed onto the stack
    And 0x3 is pushed onto the stack
    And 0x200 is pushed onto the stack
    And 0x2 is pushed onto the stack
    And the current caller address is 0xABCEF
    And the current call value is 0x111
    And the contract address is 0xEEEEEE
    And the account with address 0xEEEEEE has balance 0x1234
    And there is 0x6A6 gas remaining
    And the account with address 0xADD8E55 has balance 0x0
    When opcode STATICCALL is executed
    Then the call stack is now 2 deep
    And the current call now has the following:
      | type         | caller address | calldata | contract address | value | gas   | out location | out size |
      | STATICCALL   | 0xEEEEEE       | 0x123456 | 0xADD8E55        | 0x0   | 0x6A5 | 0x200        | 0x2      |
    And the balance of account 0xADD8E55 is now 0x0
    And the balance of account 0xEEEEEE is now 0x1234
    And the previous call gas remaining is now 1

  Scenario: a contract is created and deployed with CREATE2
    Given the contract address is 0xEE
    And the account with address 0xEE has balance 0x9
    And 0x123456 is stored in memory at location 0x100
    And 0x4 is pushed onto the stack
    And 0x1 is pushed onto the stack
    And 0x100 is pushed onto the stack
    And 0x3 is pushed onto the stack
    When opcode CREATE2 is executed
    Then the balance of account 0xEE is now 5
    And the balance of account 0xb42ef6d8789aa191d5c6f948a528f40153745664 is now 4
    And the code at address 0xb42ef6d8789aa191d5c6f948a528f40153745664 is 0x123456
    And the stack contains 0xb42ef6d8789aa191d5c6f948a528f40153745664

  Scenario: Execution is halted with STOP in main contract
    Given the current call is:
      | type   | caller address | calldata | contract address | value | gas   | out location | out size |
      | CALL   | 0xEEEEEE       | 0x123456 | 0xADD8E55        | 0x0   | 0x6A5 | 0x200        | 0x2      |
    And there is only one call on the stack
    When opcode STOP is executed
    Then the call stack is now 0 deep
    And the execution context is now marked as complete
    And return data is now empty

  Scenario: Execution is halted with STOP in child contract
    Given the previous call is:
      | type   | caller address | calldata | contract address | value | gas   | out location | out size |
      | CALL   | 0xEEEEEE       | 0x123456 | 0xADD8E55        | 0x0   | 0x6A5 | 0x0          | 0x0      |
    And the current call is:
      | type   | caller address | calldata | contract address | value | gas   | out location | out size |
      | CALL   | 0xADD8E55      | 0x123456 | 0xFFFFFFF        | 0x0   | 0x6A5 | 0x200        | 0x2      |
    When opcode STOP is executed
    Then the call stack is now 1 deep
    And the execution context is now marked as not complete
    And return data is now empty
    And the stack contains 0x1

  Scenario: Execution is halted with RETURN in child contract
    Given the previous call is:
      | type   | caller address | calldata | contract address | value | gas   | out location | out size |
      | CALL   | 0xEEEEEE       | 0x123456 | 0xADD8E55        | 0x0   | 0x6A5 | 0x0          | 0x0      |
    And the current call is:
      | type   | caller address | calldata | contract address | value | gas   | out location | out size |
      | CALL   | 0xADD8E55      | 0x123456 | 0xFFFFFFF        | 0x0   | 0x6A5 | 0x200        | 0x3      |
    And 0x123456 is stored in memory at location 0x100
    And 0x100 is pushed onto the stack
    And 0x3 is pushed onto the stack
    When opcode RETURN is executed
    Then the call stack is now 1 deep
    And the execution context is now marked as not complete
    And return data is now 0x123456
    And 3 bytes of memory from position 0x200 is 0x123456
    And the stack contains 0x1

  Scenario: Execution is halted with INVALID in main contract
    Given the current call is:
      | type   | caller address | calldata | contract address | value | gas   | out location | out size |
      | CALL   | 0xADD8E55      | 0x123456 | 0xFFFFFFF        | 0x0   | 0x6A5 | 0x200        | 0x3      |
    And there is only one call on the stack
    When opcode INVALID is executed
    Then the call stack is now 0 deep
    And the execution context is now marked as complete

  Scenario: Execution is halted with INVALID in child contract
    Given the previous call is:
      | type   | caller address | calldata | contract address | value | gas   | out location | out size |
      | CALL   | 0xEEEEEE       | 0x123456 | 0xADD8E55        | 0x0   | 0x6A5 | 0x0          | 0x0      |
    And the current call is:
      | type   | caller address | calldata | contract address | value | gas   | out location | out size |
      | CALL   | 0xADD8E55      | 0x123456 | 0xFFFFFFF        | 0x0   | 0x6A5 | 0x200        | 0x3      |
    When opcode INVALID is executed
    Then the call stack is now 1 deep
    And the execution context is now marked as not complete
    And return data is now empty
    And the last error is now INVALID_INSTRUCTION with message "Invalid instruction"
    And the stack contains 0x0

  Scenario: Execution is halted with REVERT in main contract
    Given the current call is:
      | type   | caller address | calldata | contract address | value | gas   | out location | out size |
      | CALL   | 0xADD8E55      | 0x123456 | 0xFFFFFFF        | 0x0   | 0x6A5 | 0x200        | 0x3      |
    And there is only one call on the stack
    And 0x123456 is stored in memory at location 0x100
    And 0x100 is pushed onto the stack
    And 0x3 is pushed onto the stack
    When opcode REVERT is executed
    Then the call stack is now 0 deep
    And the execution context is now marked as complete

  Scenario: Execution is halted with REVERT in child contract
    Given the previous call is:
      | type   | caller address | calldata | contract address | value | gas   | out location | out size |
      | CALL   | 0xEEEEEE       | 0x123456 | 0xADD8E55        | 0x0   | 0x6A5 | 0x0          | 0x0      |
    And the current call is:
      | type   | caller address | calldata | contract address | value | gas   | out location | out size |
      | CALL   | 0xADD8E55      | 0x123456 | 0xFFFFFFF        | 0x0   | 0x6A5 | 0x200        | 0x3      |
    And 0x123456 is stored in memory at location 0x100
    And 0x100 is pushed onto the stack
    And 0x3 is pushed onto the stack
    When opcode REVERT is executed
    Then the call stack is now 1 deep
    And the execution context is now marked as not complete
    And return data is now 0x123456
    And 3 bytes of memory from position 0x200 is 0x123456
    And the stack contains 0x0

  Scenario: Execution is halted with SUICIDE in main contract
    Given the current call is:
      | type   | caller address | calldata | contract address | value | gas   | out location | out size |
      | CALL   | 0xADD8E55      | 0x123456 | 0xFFFFFFF        | 0x0   | 0x6A5 | 0x200        | 0x3      |
    And there is only one call on the stack
    And the account with address 0xFFFFFFF has balance 0x1234
    And 0xAAAAAAA is pushed onto the stack
    When opcode SUICIDE is executed
    Then the call stack is now 0 deep
    And the execution context is now marked as complete
    And the balance of account 0xAAAAAAA is now 0x1234
    And the balance of account 0xFFFFFFF is now 0
    And the code at address 0xFFFFFFF is empty

  Scenario: Execution is halted with SUICIDE in child contract
    Given the previous call is:
      | type   | caller address | calldata | contract address | value | gas   | out location | out size |
      | CALL   | 0xEEEEEE       | 0x123456 | 0xADD8E55        | 0x0   | 0x6A5 | 0x0          | 0x0      |
    And the current call is:
      | type   | caller address | calldata | contract address | value | gas   | out location | out size |
      | CALL   | 0xADD8E55      | 0x123456 | 0xFFFFFFF        | 0x0   | 0x6A5 | 0x200        | 0x3      |
    And the account with address 0xFFFFFFF has balance 0x1234
    And 0xAAAAAAA is pushed onto the stack
    When opcode SUICIDE is executed
    Then the call stack is now 1 deep
    And the execution context is now marked as not complete
    And the balance of account 0xAAAAAAA is now 0x1234
    And the balance of account 0xFFFFFFF is now 0
    And the code at address 0xFFFFFFF is empty
    And the stack contains 0x1

  Scenario: Execution is halted with unknown opcode in main contract
    Given the current call is:
      | type   | caller address | calldata | contract address | value | gas   | out location | out size |
      | CALL   | 0xADD8E55      | 0x123456 | 0xFFFFFFF        | 0x0   | 0x6A5 | 0x200        | 0x3      |
    And there is only one call on the stack
    When opcode 0xBB is executed
    Then the call stack is now 0 deep
    And the execution context is now marked as complete
    And the last error is now INVALID_INSTRUCTION with message "Invalid instruction - unknown opcode 0xbb"

  Scenario: fail when not enough elements on the stack
    Given 0x5 is pushed onto the stack
    When opcode ADD is executed
    Then the last error is now STACK_DEPTH with message "Stack not deep enough for ADD"

  Scenario: Can call any of the read-only opcodes in static context
    Given the current call type is any of
      | STATICCALL |
    And 0x01 is pushed onto the stack
    And 0x02 is pushed onto the stack
    And 0x03 is pushed onto the stack
    # TODO - add a bunch of opcodes here
    And the opcode is any of
      | ADD |
    When the next opcode in the context is executed
    Then there is no last error

  Scenario: fail if trying to execute non-read only opcodes in static context
    Given the current call type is any of
      | STATICCALL |
    And 0x01 is pushed onto the stack
    And 0x02 is pushed onto the stack
    And 0x03 is pushed onto the stack
    # TODO - add a bunch of opcodes here
    And the opcode is any of
      | LOG0 |
    When the next opcode in the context is executed
    Then the last error is now STATE_CHANGE_STATIC_CALL with message "LOG0 not allowed in static call"

  Scenario: Execution is halted at end of main contract
    Given contract code is [DUP1, DUP1, GAS]
    And the code location is 3
    When the next opcode in the context is executed
    Then the execution context is now marked as complete
    And return data is now empty

