Feature: EIP-2028

  # https://eips.ethereum.org/EIPS/eip-2028

  Background: Account has plenty funds
    Given account 0x5E4DE7 has nonce 0
    Given account 0x5E4DE7 has balance 100000000000000000


  Scenario Outline: Sending one byte of calldata in a transaction costs <byteFee> wei with EIP
    Given a transaction with contents:
      | from     | to       | value | gasPrice | gasLimit | data | nonce |
      | 0x5E4DE7 | 0xAAAAAA | 0     | 1        | 3000000  | 0xFF | 0     |
    And EIP <eip> is enabled
    When the transaction is executed
    Then transaction used <transactionFee> gas

    Examples: # 21000 is the base transaction cost
      | eip     | transactionFee | byteFee |
      | EIP2    | 21068          | 68      |
      | EIP2028 | 21016          | 16      |



