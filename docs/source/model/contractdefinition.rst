ContractDefinition
==================

ContractDefinition is a model for contract written in lua. For more about writing lua smart contract, see `Programming Guide <https://docs.aergo.io/en/latest/smart-contracts/lua/guide.html>`_.

Make
----

Without args.

.. code-block:: java

  // made by aergoluac --payload {some_contract}.lua
  String encodedContract = contractPayload;

  // make a contract definition
  ContractDefinition contractDefinition = ContractDefinition.newBuilder()
      .encodedContract(encodedContract)
      .build();
  System.out.println("Contract definition: " + contractDefinition);

With args.

.. code-block:: java

  // made by aergoluac --payload {some_contract}.lua
  String encodedContract = contractPayload;

  // make a contract definition
  ContractDefinition contractDefinition = ContractDefinition.newBuilder()
      .encodedContract(encodedContract)
      .constructorArgs("key", 123, "test")
      .build();
  System.out.println("Contract definition: " + contractDefinition);

With args and amount.

.. code-block:: java

  // made by aergoluac --payload {some_contract}.lua
  String encodedContract = contractPayload;

  // make a contract definition
  ContractDefinition contractDefinition = ContractDefinition.newBuilder()
      .encodedContract(encodedContract)
      .constructorArgs("key", 123, "test")
      .amount(Aer.AERGO_ONE)
      .build();
  System.out.println("Contract definition: " + contractDefinition);