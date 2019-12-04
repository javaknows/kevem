Feature: Nonces When Transaction Processing

  Background: Account has plenty funds
    Given account 0x5E4DE7 has nonce 0
    Given account 0x5E4DE7 has balance 100000000000000000


  Scenario: Sender nonce is incremented during valid transaction
    Given a transaction with contents:
      | from     | to        | value | gasPrice | gasLimit | data | nonce |
      | 0x5E4DE7 | 0xADD7E55 | 0     | 1        | 3000000  |      | 0     |
    When the transaction is executed
    Then account 0x5E4DE7 now has nonce 1


  Scenario: Sender nonce is not incremented for invalid transaction
    Given a transaction with contents:
      | from     | to        | value | gasPrice           | gasLimit | data | nonce |
      | 0x5E4DE7 | 0xADD7E55 | 0     | 100000000000000001 | 3000000  |      | 0     |
    When the transaction is executed
    Then account 0x5E4DE7 now has nonce 0

