Feature: Single Opcode Execution

  Check that each opcode executes correctly

  Scenario: Two numbers can be added using ADD then logged with LOG1
    Given contract code is [PUSH1, 0x0, PUSH1, 0x0, PUSH1, 0x01, PUSH1, 0x02, ADD, LOG1]
    When the context is executed to completion
    Then the execution context is now marked as complete
    And there is no last error
    And a log has been generated with no data
    And the log has topic data
      | 0x03 |