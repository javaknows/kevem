Feature: Gas costs for SHA3 operation

  Scenario: SHA3 gas cost num words is zero reading existing memory
    Given 0x0 is pushed onto the stack
    And 0x0 is pushed onto the stack
    And some data is stored in memory at location 0x0
    When opcode SHA3 is executed
    Then 30 gas is now used

  Scenario: SHA3 gas cost num words is 1 reading existing memory
    Given 0x1 is pushed onto the stack
    And 0x0 is pushed onto the stack
    And some data is stored in memory at location 0x0
    When opcode SHA3 is executed
    Then 36 gas is now used

  Scenario: SHA3 gas cost num words is 2 reading existing memory
    Given 0x20 is pushed onto the stack
    And 0x0 is pushed onto the stack
    And some data is stored in memory at location 0x0
    When opcode SHA3 is executed
    Then 42 gas is now used