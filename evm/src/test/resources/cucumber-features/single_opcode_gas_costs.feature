Feature: Debugging

  Check gas costs for single opcode execution

  Scenario: check gas usage for operations which takes no arguments
    When an opcode is executed it consumes gas:
      | Opcode         | Gas |
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


  Scenario: check gas usage for operations which takes one argument
    Given 0x1 is pushed onto the stack
    When an opcode is executed it consumes gas:
      | ISZERO      | 3   |
      | NOT         | 3   |
      | BALANCE     | 400 |
      | EXTCODESIZE | 700 |
      | BLOCKHASH   | 20  |
      | POP         | 2   |
      | SLOAD       | 200 |


  Scenario: check gas usage for operations which takes two arguments
    Given 0x1 is pushed onto the stack
    And 0x2 is pushed onto the stack
    When an opcode is executed it consumes gas:
      | ADD        | 3 |
      | MUL        | 5 |
      | SUB        | 3 |
      | DIV        | 5 |
      | SDIV       | 5 |
      | MOD        | 5 |
      | SMOD       | 5 |
      | SIGNEXTEND | 5 |
      | LT         | 3 |
      | GT         | 3 |
      | SLT        | 3 |
      | SGT        | 3 |
      | EQ         | 3 |
      | AND        | 3 |
      | OR         | 3 |
      | XOR        | 3 |
      | BYTE       | 3 |

  Scenario: check gas usage for operations which takes two arguments
    Given 0x1 is pushed onto the stack
    And 0x2 is pushed onto the stack
    And 0x3 is pushed onto the stack
    When an opcode is executed it consumes gas:
      | ADDMOD | 8 |
      | MULMOD | 8 |

