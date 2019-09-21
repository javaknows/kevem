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
public class Kevin extends Contract {
    private static final String BINARY = "608060405234801561001057600080fd5b5061089e806100206000396000f3fe608060405234801561001057600080fd5b50600436106101425760003560e01c806392e50369116100b8578063d1de592a1161007c578063d1de592a146104c7578063d28dcea7146104ff578063d8c8807b14610541578063e40ec7ce14610579578063ed365106146105b1578063edf3afa6146105df57610142565b806392e50369146103af57806396ce1ec7146103e7578063b61d9d311461041f578063ba6f4f6c14610457578063d102b4d31461048f57610142565b806341aa00801161010a57806341aa0080146102555780635a67c5431461028d57806360bc2253146102c557806372321157146103075780638bf8f6e81461033f5780638dc298071461037757610142565b806321f6c3001461014757806325df0ac21461017557806330297400146101ad5780633447c030146101e55780633c1339751461021d575b600080fd5b6101736004803603602081101561015d57600080fd5b8101908080359060200190929190505050610617565b005b6101ab6004803603604081101561018b57600080fd5b81019080803590602001909291908035906020019092919050505061062c565b005b6101e3600480360360408110156101c357600080fd5b810190808035906020019092919080359060200190929190505050610647565b005b61021b600480360360408110156101fb57600080fd5b810190808035906020019092919080359060200190929190505050610662565b005b6102536004803603604081101561023357600080fd5b81019080803590602001909291908035906020019092919050505061067d565b005b61028b6004803603604081101561026b57600080fd5b810190808035906020019092919080359060200190929190505050610698565b005b6102c3600480360360408110156102a357600080fd5b8101908080359060200190929190803590602001909291905050506106b3565b005b610305600480360360608110156102db57600080fd5b810190808035906020019092919080359060200190929190803590602001909291905050506106ce565b005b61033d6004803603604081101561031d57600080fd5b8101908080359060200190929190803590602001909291905050506106ef565b005b6103756004803603604081101561035557600080fd5b81019080803590602001909291908035906020019092919050505061070a565b005b6103ad6004803603604081101561038d57600080fd5b810190808035906020019092919080359060200190929190505050610725565b005b6103e5600480360360408110156103c557600080fd5b810190808035906020019092919080359060200190929190505050610740565b005b61041d600480360360408110156103fd57600080fd5b81019080803590602001909291908035906020019092919050505061075b565b005b6104556004803603604081101561043557600080fd5b810190808035906020019092919080359060200190929190505050610776565b005b61048d6004803603604081101561046d57600080fd5b810190808035906020019092919080359060200190929190505050610791565b005b6104c5600480360360408110156104a557600080fd5b8101908080359060200190929190803590602001909291905050506107ac565b005b6104fd600480360360408110156104dd57600080fd5b8101908080359060200190929190803590602001909291905050506107c7565b005b61053f6004803603606081101561051557600080fd5b810190808035906020019092919080359060200190929190803590602001909291905050506107e2565b005b6105776004803603604081101561055757600080fd5b810190808035906020019092919080359060200190929190505050610803565b005b6105af6004803603604081101561058f57600080fd5b81019080803590602001909291908035906020019092919050505061081e565b005b6105dd600480360360208110156105c757600080fd5b8101908080359060200190929190505050610839565b005b610615600480360360408110156105f557600080fd5b81019080803590602001909291908035906020019092919050505061084e565b005b6004358015604051818152602081a050505050565b60043560243580820a604051818152602081a0505050505050565b600435602435808204604051818152602081a0505050505050565b600435602435808214604051818152602081a0505050505050565b600435602435808213604051818152602081a0505050505050565b600435602435808203604051818152602081a0505050505050565b600435602435808207604051818152602081a0505050505050565b60043560243560443580828409604051818152602081a05050505050505050565b600435602435808206604051818152602081a0505050505050565b600435602435808205604051818152602081a0505050505050565b600435602435808211604051818152602081a0505050505050565b600435602435808216604051818152602081a0505050505050565b600435602435808202604051818152602081a0505050505050565b60043560243580820b604051818152602081a0505050505050565b600435602435808217604051818152602081a0505050505050565b600435602435808210604051818152602081a0505050505050565b600435602435808201604051818152602081a0505050505050565b60043560243560443580828408604051818152602081a05050505050505050565b60043560243580821a604051818152602081a0505050505050565b600435602435808218604051818152602081a0505050505050565b6004358019604051818152602081a050505050565b600435602435808212604051818152602081a050505050505056fea265627a7a72305820440ab335f377421f7a894e460bacc8fa0829d63a73c9656a20c29b34f32ec2da64736f6c634300050a0032";

    public static final String FUNC_ISZERO = "iszero";

    public static final String FUNC_EXP = "exp";

    public static final String FUNC_DIV = "div";

    public static final String FUNC_EQ = "eq";

    public static final String FUNC_SGT = "sgt";

    public static final String FUNC_SUB = "sub";

    public static final String FUNC_SMOD = "smod";

    public static final String FUNC_MULMOD = "mulmod";

    public static final String FUNC_MOD = "mod";

    public static final String FUNC_SDIV = "sdiv";

    public static final String FUNC_GT = "gt";

    public static final String FUNC_AND = "and";

    public static final String FUNC_MUL = "mul";

    public static final String FUNC_SIGNEXTEND = "signextend";

    public static final String FUNC_OR = "or";

    public static final String FUNC_LT = "lt";

    public static final String FUNC_ADD = "add";

    public static final String FUNC_ADDMOD = "addmod";

    public static final String FUNC_CALLBYTE = "callByte";

    public static final String FUNC_XOR = "xor";

    public static final String FUNC_NOT = "not";

    public static final String FUNC_SLT = "slt";

    @Deprecated
    protected Kevin(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected Kevin(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected Kevin(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected Kevin(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public RemoteCall<TransactionReceipt> iszero(byte[] a) {
        final Function function = new Function(
                FUNC_ISZERO, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(a)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> exp(byte[] a, byte[] b) {
        final Function function = new Function(
                FUNC_EXP, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(a), 
                new org.web3j.abi.datatypes.generated.Bytes32(b)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> div(byte[] a, byte[] b) {
        final Function function = new Function(
                FUNC_DIV, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(a), 
                new org.web3j.abi.datatypes.generated.Bytes32(b)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> eq(byte[] a, byte[] b) {
        final Function function = new Function(
                FUNC_EQ, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(a), 
                new org.web3j.abi.datatypes.generated.Bytes32(b)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> sgt(byte[] a, byte[] b) {
        final Function function = new Function(
                FUNC_SGT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(a), 
                new org.web3j.abi.datatypes.generated.Bytes32(b)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> sub(byte[] a, byte[] b) {
        final Function function = new Function(
                FUNC_SUB, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(a), 
                new org.web3j.abi.datatypes.generated.Bytes32(b)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> smod(byte[] a, byte[] b) {
        final Function function = new Function(
                FUNC_SMOD, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(a), 
                new org.web3j.abi.datatypes.generated.Bytes32(b)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> mulmod(byte[] a, byte[] b, byte[] c) {
        final Function function = new Function(
                FUNC_MULMOD, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(a), 
                new org.web3j.abi.datatypes.generated.Bytes32(b), 
                new org.web3j.abi.datatypes.generated.Bytes32(c)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> mod(byte[] a, byte[] b) {
        final Function function = new Function(
                FUNC_MOD, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(a), 
                new org.web3j.abi.datatypes.generated.Bytes32(b)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> sdiv(byte[] a, byte[] b) {
        final Function function = new Function(
                FUNC_SDIV, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(a), 
                new org.web3j.abi.datatypes.generated.Bytes32(b)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> gt(byte[] a, byte[] b) {
        final Function function = new Function(
                FUNC_GT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(a), 
                new org.web3j.abi.datatypes.generated.Bytes32(b)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> and(byte[] a, byte[] b) {
        final Function function = new Function(
                FUNC_AND, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(a), 
                new org.web3j.abi.datatypes.generated.Bytes32(b)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> mul(byte[] a, byte[] b) {
        final Function function = new Function(
                FUNC_MUL, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(a), 
                new org.web3j.abi.datatypes.generated.Bytes32(b)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> signextend(byte[] a, byte[] b) {
        final Function function = new Function(
                FUNC_SIGNEXTEND, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(a), 
                new org.web3j.abi.datatypes.generated.Bytes32(b)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> or(byte[] a, byte[] b) {
        final Function function = new Function(
                FUNC_OR, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(a), 
                new org.web3j.abi.datatypes.generated.Bytes32(b)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> lt(byte[] a, byte[] b) {
        final Function function = new Function(
                FUNC_LT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(a), 
                new org.web3j.abi.datatypes.generated.Bytes32(b)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> add(byte[] a, byte[] b) {
        final Function function = new Function(
                FUNC_ADD, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(a), 
                new org.web3j.abi.datatypes.generated.Bytes32(b)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> addmod(byte[] a, byte[] b, byte[] c) {
        final Function function = new Function(
                FUNC_ADDMOD, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(a), 
                new org.web3j.abi.datatypes.generated.Bytes32(b), 
                new org.web3j.abi.datatypes.generated.Bytes32(c)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> callByte(byte[] a, byte[] b) {
        final Function function = new Function(
                FUNC_CALLBYTE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(a), 
                new org.web3j.abi.datatypes.generated.Bytes32(b)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> xor(byte[] a, byte[] b) {
        final Function function = new Function(
                FUNC_XOR, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(a), 
                new org.web3j.abi.datatypes.generated.Bytes32(b)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> not(byte[] a) {
        final Function function = new Function(
                FUNC_NOT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(a)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> slt(byte[] a, byte[] b) {
        final Function function = new Function(
                FUNC_SLT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(a), 
                new org.web3j.abi.datatypes.generated.Bytes32(b)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public static Function iszeroFunction(byte[] a) {
        final Function function = new Function(
                FUNC_ISZERO, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(a)), 
                Collections.<TypeReference<?>>emptyList());
        return function;
    }

    public static Function expFunction(byte[] a, byte[] b) {
        final Function function = new Function(
                FUNC_EXP, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(a), 
                new org.web3j.abi.datatypes.generated.Bytes32(b)), 
                Collections.<TypeReference<?>>emptyList());
        return function;
    }

    public static Function divFunction(byte[] a, byte[] b) {
        final Function function = new Function(
                FUNC_DIV, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(a), 
                new org.web3j.abi.datatypes.generated.Bytes32(b)), 
                Collections.<TypeReference<?>>emptyList());
        return function;
    }

    public static Function eqFunction(byte[] a, byte[] b) {
        final Function function = new Function(
                FUNC_EQ, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(a), 
                new org.web3j.abi.datatypes.generated.Bytes32(b)), 
                Collections.<TypeReference<?>>emptyList());
        return function;
    }

    public static Function sgtFunction(byte[] a, byte[] b) {
        final Function function = new Function(
                FUNC_SGT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(a), 
                new org.web3j.abi.datatypes.generated.Bytes32(b)), 
                Collections.<TypeReference<?>>emptyList());
        return function;
    }

    public static Function subFunction(byte[] a, byte[] b) {
        final Function function = new Function(
                FUNC_SUB, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(a), 
                new org.web3j.abi.datatypes.generated.Bytes32(b)), 
                Collections.<TypeReference<?>>emptyList());
        return function;
    }

    public static Function smodFunction(byte[] a, byte[] b) {
        final Function function = new Function(
                FUNC_SMOD, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(a), 
                new org.web3j.abi.datatypes.generated.Bytes32(b)), 
                Collections.<TypeReference<?>>emptyList());
        return function;
    }

    public static Function mulmodFunction(byte[] a, byte[] b, byte[] c) {
        final Function function = new Function(
                FUNC_MULMOD, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(a), 
                new org.web3j.abi.datatypes.generated.Bytes32(b), 
                new org.web3j.abi.datatypes.generated.Bytes32(c)), 
                Collections.<TypeReference<?>>emptyList());
        return function;
    }

    public static Function modFunction(byte[] a, byte[] b) {
        final Function function = new Function(
                FUNC_MOD, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(a), 
                new org.web3j.abi.datatypes.generated.Bytes32(b)), 
                Collections.<TypeReference<?>>emptyList());
        return function;
    }

    public static Function sdivFunction(byte[] a, byte[] b) {
        final Function function = new Function(
                FUNC_SDIV, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(a), 
                new org.web3j.abi.datatypes.generated.Bytes32(b)), 
                Collections.<TypeReference<?>>emptyList());
        return function;
    }

    public static Function gtFunction(byte[] a, byte[] b) {
        final Function function = new Function(
                FUNC_GT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(a), 
                new org.web3j.abi.datatypes.generated.Bytes32(b)), 
                Collections.<TypeReference<?>>emptyList());
        return function;
    }

    public static Function andFunction(byte[] a, byte[] b) {
        final Function function = new Function(
                FUNC_AND, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(a), 
                new org.web3j.abi.datatypes.generated.Bytes32(b)), 
                Collections.<TypeReference<?>>emptyList());
        return function;
    }

    public static Function mulFunction(byte[] a, byte[] b) {
        final Function function = new Function(
                FUNC_MUL, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(a), 
                new org.web3j.abi.datatypes.generated.Bytes32(b)), 
                Collections.<TypeReference<?>>emptyList());
        return function;
    }

    public static Function signextendFunction(byte[] a, byte[] b) {
        final Function function = new Function(
                FUNC_SIGNEXTEND, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(a), 
                new org.web3j.abi.datatypes.generated.Bytes32(b)), 
                Collections.<TypeReference<?>>emptyList());
        return function;
    }

    public static Function orFunction(byte[] a, byte[] b) {
        final Function function = new Function(
                FUNC_OR, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(a), 
                new org.web3j.abi.datatypes.generated.Bytes32(b)), 
                Collections.<TypeReference<?>>emptyList());
        return function;
    }

    public static Function ltFunction(byte[] a, byte[] b) {
        final Function function = new Function(
                FUNC_LT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(a), 
                new org.web3j.abi.datatypes.generated.Bytes32(b)), 
                Collections.<TypeReference<?>>emptyList());
        return function;
    }

    public static Function addFunction(byte[] a, byte[] b) {
        final Function function = new Function(
                FUNC_ADD, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(a), 
                new org.web3j.abi.datatypes.generated.Bytes32(b)), 
                Collections.<TypeReference<?>>emptyList());
        return function;
    }

    public static Function addmodFunction(byte[] a, byte[] b, byte[] c) {
        final Function function = new Function(
                FUNC_ADDMOD, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(a), 
                new org.web3j.abi.datatypes.generated.Bytes32(b), 
                new org.web3j.abi.datatypes.generated.Bytes32(c)), 
                Collections.<TypeReference<?>>emptyList());
        return function;
    }

    public static Function callByteFunction(byte[] a, byte[] b) {
        final Function function = new Function(
                FUNC_CALLBYTE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(a), 
                new org.web3j.abi.datatypes.generated.Bytes32(b)), 
                Collections.<TypeReference<?>>emptyList());
        return function;
    }

    public static Function xorFunction(byte[] a, byte[] b) {
        final Function function = new Function(
                FUNC_XOR, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(a), 
                new org.web3j.abi.datatypes.generated.Bytes32(b)), 
                Collections.<TypeReference<?>>emptyList());
        return function;
    }

    public static Function notFunction(byte[] a) {
        final Function function = new Function(
                FUNC_NOT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(a)), 
                Collections.<TypeReference<?>>emptyList());
        return function;
    }

    public static Function sltFunction(byte[] a, byte[] b) {
        final Function function = new Function(
                FUNC_SLT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(a), 
                new org.web3j.abi.datatypes.generated.Bytes32(b)), 
                Collections.<TypeReference<?>>emptyList());
        return function;
    }

    @Deprecated
    public static Kevin load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new Kevin(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static Kevin load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new Kevin(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static Kevin load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new Kevin(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static Kevin load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new Kevin(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<Kevin> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(Kevin.class, web3j, credentials, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<Kevin> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(Kevin.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    public static RemoteCall<Kevin> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(Kevin.class, web3j, transactionManager, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<Kevin> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(Kevin.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }
}
