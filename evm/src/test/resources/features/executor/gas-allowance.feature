Feature: Gas allowance for CALL operations

  Background: Plenty gas available and plenty balance available
    Given there is 500000 gas remaining
    And the contract address is 0xC0476AC7
    And the account with address 0xC0476AC7 has balance 0x10000000000000000000

  Scenario Outline: Gas allowance includes stipend for non-zero value - <callType>
    Given an account with address 0xADD7E55 exists
    And contract code is [<contractCode>]
    And the current call value is <parentCallValue>
    # logs the GAS of the delegate
    And contract at address 0xBBBBBB has code [GAS, PUSH1, 0x0, PUSH1, 0x0, LOG1]
    When the context is executed to completion
    Then there is no last error
    And the execution context is now marked as complete
    And a log has been generated with no data
    And the log has topic data
      | <loggedValue> |

    # 0x8FA comes from call stipend of 0x8fc (2300) - 2 for GAS call cost
    Examples:
      | callType     | parentCallValue | contractCode                                                                                        | loggedValue |
      | CALL         | 0x0             | PUSH1, 0x0, PUSH1, 0x0, PUSH1, 0x0, PUSH1, 0x0, PUSH2, 0x100, PUSH3, 0xBBBBBB, PUSH1, 0x0, CALL     | 0x8FA       |
      | CALLCODE     | 0x0             | PUSH1, 0x0, PUSH1, 0x0, PUSH1, 0x0, PUSH1, 0x0, PUSH2, 0x100, PUSH3, 0xBBBBBB, PUSH1, 0x0, CALLCODE | 0x8FA       |
      | DELEGATECALL | 0x100           | PUSH1, 0x0, PUSH1, 0x0, PUSH1, 0x0, PUSH1, 0x0, PUSH3, 0xBBBBBB, PUSH1, 0x0, DELEGATECALL           | 0x8FA       |
