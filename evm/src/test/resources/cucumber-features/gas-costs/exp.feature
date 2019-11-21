Feature: Gas costs for EXP operation

  Scenario: EXP gas cost when exponent is zero
    Given 0x0 is pushed onto the stack
    And 0x1 is pushed onto the stack
    When opcode EXP is executed
    Then 10 gas is now used

  Scenario: EXP gas cost when exponent is non-zero
    Given 0xFF1 is pushed onto the stack
    And 0x1 is pushed onto the stack
    When opcode EXP is executed
    Then 30 gas is now used