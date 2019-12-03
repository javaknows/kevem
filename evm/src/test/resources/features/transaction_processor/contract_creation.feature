Feature: Contract Creation by Transaction

  Check that a contract can be created

  Background: Account has plenty funds
    Given account 0x5E4DE7 has nonce 0
    Given account 0x5E4DE7 has balance 100000000000000000


  Scenario: Gas cost for a small contract
    Given a transaction with contents:
      | from     | to | value | gasPrice | gasLimit | data                                                                               | nonce |
      | 0x5E4DE7 |    | 0     | 1        | 3000000  | [PUSH1 0x01 DUP1 PUSH1 0x0C PUSH1 0x00 CODECOPY PUSH1 0x00 RETURN INVALID ADDRESS] | 0     |
    When the transaction is executed
    Then a contract with address 0x66ee2961f3200b1a4b6f585c9c64e95920799b29 was created
    And the code at address 0x66ee2961f3200b1a4b6f585c9c64e95920799b29 is now 0x30
    Then transaction used 53980 gas

    # base cost: 21000
    # data argument cost: 11 non-zero, 2 zero = (11 * 68) + (2 * 4) = 756
    # operations cost: PUSH1+DUP1+PUSH1+PUSH1+PUSH1 = (5 * 3) = 15
    # CODECOPY cost: G verylow + G copy × dμ_s [2] ÷ 32 = (3 + 3 * 1) = 6
    # use of extra word of memory: 3
    # RETURN cost: 0
    # contract creation base fee: 32000
    # contract creation data size fee: = 200 * 1 = 200
    #  200 + 32000 + 3 + 6 + 15 + 756 + 21000 = 53980