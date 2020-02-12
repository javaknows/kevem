Feature: EIP 170: Contract code size limit

  # https://eips.ethereum.org/EIPS/eip-170

  Background: Plenty gas available and plenty balance available
    Given there is 500000 gas remaining
    And the contract address is 0xC0476AC7
    And the account with address 0xC0476AC7 has balance 0x1000


  Scenario Outline: contract creation with <opcode> fails when EIP-170 is enabled and contract size is over 0x6000 bytes
    Given EIP EIP170 is enabled
    And 0x12 repeated 0x6001 times is stored in memory at 0x100
    And the stack contains elements <stack>
    When opcode <opcode> is executed
    Then the last error is now OUT_OF_GAS with message "Out of gas"

    Examples:
      | opcode  | stack                     |
      | CREATE  | [0x0, 0x100, 0x6001]      |
      | CREATE2 | [0x4, 0x1, 0x100, 0x6001] |


  Scenario Outline: contract creation with <opcode> succeeds when EIP-170 is not enabled and contract size is over 0x6000 bytes
    Given EIP EIP170 is not enabled
    And 0x12 repeated 0x6001 times is stored in memory at 0x100
    And the stack contains elements <stack>
    When opcode <opcode> is executed
    Then there is no last error

    Examples:
      | opcode  | stack                     |
      | CREATE  | [0x0, 0x100, 0x6001]      |
      | CREATE2 | [0x4, 0x1, 0x100, 0x6001] |