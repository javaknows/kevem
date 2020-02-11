Feature: EIP 1884: Repricing for trie-size-dependent opcodes

  # https://eips.ethereum.org/EIPS/eip-1884

  Background: Plenty gas available and plenty balance available
    Given there is 500000 gas remaining
    And the contract address is 0xC0476AC7
    And the account with address 0xC0476AC7 has balance 0x1000


  Scenario Outline: SLOAD gas is correct when <eip> is enabled
    Given EIP <eip> is enabled
    And 0x1 is pushed onto the stack
    When opcode SLOAD is executed
    Then <gas> gas is now used

    Examples:
      | eip     | gas |
      | EIP150  | 200 |
      | EIP1884 | 800 |


  Scenario Outline: BALANCE gas is correct when <eip> is enabled
    Given EIP <eip> is enabled
    And 0x1 is pushed onto the stack
    When opcode SLOAD is executed
    Then <gas> gas is now used

    Examples:
      | eip     | gas |
      | EIP150  | 200 |
      | EIP1884 | 800 |


  Scenario Outline: BALANCE gas is correct when <eip> is enabled
    Given EIP <eip> is enabled
    And an account with address 0xAABBCCDDEEFF exists
    And contract at address 0xAABBCCDDEEFF has code [STOP]
    And 0xAABBCCDDEEFF is pushed onto the stack
    When opcode EXTCODEHASH is executed
    Then <gas> gas is now used

    Examples:
      | eip     | gas |
      | EIP1052 | 400 |
      | EIP1884 | 700 |


  Scenario: SELFBALANCE is correct when EIP1884 is enabled
    Given EIP EIP1884 is enabled
    And the contract address is 0xAABBCCDDEEFF
    And contract account with address 0xAABBCCDDEEFF has balance 0x1234
    When opcode SELFBALANCE is executed
    Then the stack contains 0x1234
    And 5 gas is now used

