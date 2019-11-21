Feature: Gas costs for LOG operations

  Scenario: LOG0 gas cost when no memory is used
    Given 0x0 is pushed onto the stack
    And 0x0 is pushed onto the stack
    When opcode LOG0 is executed
    Then 375 gas is now used

  Scenario: LOG0 gas cost when 2 bytes of already used memory is logged
    Given 0x2 is pushed onto the stack
    And 0x0 is pushed onto the stack
    And some data is stored in memory at location 0x0
    When opcode LOG0 is executed
    Then 391 gas is now used

  Scenario: LOG0 gas cost when 2 bytes of new memory is logged
    Given 0x2 is pushed onto the stack
    And 0x0 is pushed onto the stack
    When opcode LOG0 is executed
    Then 394 gas is now used

  Scenario: LOG1 gas cost when no memory is used
    Given 0x69 is pushed onto the stack
    And 0x0 is pushed onto the stack
    And 0x0 is pushed onto the stack
    When opcode LOG1 is executed
    Then 750 gas is now used

  Scenario: LOG1 gas cost when 2 bytes of already used memory is logged
    Given 0x69 is pushed onto the stack
    And 0x2 is pushed onto the stack
    And 0x0 is pushed onto the stack
    And some data is stored in memory at location 0x0
    When opcode LOG1 is executed
    Then 766 gas is now used

  Scenario: LOG2 gas cost when no memory is used
    Given 0x69 is pushed onto the stack
    And 0x69 is pushed onto the stack
    And 0x0 is pushed onto the stack
    And 0x0 is pushed onto the stack
    When opcode LOG2 is executed
    Then 1125 gas is now used

  Scenario: LOG2 gas cost when 2 bytes of already used memory is logged
    Given 0x69 is pushed onto the stack
    And 0x69 is pushed onto the stack
    And 0x2 is pushed onto the stack
    And 0x0 is pushed onto the stack
    And some data is stored in memory at location 0x0
    When opcode LOG2 is executed
    Then 1141 gas is now used

  Scenario: LOG3 gas cost when no memory is used
    Given 0x69 is pushed onto the stack
    And 0x69 is pushed onto the stack
    And 0x69 is pushed onto the stack
    And 0x0 is pushed onto the stack
    And 0x0 is pushed onto the stack
    When opcode LOG3 is executed
    Then 1500 gas is now used

  Scenario: LOG3 gas cost when 2 bytes of already used memory is logged
    Given 0x69 is pushed onto the stack
    And 0x69 is pushed onto the stack
    And 0x69 is pushed onto the stack
    And 0x2 is pushed onto the stack
    And 0x0 is pushed onto the stack
    And some data is stored in memory at location 0x0
    When opcode LOG3 is executed
    Then 1516 gas is now used

  Scenario: LOG4 gas cost when no memory is used
    Given 0x69 is pushed onto the stack
    And 0x69 is pushed onto the stack
    And 0x69 is pushed onto the stack
    And 0x69 is pushed onto the stack
    And 0x0 is pushed onto the stack
    And 0x0 is pushed onto the stack
    When opcode LOG4 is executed
    Then 1875 gas is now used

  Scenario: LOG4 gas cost when 2 bytes of already used memory is logged
    Given 0x69 is pushed onto the stack
    And 0x69 is pushed onto the stack
    And 0x69 is pushed onto the stack
    And 0x69 is pushed onto the stack
    And 0x2 is pushed onto the stack
    And 0x0 is pushed onto the stack
    And some data is stored in memory at location 0x0
    When opcode LOG4 is executed
    Then 1891 gas is now used
