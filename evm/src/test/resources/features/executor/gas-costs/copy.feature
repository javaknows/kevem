Feature: Gas costs for copy operations

  Scenario Outline: <opcode> gas cost when using previously used memory
    Given some data is stored in memory at location 0x0
    And 0x2 is pushed onto the stack
    And 0x0 is pushed onto the stack
    And 0x0 is pushed onto the stack
    When opcode <opcode> is executed
    Then 6 gas is now used

    Examples:
      | opcode         |
      | CALLDATACOPY   |
      | RETURNDATACOPY |
      | CODECOPY       |

  Scenario Outline: <opcode> gas cost when using new word of memory
    Given 0x2 is pushed onto the stack
    And 0x0 is pushed onto the stack
    And 0x0 is pushed onto the stack
    When opcode <opcode> is executed
    Then 9 gas is now used

    Examples:
      | opcode         |
      | CALLDATACOPY   |
      | RETURNDATACOPY |
      | CODECOPY       |

  Scenario: EXTCODECOPY gas cost when using previously used memory
    Given some data is stored in memory at location 0x0
    And 0x2 is pushed onto the stack
    And 0x0 is pushed onto the stack
    And 0x0 is pushed onto the stack
    And 0xADD7E55 is pushed onto the stack
    When opcode EXTCODECOPY is executed
    Then 703 gas is now used

  Scenario: EXTCODECOPY gas cost when using new word of memory
    Given 0x2 is pushed onto the stack
    And 0x0 is pushed onto the stack
    And 0x0 is pushed onto the stack
    And 0xADD7E55 is pushed onto the stack
    When opcode EXTCODECOPY is executed
    Then 706 gas is now used