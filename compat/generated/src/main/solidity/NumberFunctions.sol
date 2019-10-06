pragma solidity >=0.4.24;

contract NumberFunctions  {

    function callAdd(bytes32 a, bytes32 b) public {
        assembly {
            let arg1 := calldataload(4)
            let arg2 := calldataload(0x24)

            let ret := add(arg1, arg2)

            let memPointer := mload(0x40)
            mstore(memPointer, ret)
            log0(memPointer, 0x20)
        }
    }

    function callSub(bytes32 a, bytes32 b) public {
        assembly {
            let arg1 := calldataload(4)
            let arg2 := calldataload(0x24)

            let ret := sub(arg1, arg2)

            let memPointer := mload(0x40)
            mstore(memPointer, ret)
            log0(memPointer, 0x20)
        }
    }

    function callMul(bytes32 a, bytes32 b) public {
        assembly {
            let arg1 := calldataload(4)
            let arg2 := calldataload(0x24)

            let ret := mul(arg1, arg2)

            let memPointer := mload(0x40)
            mstore(memPointer, ret)
            log0(memPointer, 0x20)
        }
    }

    function callDiv(bytes32 a, bytes32 b) public {
        assembly {
            let arg1 := calldataload(4)
            let arg2 := calldataload(0x24)

            let ret := div(arg1, arg2)

            let memPointer := mload(0x40)
            mstore(memPointer, ret)
            log0(memPointer, 0x20)
        }
    }

    function callSdiv(bytes32 a, bytes32 b) public {
        assembly {
            let arg1 := calldataload(4)
            let arg2 := calldataload(0x24)

            let ret := sdiv(arg1, arg2)

            let memPointer := mload(0x40)
            mstore(memPointer, ret)
            log0(memPointer, 0x20)
        }
    }

    function callMod(bytes32 a, bytes32 b) public {
        assembly {
            let arg1 := calldataload(4)
            let arg2 := calldataload(0x24)

            let ret := mod(arg1, arg2)

            let memPointer := mload(0x40)
            mstore(memPointer, ret)
            log0(memPointer, 0x20)
        }
    }

    function callSmod(bytes32 a, bytes32 b) public {
        assembly {
            let arg1 := calldataload(4)
            let arg2 := calldataload(0x24)

            let ret := smod(arg1, arg2)

            let memPointer := mload(0x40)
            mstore(memPointer, ret)
            log0(memPointer, 0x20)
        }
    }

    function callAddmod(bytes32 a, bytes32 b, bytes32 c) public {
        assembly {
            let arg1 := calldataload(4)
            let arg2 := calldataload(0x24)
            let arg3 := calldataload(0x44)

            let ret := addmod(arg1, arg2, arg3)

            let memPointer := mload(0x40)
            mstore(memPointer, ret)
            log0(memPointer, 0x20)
        }
    }

    function callMulmod(bytes32 a, bytes32 b, bytes32 c) public {
        assembly {
            let arg1 := calldataload(4)
            let arg2 := calldataload(0x24)
            let arg3 := calldataload(0x44)

            let ret := mulmod(arg1, arg2, arg3)

            let memPointer := mload(0x40)
            mstore(memPointer, ret)
            log0(memPointer, 0x20)
        }
    }

    function callExp(bytes32 a, bytes32 b) public {
        assembly {
            let arg1 := calldataload(4)
            let arg2 := calldataload(0x24)

            let ret := exp(arg1, arg2)

            let memPointer := mload(0x40)
            mstore(memPointer, ret)
            log0(memPointer, 0x20)
        }
    }

    function callSignextend(bytes32 a, bytes32 b) public {
        assembly {
            let arg1 := calldataload(4)
            let arg2 := calldataload(0x24)

            let ret := signextend(arg1, arg2)

            let memPointer := mload(0x40)
            mstore(memPointer, ret)
            log0(memPointer, 0x20)
        }
    }

    function callLt(bytes32 a, bytes32 b) public {
        assembly {
            let arg1 := calldataload(4)
            let arg2 := calldataload(0x24)

            let ret := lt(arg1, arg2)

            let memPointer := mload(0x40)
            mstore(memPointer, ret)
            log0(memPointer, 0x20)
        }
    }

    function callGt(bytes32 a, bytes32 b) public {
        assembly {
            let arg1 := calldataload(4)
            let arg2 := calldataload(0x24)

            let ret := gt(arg1, arg2)

            let memPointer := mload(0x40)
            mstore(memPointer, ret)
            log0(memPointer, 0x20)
        }
    }

    function callSlt(bytes32 a, bytes32 b) public {
        assembly {
            let arg1 := calldataload(4)
            let arg2 := calldataload(0x24)

            let ret := slt(arg1, arg2)

            let memPointer := mload(0x40)
            mstore(memPointer, ret)
            log0(memPointer, 0x20)
        }
    }

    function callSgt(bytes32 a, bytes32 b) public {
        assembly {
            let arg1 := calldataload(4)
            let arg2 := calldataload(0x24)

            let ret := sgt(arg1, arg2)

            let memPointer := mload(0x40)
            mstore(memPointer, ret)
            log0(memPointer, 0x20)
        }
    }

    function callEq(bytes32 a, bytes32 b) public {
        assembly {
            let arg1 := calldataload(4)
            let arg2 := calldataload(0x24)

            let ret := eq(arg1, arg2)

            let memPointer := mload(0x40)
            mstore(memPointer, ret)
            log0(memPointer, 0x20)
        }
    }

    function callIszero(bytes32 a) public {
        assembly {
            let arg1 := calldataload(4)

            let ret := iszero(arg1)

            let memPointer := mload(0x40)
            mstore(memPointer, ret)
            log0(memPointer, 0x20)
        }
    }

    function callAnd(bytes32 a, bytes32 b) public {
        assembly {
            let arg1 := calldataload(4)
            let arg2 := calldataload(0x24)

            let ret := and(arg1, arg2)

            let memPointer := mload(0x40)
            mstore(memPointer, ret)
            log0(memPointer, 0x20)
        }
    }

    function callOr(bytes32 a, bytes32 b) public {
        assembly {
            let arg1 := calldataload(4)
            let arg2 := calldataload(0x24)

            let ret := or(arg1, arg2)

            let memPointer := mload(0x40)
            mstore(memPointer, ret)
            log0(memPointer, 0x20)
        }
    }

    function callXor(bytes32 a, bytes32 b) public {
        assembly {
            let arg1 := calldataload(4)
            let arg2 := calldataload(0x24)

            let ret := xor(arg1, arg2)

            let memPointer := mload(0x40)
            mstore(memPointer, ret)
            log0(memPointer, 0x20)
        }
    }

    function callNot(bytes32 a) public {
        assembly {
            let arg1 := calldataload(4)

            let ret := not(arg1)

            let memPointer := mload(0x40)
            mstore(memPointer, ret)
            log0(memPointer, 0x20)
        }
    }

    function callByte(bytes32 a, bytes32 b) public {
        assembly {
            let arg1 := calldataload(4)
            let arg2 := calldataload(0x24)

            let ret := byte(arg1, arg2)

            let memPointer := mload(0x40)
            mstore(memPointer, ret)
            log0(memPointer, 0x20)
        }
    }

    function callShl(bytes32 a, bytes32 b) public {
        assembly {
            let arg1 := calldataload(4)
            let arg2 := calldataload(0x24)

            let ret := shl(arg1, arg2)

            let memPointer := mload(0x40)
            mstore(memPointer, ret)
            log0(memPointer, 0x20)
        }
    }

    function callShr(bytes32 a, bytes32 b) public {
        assembly {
            let arg1 := calldataload(4)
            let arg2 := calldataload(0x24)

            let ret := shr(arg1, arg2)

            let memPointer := mload(0x40)
            mstore(memPointer, ret)
            log0(memPointer, 0x20)
        }
    }

    function callSar(bytes32 a, bytes32 b) public {
        assembly {
            let arg1 := calldataload(4)
            let arg2 := calldataload(0x24)

            let ret := sar(arg1, arg2)

            let memPointer := mload(0x40)
            mstore(memPointer, ret)
            log0(memPointer, 0x20)
        }
    }

    function callSha3(bytes32 a, bytes32 b) public {
        assembly {
            let arg1 := calldataload(4)
            let arg2 := calldataload(0x24)

            let ret := keccak256(arg1, arg2)

            let memPointer := mload(0x40)
            mstore(memPointer, ret)
            log0(memPointer, 0x20)
        }
    }
}