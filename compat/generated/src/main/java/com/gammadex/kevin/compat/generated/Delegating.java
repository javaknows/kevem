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
public class Delegating extends Contract {
    private static final String BINARY = "608060405234801561001057600080fd5b50610566806100206000396000f3fe608060405234801561001057600080fd5b50600436106100885760003560e01c8063b49b8c4e1161005b578063b49b8c4e146100ef578063c19166cd146100f9578063eee9c70814610103578063fdbacfd91461013157610088565b80630c166a611461008d5780631253f7aa146100d1578063624f011d146100db578063afc874d2146100e5575b600080fd5b6100cf600480360360208110156100a357600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff16906020019092919050505061015f565b005b6100d96101a2565b005b6100e3610259565b005b6100ed61030a565b005b6100f76103bb565b005b61010161046c565b005b61012f6004803603602081101561011957600080fd5b810190808035906020019092919050505061051d565b005b61015d6004803603602081101561014757600080fd5b8101908080359060200190929190505050610527565b005b806000806101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555050565b60008060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1690506000600154905060006002549050604051631253f7aa81526004602003810160208201915061010160018414156102075760208360048460008a8af190505b600284141561021e5760208360048460008a8af290505b6003841415610233576020836004848989f490505b6004841415610248576020836004848989fa90505b836108e582600080a3505050505050565b60008060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff169050600060015490506000600254905060405163624f011d81526004602003810161010160018414156102b85760008060048460008a8af190505b60028414156102cf5760008060048460008a8af290505b60038414156102e4576000806004848989f490505b60048414156102f9576000806004848989fa90505b836108e582600080a3505050505050565b60008060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff169050600060015490506000600254905060405163afc874d281526004602003810161010160018414156103695760008060048460008a8af190505b60028414156103805760008060048460008a8af290505b6003841415610395576000806004848989f490505b60048414156103aa576000806004848989fa90505b836108e582600080a3505050505050565b60008060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff169050600060015490506000600254905060405163b49b8c4e815260046020038101610101600184141561041a5760008060048460008a8af190505b60028414156104315760008060048460008a8af290505b6003841415610446576000806004848989f490505b600484141561045b576000806004848989fa90505b836108e582600080a3505050505050565b60008060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff169050600060015490506000600254905060405163c19166cd81526004602003810161010160018414156104cb5760008060048460008a8af190505b60028414156104e25760008060048460008a8af290505b60038414156104f7576000806004848989f490505b600484141561050c576000806004848989fa90505b836108e582600080a3505050505050565b8060018190555050565b806002819055505056fea265627a7a7230582000af28c005aa218ace8cd0484342f97ea9a11bcf2f9410f58990369db557e04764736f6c634300050a0032";

    public static final String FUNC_SETCHILDADDRESS = "setChildAddress";

    public static final String FUNC_DORETURN = "doReturn";

    public static final String FUNC_DOSTOP = "doStop";

    public static final String FUNC_DOREVERT = "doRevert";

    public static final String FUNC_DOINVALID = "doInvalid";

    public static final String FUNC_DOSELFDESTRUCT = "doSelfDestruct";

    public static final String FUNC_SETGASTOUSE = "setGasToUse";

    public static final String FUNC_SETCALLTYPE = "setCallType";

    @Deprecated
    protected Delegating(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected Delegating(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected Delegating(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected Delegating(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public RemoteCall<TransactionReceipt> setChildAddress(String _childAddress) {
        final Function function = new Function(
                FUNC_SETCHILDADDRESS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_childAddress)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> doReturn() {
        final Function function = new Function(
                FUNC_DORETURN, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> doStop() {
        final Function function = new Function(
                FUNC_DOSTOP, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> doRevert() {
        final Function function = new Function(
                FUNC_DOREVERT, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> doInvalid() {
        final Function function = new Function(
                FUNC_DOINVALID, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> doSelfDestruct() {
        final Function function = new Function(
                FUNC_DOSELFDESTRUCT, 
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

    public static Function doReturnFunction() {
        final Function function = new Function(
                FUNC_DORETURN, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return function;
    }

    public static Function doStopFunction() {
        final Function function = new Function(
                FUNC_DOSTOP, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return function;
    }

    public static Function doRevertFunction() {
        final Function function = new Function(
                FUNC_DOREVERT, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return function;
    }

    public static Function doInvalidFunction() {
        final Function function = new Function(
                FUNC_DOINVALID, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return function;
    }

    public static Function doSelfDestructFunction() {
        final Function function = new Function(
                FUNC_DOSELFDESTRUCT, 
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
    public static Delegating load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new Delegating(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static Delegating load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new Delegating(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static Delegating load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new Delegating(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static Delegating load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new Delegating(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<Delegating> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(Delegating.class, web3j, credentials, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<Delegating> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(Delegating.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    public static RemoteCall<Delegating> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(Delegating.class, web3j, transactionManager, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<Delegating> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(Delegating.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }
}
