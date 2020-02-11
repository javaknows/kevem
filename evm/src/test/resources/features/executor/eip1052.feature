Feature: EIP 1052: EXTCODEHASH opcode

  # https://eips.ethereum.org/EIPS/eip-1052

  Background: Plenty gas available and plenty balance available and EIP1052 is enabled
    Given EIP EIP1052 is enabled
    And there is 500000 gas remaining
    And the contract address is 0xC0476AC7
    And the account with address 0xC0476AC7 has balance 0x1000

  Scenario: Hash is zero if account does not exist
    Given there is no existing account with address 0xAABBCCDDEEFF
    And 0xAABBCCDDEEFF is pushed onto the stack
    When opcode EXTCODEHASH is executed
    Then the stack contains 0x0
    And 400 gas is now used

  Scenario: Hash is set when account exists but code is empty
    Given an account with address 0xAABBCCDDEEFF exists
    And 0xAABBCCDDEEFF is pushed onto the stack
    When opcode EXTCODEHASH is executed
    Then the stack contains 0xc5d2460186f7233c927e7db2dcc703c0e500b653ca82273b7bfad8045d85a470
    And 400 gas is now used

  Scenario: Hash is set when account exists and code code is non-empty
    Given an account with address 0xAABBCCDDEEFF exists
    And contract at address 0xAABBCCDDEEFF has code [STOP]
    And 0xAABBCCDDEEFF is pushed onto the stack
    When opcode EXTCODEHASH is executed
    Then the stack contains 0xbc36789e7a1e281436464229828f817d6612f7b477d66591ff96a9e064bcc98a
    And 400 gas is now used


