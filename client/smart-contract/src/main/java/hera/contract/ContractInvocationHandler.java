package hera.contract;

import static org.slf4j.LoggerFactory.getLogger;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.ContractAddress;
import hera.api.model.ContractFunction;
import hera.api.model.ContractInterface;
import hera.api.model.ContractInvocation;
import hera.api.model.ContractResult;
import hera.api.model.Fee;
import hera.exception.ContractException;
import hera.util.StringUtils;
import hera.wallet.Wallet;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;

@ApiAudience.Private
@ApiStability.Unstable
public class ContractInvocationHandler implements InvocationHandler {

  protected final transient Logger logger = getLogger(getClass());

  @Getter(value = AccessLevel.PROTECTED)
  protected ContractAddress contractAddress;

  @Getter(value = AccessLevel.PROTECTED)
  protected ContractInterface contractInterface;

  @Setter
  protected Wallet wallet;

  @Getter(value = AccessLevel.PROTECTED)
  @Setter
  protected Fee fee = Fee.ZERO;

  ContractInvocationHandler(final ContractAddress contractAddress) {
    if (null == contractAddress) {
      throw new ContractException("Contract address must not null");
    }
    this.contractAddress = contractAddress;
  }

  protected Wallet getWallet() {
    if (null == this.wallet) {
      throw new ContractException("No binded wallet");
    }
    return this.wallet;
  }

  @Override
  public Object invoke(final Object proxy, final Method method, final Object[] args)
      throws Throwable {
    try {
      logger.debug("Method: {}", method);
      if (!method.getDeclaringClass().isInterface()) {
        return method.invoke(this, args);
      }

      logger.debug("Proxy: {}, Method: {}, Args: {}, Ret: {}", proxy, method,
          (args == null) ? null : StringUtils.join(args, ","));

      if (isContractConfig(method)) {
        logger.debug("Invocation is contract config: {}", method);
        invokeConfigMethod(proxy, method, args);
        return null;
      } else {
        logger.debug("Invocation is contract call: {}", method);
        return invokeUserMethod(proxy, method, args);
      }
    } catch (ContractException e) {
      throw e;
    } catch (Exception e) {
      throw new ContractException(e);
    }
  }

  protected boolean isContractConfig(final Method method) {
    return "bind".equals(method.getName());
  }

  protected void invokeConfigMethod(final Object proxy, final Method method, final Object[] args) {
    if (args.length > 1) {
      throw new ContractException("Illegal number of arguments for method \'bind\'");
    }

    final Object arg = args[0];
    if (arg instanceof Wallet) {
      setWallet((Wallet) arg);
    } else if (arg instanceof Fee) {
      setFee((Fee) arg);
    } else {
      throw new ContractException(
          "Unable to process bind argument for type " + arg.getClass().getName());
    }
  }

  protected Object invokeUserMethod(final Object proxy, final Method method, final Object[] args)
      throws Exception {
    final Wallet wallet = getWallet();
    if (null == getContractInterface()) {
      final ContractInterface contractInterface = wallet.getContractInterface(getContractAddress());
      validateInterface(proxy, contractInterface);
      this.contractInterface = contractInterface;
    }

    final ContractInterface contractInterface = getContractInterface();
    final ContractInvocation contractInvocation = contractInterface.newInvocationBuilder()
        .function(method.getName())
        .args(args)
        .build();

    if (Void.TYPE.equals(method.getReturnType())) {
      logger.debug("Contract execution: {}", contractInvocation);
      if (contractInvocation.getFunction().isView()) {
        throw new ContractException(
            "Unable to execute with function registered with abi.register_view()");
      }
      wallet.execute(contractInvocation, getFee());
      return null;
    } else {
      logger.debug("Contract query: {}", contractInvocation);
      if (!contractInvocation.getFunction().isView()) {
        throw new ContractException("Unable to query with function registered with abi.register()");
      }
      ContractResult result = wallet.query(contractInvocation);
      return result.bind(method.getReturnType());
    }
  }

  protected void validateInterface(final Object proxy, final ContractInterface contractInterface) {
    logger.debug("Validate {} with {}", proxy, contractInterface);
    for (final Method method : proxy.getClass().getDeclaredMethods()) {
      if (isJavaObjectMethod(method) || isContractSpecificMethod(method)) {
        continue;
      }
      // check method name
      final String methodName = method.getName();
      final ContractFunction contractFunction = contractInterface.findFunction(methodName);
      if (null == contractFunction) {
        throw new ContractException("No method " + methodName + " in contract interface");
      }
      // check method parameter count
      final int methodParameterCount = method.getParameterTypes().length;
      final int actualArgCount = contractFunction.getArgumentNames().size();
      if (methodParameterCount != actualArgCount) {
        throw new ContractException(String.format(
            "Method parameter count for %s is invalid (expected: %d)", methodName, actualArgCount));
      }
    }
  }

  protected boolean isJavaObjectMethod(final Method method) {
    final String methodName = method.getName();
    return "equals".equals(methodName) || "toString".equals(methodName)
        || "hashCode".equals(methodName);
  }

  protected boolean isContractSpecificMethod(final Method method) {
    final String name = method.getName();
    final int parameterCount = method.getParameterTypes().length;
    for (final Method preDefinedMethod : SmartContract.class.getMethods()) {
      if (name.equals(preDefinedMethod.getName())
          && parameterCount == preDefinedMethod.getParameterTypes().length) {
        return true;
      }
    }
    return false;
  }

}
