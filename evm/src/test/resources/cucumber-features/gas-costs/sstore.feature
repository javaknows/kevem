Feature: Gas costs for SSTORE operation

  Background:
    Given there is 30000 gas remaining
    And the current caller address is 0xCA11E7
    And the contract address is 0xC0417AC1
    And contract at address 0xC0417AC1 has code [SSTORE, JUMPDEST, STOP]

  Scenario: SSTORE gas costs when setting zero value to non-zero
    Given 0x0 is in storage at location 0x69 of 0xC0417AC1
    And 0x420 is pushed onto the stack
    And 0x69 is pushed onto the stack
    When opcode SSTORE is executed
    Then 20000 gas is now used

  Scenario: SSTORE gas costs when setting non-zero value to zero
    Given 0x0 is in storage at location 0x69 of 0xC0417AC1
    And 0x0 is pushed onto the stack
    And 0x69 is pushed onto the stack
    When opcode SSTORE is executed
    Then 5000 gas is now used

  Scenario: SSTORE gas costs when setting non-zero value to non-zero
    Given 0x420 is in storage at location 0x69 of 0xC0417AC1
    And 0x666 is pushed onto the stack
    And 0x69 is pushed onto the stack
    When opcode SSTORE is executed
    Then 5000 gas is now used