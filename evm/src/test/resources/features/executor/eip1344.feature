Feature: EIP 1344: ChainID opcode

  # https://eips.ethereum.org/EIPS/eip-1344

  Background: Plenty gas available and plenty balance available
    Given there is 500000 gas remaining
    And the contract address is 0xC0476AC7
    And the account with address 0xC0476AC7 has balance 0x1000

  Scenario: Chain ID is returned as per configuration
    Given the chain ID is 7
    When opcode CHAINID is executed
    Then the stack contains 0x7
    And 2 gas is now used