Feature: Check gas costs of SUICIDE / SELFDESTRUCT operation

  Background:
    Given there is 40000 gas remaining
    And the current caller address is 0xCA11E7
    And the contract address is 0xC0417AC1
    And contract at address 0xC0417AC1 has code [SUICIDE]
    And the account with address 0xC0417AC1 has balance 0x10
    And 0xADD8E55 is pushed onto the stack

  Scenario: Suicide gas cost when destination account exists is 5000
    Given an account with address 0xADD8E55 exists
    When opcode SUICIDE is executed
    And account 0xCA11E7 has a refund of 35000

  Scenario: Suicide gas cost when destination account does not exist is 30000
    Given there is no existing account with address 0xADD8E55
    When opcode SUICIDE is executed
    And account 0xCA11E7 has a refund of 10000
