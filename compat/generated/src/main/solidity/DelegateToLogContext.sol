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
            mstore(0x40, add(mload(0x40), 0x200)) // bump free memory pointer to check in delegate later
            sstore(1, 0xCAFEBABE) // store an element in storage to check in delegate later

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

            let memLoc := mload(0x40)

            returndatacopy(memLoc, 0x00, 0x20)
            log3(0, 0, c, 1, mload(memLoc))   // mempointer

            returndatacopy(memLoc, 0x20, 0x20)
            log3(0, 0, c, 2, mload(memLoc))   // storage

            returndatacopy(memLoc, 0x40, 0x20)
            log3(0, 0, c, 3, mload(memLoc))   // address

            returndatacopy(memLoc, 0x60, 0x20)
            log3(0, 0, c, 4, mload(memLoc))   // caller

            returndatacopy(memLoc, 0x80, 0x20)
            log3(0, 0, c, 5, mload(memLoc))  // origin

            log3(0, 0, c, 9, result) // result
        }
    }
}