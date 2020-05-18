Contract Api
============

ContractApi provides java interface based smart contract call. ContractApi automatically fill nonce for signer. It commit fails by nonce error, it automatically fetch right nonce and retry with it.

Prepare
-------

To use ContractApi, first you have to deploy smart contract. Then, write an interface corresponding to smart contract functions.

Write a smart contract. For more about writing lua smart contract, see `Programming Guide <https://docs.aergo.io/en/latest/smart-contracts/lua/guide.html>`_.

.. code-block:: lua

  function constructor(key, arg1, arg2)
    if key ~= nil then
      system.setItem(key, {intVal=arg1, stringVal=arg2})
    end
  end

  function set(key, arg1, arg2)
    contract.event("set", key, arg1, arg2)
    system.setItem(key, {intVal=arg1, stringVal=arg2})
  end

  function get(key)
    return system.getItem(key)
  end

  function check_delegation()
    return true
  end

  abi.register_view(get)
  abi.register(set)
  abi.fee_delegation(set)
  abi.payable(set)

Deploy smart contract.

.. code-block:: java

  // make a contract definition
  String encodedContract = contractPayload;
  ContractDefinition contractDefinition = ContractDefinition.newBuilder()
      .encodedContract(encodedContract)
      .build();

  // deploy contract
  walletApi.unlock(authentication);
  TxHash txHash = walletApi.with(client).transaction()
      .deploy(contractDefinition, Fee.INFINITY);
  walletApi.lock();

  // sleep
  Thread.sleep(2000L);

  // get ContractTxReceipt
  ContractTxReceipt contractTxReceipt = walletApi.with(client).query()
      .getContractTxReceipt(txHash);

  // get contract address
  ContractAddress contractAddress = contractTxReceipt.getContractAddress();
  System.out.println("Deployed contract address: " + contractPayload);

Write an interface. Interface methods should matches with smart contract functions.

.. code-block:: java

  // interface for smart contract
  interface CustomInterface1 {

    /*
      Matches with

        function set(key, arg1, arg2)
          ...
        end

        ...

        abi.register(set)

      And it also uses provided fee when making transaction.
     */
    TxHash set(String key, int arg1, String args2, Fee fee);

    /*
      Matches with

        function set(key, arg1, arg2)
          ...
        end

        ...

        abi.register(set)

      And it also uses Fee.INFINITY when making transaction.
     */
    TxHash set(String key, int arg1, String args2);

    /*
      Matches with

        function get(key)
          ...
          -- returns lua table which can be binded with Data class
          return someVal
        end

        ...

        abi.register_view(get)
     */
    Data get(String key);

  }

  // java bean
  class Data {

    protected int intVal;

    protected String stringVal;

    public int getIntVal() {
      return intVal;
    }

    public void setIntVal(int intVal) {
      this.intVal = intVal;
    }

    public String getStringVal() {
      return stringVal;
    }

    public void setStringVal(String stringVal) {
      this.stringVal = stringVal;
    }

    @Override
    public String toString() {
      return "Data{" +
          "intVal=" + intVal +
          ", stringVal=" + stringVal +
          '}';
    }
  }

Make
----

Given deployed smart contract and an java interface to use it, you can make a ContractApi for it.

Make a ContractApi with implicit retry count and interval on nonce failure.

.. code-block:: java

  // create a contract api
  ContractAddress contractAddress = deployedContractAddress;
  ContractApi<CustomInterface1> contractApi = new ContractApiFactory()
      .create(contractAddress, CustomInterface1.class);
  System.out.println("ContractApi: " + contractApi);

Make a ContractApi with explicit retry count and interval on nonce failure.

.. code-block:: java

  // create a contract api with retry count 5 and interval 1000ms
  ContractAddress contractAddress = deployedContractAddress;
  TryCountAndInterval tryCountAndInterval = TryCountAndInterval.of(5, Time.of(1000L));
  ContractApi<CustomInterface1> contractApi = new ContractApiFactory()
      .create(contractAddress, CustomInterface1.class, tryCountAndInterval);
  System.out.println("ContractApi: " + contractApi);

Execute
-------

With an AergoKey.

.. code-block:: java

  // prepare an signer
  AergoKey signer = richKey;

  // create a contract api
  ContractAddress contractAddress = deployedContractAddress;
  ContractApi<CustomInterface1> contractApi = new ContractApiFactory()
      .create(contractAddress, CustomInterface1.class);

  // execute contract with a contract api
  TxHash executeTxHash = contractApi.with(client).execution(signer)
      .set("key", 123, "test", Fee.INFINITY);
  System.out.println("Execute tx hash: " + executeTxHash);

With a WalletApi.

.. code-block:: java

  // create a contract api
  ContractAddress contractAddress = deployedContractAddress;
  ContractApi<CustomInterface1> contractApi = new ContractApiFactory()
      .create(contractAddress, CustomInterface1.class);

  // execute contract with a contract api
  walletApi.unlock(authentication);
  TxHash executeTxHash = contractApi.with(client).execution(walletApi)
      .set("key", 123, "test", Fee.INFINITY);
  walletApi.lock();
  System.out.println("Execute tx hash: " + executeTxHash);

Query
-----

With a model binded.

.. code-block:: java

  // create a contract api
  ContractAddress contractAddress = deployedContractAddress;
  ContractApi<CustomInterface1> contractApi = new ContractApiFactory()
      .create(contractAddress, CustomInterface1.class);

  // query contract with a contract api
  Data data = contractApi.with(client).query().get("key");
  System.out.println("Queried data: " + data);

Without binded model.

.. code-block:: java

  // create a contract api
  ContractAddress contractAddress = deployedContractAddress;
  ContractApi<CustomInterface2> contractApi = new ContractApiFactory()
      .create(contractAddress, CustomInterface2.class);

  // query contract with a contract api
  ContractResult contractResult = contractApi.with(client).query().get("key");
  System.out.println("Queried data: " + contractResult);
