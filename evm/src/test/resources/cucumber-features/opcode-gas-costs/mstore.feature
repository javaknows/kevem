Feature: Gas costs for MSTORE and MSTORE8 operations

  Background:
    Given there is 500 gas remaining
    And the current caller address is 0xCA11E7
    And the contract address is 0xC0417AC1
    And contract at address 0xC0417AC1 has code [MSTORE, JUMPDEST, STOP]

  Scenario: MSTORE gas cost when no new memory is used
    Given 0x1234567890123456789012345678901234567890123456789012345678901234 is stored in memory at location 0x0
    And 0x69 is pushed onto the stack
    And 0x0 is pushed onto the stack
    When opcode MSTORE is executed
    Then 3 gas is now used

  Scenario: MSTORE gas cost when 1 new word of memory is used
    Given 0x69 is pushed onto the stack
    And 0x0 is pushed onto the stack
    When opcode MSTORE is executed
    Then 6 gas is now used

  Scenario: MSTORE8 gas cost when no new memory is used
    Given 0x1 is stored in memory at location 0x0
    And 0x69 is pushed onto the stack
    And 0x0 is pushed onto the stack
    When opcode MSTORE8 is executed
    Then 3 gas is now used

  Scenario: MSTORE8 gas cost when 1 new word of memory is used
    Given 0x69 is pushed onto the stack
    And 0x0 is pushed onto the stack
    When opcode MSTORE8 is executed
    Then 6 gas is now used
