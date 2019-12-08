Feature: Nonces When Transaction Processing

  Background: Account has plenty funds
    Given account 0x5E4DE7 has nonce 0
    Given account 0x5E4DE7 has balance 100000000000000000
    Given the current time is 2007-12-03T10:15:30.00Z


  Scenario: block is mined with a incremented number and current timestamp
    Given the previous block is:
      | number | difficulty | gasLimit | timestamp |
      | 1      | 1          | 100000   |           |
    And a transaction with contents:
      | from     | to | value | gasPrice | gasLimit | data | nonce |
      | 0x5E4DE7 |    | 69    | 1        | 3000000  |      | 0     |
    When the transaction is executed via stateful transaction processor
    Then the mined block now has:
      | number | difficulty | gasLimit | timestamp               | numTransactions |
      | 2      | 1          | 100000   | 2007-12-03T10:15:30.00Z | 1               |