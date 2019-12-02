Feature: Initial Validation of Transaction

  Background: Account has plenty funds
    Given account 0x5E4DE7 has nonce 0
    Given account 0x5E4DE7 has balance 100000000000000000


  Scenario: Transaction rejected if account nonce doesn't match world state
    Given account 0x5E4DE7 has nonce 0
    And a transaction with contents:
      | from     | to       | value | gasPrice | gasLimit | data | nonce |
      | 0x5E4DE7 | 0xAAAAAA | 100   | 1        | 3000000  |      | 1     |
    When the transaction is executed
    Then the result status is now REJECTED


  Scenario: Transaction rejected if gas limit below intrinsic gas cost
    Given a transaction with contents:
      | from     | to       | value | gasPrice | gasLimit | data | nonce |
      | 0x5E4DE7 | 0xAAAAAA | 100   | 1        | 20999    |      | 0     |
    When the transaction is executed
    Then the result status is now REJECTED


  Scenario: Transaction rejected if not enough funds for value
    Given account 0x5E4DE7 has balance 10000000
    And a transaction with contents:
      | from     | to       | value    | gasPrice | gasLimit | data | nonce |
      | 0x5E4DE7 | 0xAAAAAA | 99999999 | 1        | 21000    |      | 0     |
    When the transaction is executed
    Then the result status is now REJECTED