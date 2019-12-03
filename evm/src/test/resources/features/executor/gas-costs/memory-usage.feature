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
      | one word of memory is used                          | 0x1    | 9    | 6 (base) + 3 + 0       |
      | upper bound of 1 word of memory is used             | 0x20   | 9    | 6 (base) + 3 + 0       |
      | lower bound of 2 words of memory is used            | 0x21   | 15   | 9 (base) + 6 + 0       |
      | very high memeory hitting non-zero quadratic factor | 0x2100 | 1723 | 795 (base) + 792 + 136 |

  Scenario: MLOAD uses charges for one word of memory if not previously accessed
    Given the stack contains elements [0x0]
    When opcode MLOAD is executed
    Then 6 gas is now used
    # MLOAD costs 3 without new memory access
    And opcode MLOAD is executed

  Scenario: MLOAD uses charges for two words of memory if not previously accessed
    # writing a word into memory location 0x1 will cause the highest referenced index to be 0x21 - boundary of two bytes
    Given the stack contains elements [0x1]
    When opcode MLOAD is executed
    Then 9 gas is now used
    # MLOAD costs 3 without new memory access
    And opcode MLOAD is executed

  Scenario: MSTORE uses charges for one word of memory if not previously accessed
    Given the stack contains elements [0x0, 0x69]
    When opcode MSTORE is executed
    Then 6 gas is now used
    # MSTORE costs 3 without new memory access
    And opcode MSTORE is executed

  Scenario: MSTORE uses charges for two words of memory if not previously accessed
    # writing a word into memory location 0x1 will cause the highest referenced index to be 0x21 - boundary of two bytes
    Given the stack contains elements [0x1, 0x69]
    When opcode MSTORE is executed
    Then 9 gas is now used
    # MSTORE costs 3 without new memory access
    And opcode MSTORE is executed


  Scenario: MSTORE8 uses charges for one word of memory if not previously accessed
    Given the stack contains elements [0x0, 0x69]
    When opcode MSTORE8 is executed
    Then 6 gas is now used
    # MSTORE8 costs 3 without new memory access
    And opcode MSTORE8 is executed

  Scenario Outline: MSTORE8 uses charges correct gas charges word when writing to index <index> / <gas>
    Given the stack contains elements [<index>, 0x69]
    When opcode MSTORE8 is executed
    Then <gas> gas is now used
    # MSTORE8 costs 3 without new memory access
    And opcode MSTORE8 is executed

    Examples:
      | index | gas |
      | 0x0   | 6   |
      | 0x1   | 6   |
      | 0x19  | 6   |
      | 0x20  | 9   |
      | 0x21  | 9   |
      | 0x39  | 9   |
