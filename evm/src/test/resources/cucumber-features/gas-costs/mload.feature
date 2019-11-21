Feature: Gas costs for MLOAD operation

  Background:
    Given there is 500 gas remaining
    And the current caller address is 0xCA11E7
    And the contract address is 0xC0417AC1
    And contract at address 0xC0417AC1 has code [MLOAD, JUMPDEST, STOP]

  Scenario: MLOAD gas cost when no new memory is used
    Given some data is stored in memory at location 0x0
    And 0x0 is pushed onto the stack
    When opcode MLOAD is executed
    Then 3 gas is now used

  Scenario: MLOAD gas cost when 1 new word of memory is used
    Given 0x0 is pushed onto the stack
    When opcode MLOAD is executed
    Then 6 gas is now used