Feature: Program counter increments correctly when returning from calls

  Background: Plenty gas available and plenty balance available
    Given there is 500000 gas remaining
    And the contract address is 0xC0476AC7
    And the account with address 0xC0476AC7 has balance 0x1000000000000

  Scenario: One level of delegation with no terminating halt operation in delegate
    Given an account with address 0xADD7E55 exists
    And contract code is [PUSH1, 0x0, PUSH1, 0x0, PUSH1, 0x0, PUSH1, 0x0, PUSH2, 0x100, PUSH3, 0xBBBBBB, PUSH2, 0x9000, CALL, PUSH1, 0x0, ADD]
    And contract at address 0xBBBBBB has code [PC, NUMBER, TIMESTAMP]
    When the context is executed to completion
    Then the execution context is now marked as complete
    And there is no last error

  Scenario: One level of delegation with halt operation in delegate
    Given an account with address 0xADD7E55 exists
    And contract code is [PUSH1, 0x0, PUSH1, 0x0, PUSH1, 0x0, PUSH1, 0x0, PUSH2, 0x100, PUSH3, 0xBBBBBB, PUSH2, 0x9000, CALL, PUSH1, 0x0, ADD]
    And contract at address 0xBBBBBB has code [PC, NUMBER, STOP]
    When the context is executed to completion
    Then the execution context is now marked as complete
    And there is no last error

  Scenario: One level of delegation with EVM error in delegate
    Given an account with address 0xADD7E55 exists
    And contract code is [PUSH1, 0x0, PUSH1, 0x0, PUSH1, 0x0, PUSH1, 0x0, PUSH2, 0x100, PUSH3, 0xBBBBBB, PUSH2, 0x9000, CALL, PUSH1, 0x0, ADD]
    And contract at address 0xBBBBBB has code [PC, NUMBER, INVALID]
    When the context is executed to completion
    Then the execution context is now marked as complete
    And there is no last error