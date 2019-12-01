Feature: Gas costs for halting opcodes

  Scenario Outline: <opcode> doesn't cost gas
    Given there is 1000 gas remaining
    And there is 40 gas used
    And the stack contains elements [0x0, 0x0]
    When opcode <opcode> is executed
    Then the transaction has now used 40 gas

    Examples:
      | opcode |
      | REVERT |
      | STOP   |
      | RETURN |
