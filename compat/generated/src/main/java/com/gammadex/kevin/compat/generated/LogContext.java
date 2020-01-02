package org.kevm.compat.generated;

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
public class LogContext extends Contract {
    private static final String BINARY = "608060405234801561001057600080fd5b5060e48061001f6000396000f3fe6080604052348015600f57600080fd5b506004361060325760003560e01c80638f3242ca146037578063fdbacfd914603f575b600080fd5b603d606a565b005b606860048036036020811015605357600080fd5b810190808035906020019092919050505060a5565b005b600080549050604051808182526020820191506001548252602082019150308252602082019150338252602082019150328252610200810181f35b806000819055505056fea265627a7a723058208397a4c09823bbd2c7818bc2715ea521217980b8a22943d754e687c1834ec1b364736f6c634300050a0032";

    public static final String FUNC_CREATECONTEXTLOGS = "createContextLogs";

    public static final String FUNC_SETCALLTYPE = "setCallType";

    @Deprecated
    protected LogContext(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected LogContext(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected LogContext(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected LogContext(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public RemoteCall<TransactionReceipt> createContextLogs() {
        final Function function = new Function(
                FUNC_CREATECONTEXTLOGS, 
                Arrays.<Type>asList(), 
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

    public static Function createContextLogsFunction() {
        final Function function = new Function(
                FUNC_CREATECONTEXTLOGS, 
                Arrays.<Type>asList(), 
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
    public static LogContext load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new LogContext(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static LogContext load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new LogContext(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static LogContext load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new LogContext(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static LogContext load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new LogContext(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<LogContext> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(LogContext.class, web3j, credentials, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<LogContext> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(LogContext.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    public static RemoteCall<LogContext> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(LogContext.class, web3j, transactionManager, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<LogContext> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(LogContext.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }
}
