Feature: Gas costs for SHA3 operation

  Scenario: SHA3 gas cost when num words is zero reading existing memory
    Given the stack contains elements [0x0, 0x0]
    And some data is stored in memory at location 0x0
    When opcode SHA3 is executed
    Then 30 gas is now used

  Scenario: SHA3 gas cost when num words is 1 reading existing memory
    Given the stack contains elements [0x0, 0x1]
    And some data is stored in memory at location 0x0
    When opcode SHA3 is executed
    Then 36 gas is now used

  Scenario: SHA3 gas cost when num words is 2 reading existing memory
    Given the stack contains elements [0x0, 0x21]
    And some data is stored in memory at location 0x0
    And some data is stored in memory at location 0x20
    When opcode SHA3 is executed
    Then 42 gas is now used