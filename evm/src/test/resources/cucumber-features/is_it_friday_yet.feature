Feature: Is it Friday yet?
  Everybody wants to know when it's Friday

  Scenario: Sunday isn't Friday
    Given an execution context
    And I ask whether it's Friday yet
    Then I should be told `Nope`
