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

