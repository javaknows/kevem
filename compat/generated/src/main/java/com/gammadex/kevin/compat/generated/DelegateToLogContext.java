package com.gammadex.kevin.compat.generated;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 4.3.2-SNAPSHOT.
 */
public class DelegateToLogContext extends Contract {
    private static final String BINARY = "608060405234801561001057600080fd5b50610323806100206000396000f3fe608060405234801561001057600080fd5b506004361061004c5760003560e01c80630c166a611461005157806357c7d23b14610095578063eee9c7081461009f578063fdbacfd9146100cd575b600080fd5b6100936004803603602081101561006757600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff1690602001909291905050506100fb565b005b61009d61013e565b005b6100cb600480360360208110156100b557600080fd5b810190808035906020019092919050505061024e565b005b6100f9600480360360208110156100e357600080fd5b8101908080359060200190929190505050610258565b005b806000806101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555050565b60008060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff16905060006001549050600060025490506102006040510160405263cafebabe600155604051638f3242ca81526004602003810161010160018414156101af5760008060048460008a8af190505b60028414156101c65760008060048460008a8af290505b60038414156101db576000806004848989f490505b60048414156101f0576000806004848989fa90505b60405160206000823e8051600186600080a3602080823e8051600286600080a360206040823e8051600386600080a360206060823e8051600486600080a360206080823e8051600586600080a381600986600080a350505050505050565b8060018190555050565b6000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1663fdbacfd9826040518263ffffffff1660e01b815260040180828152602001915050600060405180830381600087803b1580156102cc57600080fd5b505af11580156102e0573d6000803e3d6000fd5b50505050806002819055505056fea265627a7a723058207aefd28c5c0704ef617bab54a3ba5466e4e6210f668d1198b821369c80230f7964736f6c634300050a0032";

    public static final String FUNC_SETCHILDADDRESS = "setChildAddress";

    public static final String FUNC_CALLCREATECONTEXTLOGS = "callCreateContextLogs";

    public static final String FUNC_SETGASTOUSE = "setGasToUse";

    public static final String FUNC_SETCALLTYPE = "setCallType";

    @Deprecated
    protected DelegateToLogContext(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected DelegateToLogContext(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected DelegateToLogContext(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected DelegateToLogContext(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public RemoteCall<TransactionReceipt> setChildAddress(String _childAddress) {
        final Function function = new Function(
                FUNC_SETCHILDADDRESS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_childAddress)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> callCreateContextLogs() {
        final Function function = new Function(
                FUNC_CALLCREATECONTEXTLOGS, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> setGasToUse(BigInteger _gasToUse) {
        final Function function = new Function(
                FUNC_SETGASTOUSE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_gasToUse)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> setCallType(BigInteger _callType) {
        final Function function = new Function(
                FUNC_SETCALLTYPE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_callType)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public static Function setChildAddressFunction(String _childAddress) {
        final Function function = new Function(
                FUNC_SETCHILDADDRESS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_childAddress)), 
                Collections.<TypeReference<?>>emptyList());
        return function;
    }

    public static Function callCreateContextLogsFunction() {
        final Function function = new Function(
                FUNC_CALLCREATECONTEXTLOGS, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return function;
    }

    public static Function setGasToUseFunction(BigInteger _gasToUse) {
        final Function function = new Function(
                FUNC_SETGASTOUSE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_gasToUse)), 
                Collections.<TypeReference<?>>emptyList());
        return function;
    }

    public static Function setCallTypeFunction(BigInteger _callType) {
        final Function function = new Function(
                FUNC_SETCALLTYPE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_callType)), 
                Collections.<TypeReference<?>>emptyList());
        return function;
    }

    @Deprecated
    public static DelegateToLogContext load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new DelegateToLogContext(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static DelegateToLogContext load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new DelegateToLogContext(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static DelegateToLogContext load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new DelegateToLogContext(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static DelegateToLogContext load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new DelegateToLogContext(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<DelegateToLogContext> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(DelegateToLogContext.class, web3j, credentials, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<DelegateToLogContext> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(DelegateToLogContext.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    public static RemoteCall<DelegateToLogContext> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(DelegateToLogContext.class, web3j, transactionManager, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<DelegateToLogContext> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(DelegateToLogContext.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }
}
