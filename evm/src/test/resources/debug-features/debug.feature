Feature: Debugging

  Run single scenarios in here

  # DELEGATECALL(gas, address, inLocation, inSize, outLocation, outSize)
  Scenario: a call stack element is created with DELEGATECALL
    Given 0x123456 is stored in memory at location 0x100
    And 0x2 is pushed onto the stack
    And 0x200 is pushed onto the stack
    And 0x3 is pushed onto the stack
    And 0x100 is pushed onto the stack
    And 0xADD8E55 is pushed onto the stack
    And 0x6A5 is pushed onto the stack
    And the current caller address is 0xABCEF
    And the current call value is 0x111
    And the contract address is 0xEEEEEE
    And the account with address 0xEEEEEE has balance 0x1234
    And there is 0x6A6 gas remaining
    And the account with address 0xADD8E55 has balance 0x0
    When opcode DELEGATECALL is executed
    Then the call stack is now 2 deep
    And the current call now has the following:
      | type         | caller address | calldata | contract address | value | gas   | out location | out size |
      | DELEGATECALL | 0xABCEF        | 0x123456 | 0xADD8E55        | 0x111 | 0x6A5 | 0x200        | 0x2      |
    And the balance of account 0xADD8E55 is now 0x0
    And the balance of account 0xEEEEEE is now 0x1234
    And the previous call gas remaining is now 1