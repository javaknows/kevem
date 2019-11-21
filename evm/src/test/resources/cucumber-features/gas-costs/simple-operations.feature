Feature: Check gas costs for simple opcode execution

  These opcodes have no logic or memory access costs

  Scenario Outline: check gas usage for operations which takes no arguments
    When opcode <opcode> is executed
    Then <gas> gas is now used

    Examples:
      | opcode         | gas |
      | ADDRESS        | 2   |
      | ORIGIN         | 2   |
      | CALLER         | 2   |
      | CALLVALUE      | 2   |
      | CALLDATASIZE   | 2   |
      | RETURNDATASIZE | 2   |
      | CODESIZE       | 2   |
      | GASPRICE       | 2   |
      | COINBASE       | 2   |
      | TIMESTAMP      | 2   |
      | NUMBER         | 2   |
      | DIFFICULTY     | 2   |
      | GASLIMIT       | 2   |
      | PC             | 2   |
      | MSIZE          | 2   |
      | GAS            | 2   |
      | JUMPDEST       | 1   |


  Scenario Outline: check gas usage for operations which takes one argument
    Given 0x1 is pushed onto the stack
    When opcode <opcode> is executed
    Then <gas> gas is now used

    Examples:
      | opcode      | gas |
      | ISZERO      | 3   |
      | NOT         | 3   |
      | BALANCE     | 400 |
      | EXTCODESIZE | 700 |
      | BLOCKHASH   | 20  |
      | POP         | 2   |
      | SLOAD       | 200 |


  Scenario Outline: check gas usage for operations which takes two arguments
    Given 0x1 is pushed onto the stack
    And 0x2 is pushed onto the stack
    When opcode <opcode> is executed
    Then <gas> gas is now used

    Examples:
      | opcode     | gas |
      | ADD        | 3   |
      | MUL        | 5   |
      | SUB        | 3   |
      | DIV        | 5   |
      | SDIV       | 5   |
      | MOD        | 5   |
      | SMOD       | 5   |
      | SIGNEXTEND | 5   |
      | LT         | 3   |
      | GT         | 3   |
      | SLT        | 3   |
      | SGT        | 3   |
      | EQ         | 3   |
      | AND        | 3   |
      | OR         | 3   |
      | XOR        | 3   |
      | BYTE       | 3   |

  Scenario Outline: check gas usage for operations which takes three arguments
    Given 0x1 is pushed onto the stack
    And 0x2 is pushed onto the stack
    And 0x3 is pushed onto the stack
    When opcode <opcode> is executed
    Then <gas> gas is now used

    Examples:
      | opcode       | gas |
      | ADDMOD       | 8   |
      | MULMOD       | 8   |
      | CALLDATALOAD | 3   |

