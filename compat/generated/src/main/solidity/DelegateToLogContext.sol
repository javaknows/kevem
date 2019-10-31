pragma solidity >=0.4.24;

import './LogContext.sol';

contract DelegateToLogContext {

    address childAddress;
    uint gasToUse;
    uint callType;

    function setChildAddress(address _childAddress) public {
        childAddress = _childAddress;
    }

    function setGasToUse(uint _gasToUse) public {
        gasToUse = _gasToUse;
    }

    function setCallType(uint _callType) public {
        LogContext(childAddress).setCallType(_callType);
        callType = _callType;
    }

    function callCreateContextLogs() public {
        address a = childAddress;
        uint g = gasToUse;
        uint c = callType;

        assembly {
            mstore(0x200, 0xDEADBEEF)
            sstore(0, 0xCAFEBABE)

            let memPointer := mload(0x40)
            mstore(memPointer, 0x8f3242ca)
            let pos := add(memPointer, sub(0x20, 0x4))

            let result := 0x101
            if eq(c, 1) {
                result := call(g, a, 0, pos, 0x4, 0, 0)
            }
            if eq(c, 2) {
                result := callcode(g, a, 0, pos, 0x4, 0, 0)
            }
            if eq(c, 3) {
                result := delegatecall(g, a, pos, 0x4, 0, 0)
            }
            if eq(c, 4) {
                result := staticcall(g, a, pos, 0x4, 0, 0)
            }

            log3(0, 0, c, 0x8E5, result)
        }
    }
}