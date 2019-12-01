Feature: Transaction Processing

  Check that a transaction can be processed

  Background: Account has plenty funds
    Given account 0x5E4DE7 has balance 100000000000000000


  Scenario: Basic balance transfer
    Given a transaction with contents:
      | from     | to       | value | gasPrice | gasLimit | data | nonce |
      | 0x5E4DE7 | 0xAAAAAA | 100   | 1        | 3000000  |      | 0     |
    When the transaction is executed
    Then the result status is now COMPLETE
     And account 0xAAAAAA now has balance 100