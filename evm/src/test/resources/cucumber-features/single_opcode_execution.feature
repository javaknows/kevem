Feature: Single Opcode Execution
  Check that each opcode executes correctly

  Scenario: Two numbers can be added using ADD
    Given 0x01 is pushed onto the stack
    And 0x02 is pushed onto the stack
    And opcode ADD is executed
    Then the stack contains 0x03

  Scenario: Contract address is correct using ADDRESS
    Given the contract address is 0xEE
    And opcode ADDRESS is executed
    Then the stack contains 0xEE

  Scenario: Balance of an address is retrieved with BALANCE
    Given an account with address 0xAA has balance 0x123
    And 0xAA is pushed onto the stack
    And opcode BALANCE is executed
    Then the stack contains 0x123

  Scenario: Transaction origin is correct using ORIGIN
    Given transaction origin is 0xBB
    And opcode ORIGIN is executed
    Then the stack contains 0xBB

    