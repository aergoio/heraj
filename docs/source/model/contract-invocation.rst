Contract Invocation
===================

Contract Invocation is a model for smart contract invocation. It can be both execution or query. You need a ContractInterface to make an new ContractInvocation.

Make
----

Without args.

.. code-block:: java

  // make a contract invocation
  ContractInterface contractInterface = contractInterfaceKeep;
  ContractInvocation contractInvocation = contractInterface.newInvocationBuilder()
      .function("set")
      .build();
  System.out.println("Contract invocation: " + contractInvocation);

With args.

.. code-block:: java

  // make a contract invocation
  ContractInterface contractInterface = contractInterfaceKeep;
  ContractInvocation contractInvocation = contractInterface.newInvocationBuilder()
      .function("set")
      .args("key", 333, "test2")
      .build();
  System.out.println("Contract invocation: " + contractInvocation);

With args and amount.

.. code-block:: java

  // make a contract invocation
  ContractInterface contractInterface = contractInterfaceKeep;
  ContractInvocation contractInvocation = contractInterface.newInvocationBuilder()
      .function("set")
      .args("key", 333, "test2")
      .amount(Aer.AERGO_ONE)
      .build();
  System.out.println("Contract invocation: " + contractInvocation);

With args and fee delegation.

.. code-block:: java

  // make a contract invocation
  ContractInterface contractInterface = contractInterfaceKeep;
  ContractInvocation contractInvocation = contractInterface.newInvocationBuilder()
      .function("set")
      .args("key", 333, "test2")
      .delegateFee(true)
      .build();
  System.out.println("Contract invocation: " + contractInvocation);