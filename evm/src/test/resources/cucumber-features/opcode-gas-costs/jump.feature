Feature: Gas costs for JUMP and JUMPI operations

  Scenario: JUMP gas cost check
    Given there is 30000 gas remaining
    And contract at address 0xC0417AC1 has code [JUMP, JUMPDEST, STOP]
    And 0x1 is pushed onto the stack
    When opcode JUMP is executed
    Then 8 gas is now used

  Scenario: JUMPI gas cost check
    Given there is 30000 gas remaining
    And contract at address 0xC0417AC1 has code [JUMPI, ADDRESS, JUMPDEST, STOP]
    And 0x2 is pushed onto the stack
    And 0x1 is pushed onto the stack
    When opcode JUMPI is executed
    Then 10 gas is now used
