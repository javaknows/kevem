Feature: Transaction Processing

  Check that a transaction can be processed

  Background: Account has plenty funds
    Given account 0x5E4DE7 has nonce 0
    Given account 0x5E4DE7 has balance 100000000000000000


  Scenario: Simple balance transfer
    Given a transaction with contents:
      | from     | to       | value | gasPrice | gasLimit | data | nonce |
      | 0x5E4DE7 | 0xAAAAAA | 100   | 1        | 3000000  |      | 0     |
    When the transaction is executed
    Then the result status is now COMPLETE
    And account 0xAAAAAA now has balance 100
    And transaction used 21000 gas
    And account 0x5E4DE7 now has balance 99999999999978900
    # 99999999999978900 = 100000000000000000 - 21000 - 100


  Scenario: Gas price gets taken into account when charging gas
    Given a transaction with contents:
      | from     | to       | value | gasPrice | gasLimit | data | nonce |
      | 0x5E4DE7 | 0xAAAAAA | 0     | 2        | 3000000  |      | 0     |
    When the transaction is executed
    Then transaction used 21000 gas
    And account 0x5E4DE7 now has balance 99999999999958000
    # 99999999999957900 = 100000000000000000 - (2 * 21000)
