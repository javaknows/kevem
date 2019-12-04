Feature: Suicides Behaviour Of Transaction Processing

  Background: Account has plenty funds
    Given account 0x5E4DE7 has nonce 0
    Given account 0x5E4DE7 has balance 100000000000000000

  Scenario: Balances are correct after an account is suicided and sent to new account
    Given contract at address 0xEEEEEE has code [PUSH3 0xDD 0xDD 0xDD SUICIDE]
    And account 0xEEEEEE has balance 100
    And a transaction with contents:
      | from     | to       | value | gasPrice | gasLimit | data | nonce |
      | 0x5E4DE7 | 0xEEEEEE | 0     | 1        | 3000000  |      | 0     |
    When the transaction is executed
    Then transaction used 51003 gas
    And account 0xDDDDDD now has balance 100
    And account 0xEEEEEE now has balance 0
    And account 0x5E4DE7 now has balance 99999999999972997

    ## GAS cost
    # base cost: 21000
    # operations cost: PUSH3 = 3
    # SUICIDE cost: Gselfdestruct + new account chart = 5000 + 25000
    # 21000 + 3 + 5000 + 25000

    ## 0x5E4DE7 balance chart
    # initial balance - gas used + suicide refund = 100000000000000000 - 51003 + 24000 = 99999999999972997


  Scenario: Balances are correct after an account is suicided and sent to existing account
    Given contract at address 0xEEEEEE has code [PUSH3 0xDD 0xDD 0xDD SUICIDE]
    And account 0xEEEEEE has balance 100
    And account 0xDDDDDD has balance 0
    And a transaction with contents:
      | from     | to       | value | gasPrice | gasLimit | data | nonce |
      | 0x5E4DE7 | 0xEEEEEE | 0     | 1        | 3000000  |      | 0     |
    When the transaction is executed
    Then transaction used 26003 gas

    ## GAS cost
    # base cost: 21000
    # operations cost: PUSH3 = 3
    # SUICIDE cost: Gselfdestruct  = 5000
    # 21000 + 3 + 5000 = 26003