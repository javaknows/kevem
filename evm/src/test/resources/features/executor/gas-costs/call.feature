Feature: Gas costs for CALL type operations

  Background: Plenty gas available and plenty balance available
    Given there is 500000 gas remaining
    And the contract address is 0xC0476AC7
    And the account with address 0xC0476AC7 has balance 0x1000

  Scenario Outline: Gas cost for <opcode> when sending value to new account
    Given there is no existing account with address 0xADD7E55
    And the stack contains elements [0x1, 0xADD7E55, 0x100, 0x0, 0x0, 0x0, 0x0]
    When opcode <opcode> is executed
    Then 34700 gas is now used by the previous call context
    # G_call + G_newaccount + G_callvalue = 700 + 25000 + 9000 = 34700

    Examples:
      | opcode   |
      | CALL     |
      | CALLCODE |

  Scenario Outline: Gas cost for <opcode> when sending value to existing account
    Given an account with address 0xADD7E55 exists
    And the stack contains elements [0x1, 0xADD7E55, 0x100, 0x0, 0x0, 0x0, 0x0]
    When opcode <opcode> is executed
    Then 9700 gas is now used by the previous call context
    # G_call + G_callvalue = 700 + 9000 = 9700

    Examples:
      | opcode   |
      | CALL     |
      | CALLCODE |

  Scenario Outline: Gas cost for <opcode> with no value
    Given the stack contains elements <stack>
    When opcode <opcode> is executed
    Then 700 gas is now used by the previous call context
    # G_call = 700

    Examples:
      | opcode       | stack                                     |
      | CALL         | [0x1, 0xADD7E55, 0x0, 0x0, 0x0, 0x0, 0x0] |
      | CALLCODE     | [0x1, 0xADD7E55, 0x0, 0x0, 0x0, 0x0, 0x0] |
      | DELEGATECALL | [0x1, 0xADD7E55, 0x0, 0x0, 0x0, 0x0]      |
      | STATICCALL   | [0x1, 0xADD7E55, 0x0, 0x0, 0x0, 0x0]      |

  @Ignore # Yellow paper mentions this but not in ganache or parity
  Scenario: Gas cost for <opcode> which has capped gas
