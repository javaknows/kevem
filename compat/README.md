# Compatability Checks

Use for checking and compatibility of Kevem with Ganache and Parity

## Ganache

```bash
ganache-cli --gasLimit=942477600000000 --allowUnlimitedContractSize -m "stay jeans limb improve struggle return predict flower assume giraffe mother spring"
```

## Parity Dev Node

Use Parity dev chain option `--chain dev`

https://wiki.parity.io/Private-development-chain.html

* Creates account with loads of ETH: 0x00a329c0648769a73afac7f9381e08fb43dbea72
* Private key: 0x4d5db4107d237df6a3d58ee5f70ae63d73d7658d4026f2eefd2f204c81682cb7

### Kevem test account 

*One-off setup*

```bash
parity account import --chain dev keystore-dev-0x4e7d932c0f12cfe14295b86824b37bb1062bc29e-password.json
```

* address: `0x4e7d932c0f12cfe14295b86824b37bb1062bc29e`
* keystore file: keystore-dev-0x4e7d932c0f12cfe14295b86824b37bb1062bc29e-password.json
* keystore password: keystore-dev-0x4e7d932c0f12cfe14295b86824b37bb1062bc29e-password.txt

### Startup

```bash
parity --config dev --jsonrpc-apis all --unlock 0x4e7d932c0f12cfe14295b86824b37bb1062bc29e --password keystore-dev-0x4e7d932c0f12cfe14295b86824b37bb1062bc29e-password.txt
```

Give kevem account 100 ETH

```bash
# unlock parity dev account
curl --data '{"method":"personal_unlockAccount","params":["0x00A329c0648769a73afac7f9381e08fb43dbea72","",null],"id":1,"jsonrpc":"2.0"}' -H "Content-Type: application/json" -X POST localhost:8545

# transfer 100 ETH to main kevem dev account
curl --data '{"method":"eth_sendTransaction","params":[{"from":"0x00A329c0648769a73afac7f9381e08fb43dbea72","to":"0x4E7d932c0f12cfe14295b86824b37bb1062bc29e","gas":"0x5208","gasPrice":"0x9184e72a000","value":"0x56bc75e2d63100000","data":"0x"}],"id":1,"jsonrpc":"2.0"}' -H "Content-Type: application/json" -X POST localhost:8545
```
