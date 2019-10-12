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
    When the context is executed
    Then the stack contains 0x4

  Scenario: Contract code can be coped into memory with CODECOPY
    Given contract code is [CODECOPY, DUP1, DUP1, BLOCKHASH]
    And 0x3 is pushed onto the stack
    And 0x0 is pushed onto the stack
    And 0x4 is pushed onto the stack
    When the context is executed
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
    When the context is executed
    Then the position in code is 3

  Scenario: can jump to a location in code with JUMPI when condition is 1
    Given contract code is [JUMPI, DUP1, DUP1, JUMPDEST, SSTORE, GAS]
    And 0x3 is pushed onto the stack
    And 0x1 is pushed onto the stack
    When the context is executed
    Then the position in code is 3

  Scenario: can jump to a location in code with JUMPI when condition is 2
    Given contract code is [JUMPI, DUP1, DUP1, JUMPDEST, SSTORE, GAS]
    And 0x3 is pushed onto the stack
    And 0x2 is pushed onto the stack
    When the context is executed
    Then the position in code is 3

  @Ignore
  Scenario: won't jump to a location in code with JUMPI when condition is 0
    Given contract code is [JUMPI, DUP1, DUP1, JUMPDEST, SSTORE, GAS]
    And 0x3 is pushed onto the stack
    And 0x0 is pushed onto the stack
    When the context is executed
    Then the position in code is 1

  Scenario: contract position is retrieved with PC
    Given contract code is [PC, DUP1, DUP1, JUMPDEST, SSTORE, GAS]
    And contract position is 3
    When the context is executed
    Then the stack contains 0

  Scenario: max byte address in memory is returned by MSIZE
    Given 0x123456 is stored in memory at location 0x0
    When opcode MSIZE is executed
    Then the stack contains 0x3

  Scenario: remaining has is returned by GAS
    Given there is 5 gas remaining
    When opcode GAS is executed
    Then the stack contains 0x5

