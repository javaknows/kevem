pragma solidity >=0.4.24;

contract LogAddress {

    function run() public {
        assembly {
            log1(0, 0, address())
        }
    }
}