Feature: Gas costs for memory use

  Note that CALLDATACOPY has a base cost of 2 + num bytes rounded up

  Background: Plenty gas available and plenty balance available
    Given there is 500000 gas remaining
    And the contract address is 0xC0476AC7
    And the account with address 0xC0476AC7 has balance 0x1000

  Scenario Outline: <name>
    Given the stack contains elements [0x0, 0x0, <length>]
    When opcode CALLDATACOPY is executed
    Then <gas> gas is now used

    Examples:
      | name                                                | length | gas  | notes                  |
      | one word of memory is used                          | 0x1    | 8    | 5 (base) + 3 + 0       |
      | upper bound of 1 word of memory is used             | 0x20   | 8    | 5 (base) + 3 + 0       |
      | lower bound of 2 words of memory is used            | 0x21   | 14   | 8 (base) + 6 + 0       |
      | very high memeory hitting non-zero quadratic factor | 0x2100 | 1722 | 794 (base) + 792 + 136 |
