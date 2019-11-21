Feature: Gas costs for CREATE operations

  Scenario: Gas cost for CREATE
    Given 0x0 is pushed onto the stack
    And 0x0 is pushed onto the stack
    And 0x0 is pushed onto the stack
    When opcode CREATE is executed
    Then 32000 gas is now used

  Scenario: Gas cost for CREATE2
    Given 0x0 is pushed onto the stack
    And 0x0 is pushed onto the stack
    And 0x0 is pushed onto the stack
    And 0x0 is pushed onto the stack
    When opcode CREATE2 is executed
    Then 32000 gas is now used
