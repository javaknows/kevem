Feature: EIP 2200: Structured Definitions for Net Gas Metering

  # https://eips.ethereum.org/EIPS/eip-2200

  Background: Plenty gas available and plenty balance available
    Given there is 500000 gas remaining
    And the contract address is 0xC0476AC7
    And the account with address 0xC0476AC7 has balance 0x1000
    And transaction origin is 0x091614


  Scenario Outline: fail SSTORE with out of gas if gasleft is less than or equal to call stipend
    Given EIP EIP2200 is enabled
    And there is <gas> gas remaining
    And the stack contains elements [0x0, 0x0]
    When opcode SSTORE is executed
    Then the last error is now OUT_OF_GAS with message "Out of gas"

    Examples:
      | gas  |
      | 2300 |
      | 2299 |


  Scenario Outline: do not fail SSTORE with out of gas if gasleft is greater than call stipend
    Given EIP EIP2200 is enabled
    And there is <gas> gas remaining
    And the stack contains elements [0x0, 0x0]
    When opcode SSTORE is executed
    Then there is no last error

    Examples:
      | gas   |
      | 2301  |
      | 20000 |


  Scenario Outline: Case original <original>, 1st <1st>, 2nd <2nd>, 3rd <3rd> from https://eips.ethereum.org/EIPS/eip-2200
    Given EIP EIP2200 is enabled
    And contract code is <code>
    And <original> is in storage at location 0x0 of current contract
    When the context is executed to completion
    Then there is no last error
    And the transaction has now used <gas> gas
    And account 0x091614 has a refund of <refund>

    Examples:
      | code                             | gas   | refund | original | 1st | 2nd | 3rd |
      | 0x60006000556000600055           | 1612  | 0      | 0x0      | 0   | 0   |     |
      | 0x60006000556001600055           | 20812 | 0      | 0x0      | 0   | 1   |     |
      | 0x60016000556000600055           | 20812 | 19200  | 0x0      | 1   | 0   |     |
      | 0x60016000556002600055           | 20812 | 0      | 0x0      | 1   | 2   |     |
      | 0x60016000556001600055           | 20812 | 0      | 0x0      | 1   | 1   |     |
      | 0x60006000556000600055           | 5812  | 15000  | 0x1      | 0   | 0   |     |
      | 0x60006000556001600055           | 5812  | 4200   | 0x1      | 0   | 1   |     |
      | 0x60006000556002600055           | 5812  | 0      | 0x1      | 0   | 2   |     |
      | 0x60026000556000600055           | 5812  | 15000  | 0x1      | 2   | 0   |     |
      | 0x60026000556003600055           | 5812  | 0      | 0x1      | 2   | 3   |     |
      | 0x60026000556001600055           | 5812  | 4200   | 0x1      | 2   | 1   |     |
      | 0x60026000556002600055           | 5812  | 0      | 0x1      | 2   | 2   |     |
      | 0x60016000556000600055           | 5812  | 15000  | 0x1      | 1   | 0   |     |
      | 0x60016000556002600055           | 5812  | 0      | 0x1      | 1   | 2   |     |
      | 0x60016000556001600055           | 1612  | 0      | 0x1      | 1   | 1   |     |
      | 0x600160005560006000556001600055 | 40818 | 19200  | 0x0      | 1   | 0   | 1   |
      | 0x600060005560016000556000600055 | 10818 | 19200  | 0x1      | 0   | 1   | 0   |