pragma solidity >=0.4.24;

contract Delegating {

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
        callType = _callType;
    }

    function doStop() public {
        address a = childAddress;
        uint g = gasToUse;
        uint c = callType;

        assembly {
            let memPointer := mload(0x40)
            mstore(memPointer, 0x624f011d)
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

            log3(0, 0, result, 0x8E5, c)
        }
    }

    function doReturn() public {
        address a = childAddress;
        uint g = gasToUse;
        uint c = callType;

        assembly {
            let memPointer := mload(0x40)
            mstore(memPointer, 0x1253f7aa)
            let pos := add(memPointer, sub(0x20, 0x4))
            memPointer := add(memPointer, 0x20)


            let result := 0x101

            if eq(c, 1) {
                result := call(g, a, 0, pos, 0x4, memPointer, 0x20)
            }
            if eq(c, 2) {
                result := callcode(g, a, 0, pos, 0x4, memPointer, 0x20)
            }
            if eq(c, 3) {
                result := delegatecall(g, a, pos, 0x4, memPointer, 0x20)
            }
            if eq(c, 4) {
                result := staticcall(g, a, pos, 0x4, memPointer, 0x20)
            }

            //log3(memPointer, 0x20, result, 0x8E5, c)
            log3(0, 0, result, 0x8E5, c)
        }
    }

    function doRevert() public {
        address a = childAddress;
        uint g = gasToUse;
        uint c = callType;

        assembly {
            let memPointer := mload(0x40)
            mstore(memPointer, 0xafc874d2)
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

            //log3(memPointer, 0x20, result, 0x8E5, c)
            log3(0, 0, result, 0x8E5, c)
        }
    }

    function doInvalid() public {
        address a = childAddress;
        uint g = gasToUse;
        uint c = callType;

        assembly {
            let memPointer := mload(0x40)
            mstore(memPointer, 0xb49b8c4e)
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

            log3(0, 0, result, 0x8E5, c)
        }
    }

    function doSelfDestruct() public {
        address a = childAddress;
        uint g = gasToUse;
        uint c = callType;

        assembly {
            let memPointer := mload(0x40)
            mstore(memPointer, 0xc19166cd)
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

            log3(0, 0, result, 0x8E5, c)
        }
    }
}