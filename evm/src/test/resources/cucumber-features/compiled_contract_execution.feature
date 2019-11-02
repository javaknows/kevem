Feature: Compiled Small Contract Execution

  Check that sample contracts compiled by Solidity can be executed

  Scenario: Check that LogAddress.sol can be executed
    Given contract code is 0x6080604052348015600f57600080fd5b506004361060285760003560e01c8063c040622614602d575b600080fd5b60336035565b005b30600080a156fea265627a7a723058203c79d129fbb20fba2e6d9df99e583e6f5ae5eff7fbc9b1ab4bb602eebe4df83164736f6c634300050a0032
    And the contract address is 0xADD8E55
    And call data is 0xc0406226
    When the context is executed to completion
    Then the execution context is now marked as complete
    And there is no last error
    And a log has been generated with no data
    And the log has topic data
      | 0xADD8E55 |