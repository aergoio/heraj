EventFilter
===========

Aergo smart contract has event. It can be occured in specific block. Heraj provides api for querying event with a filtering.

Make
----

With block bumber.

.. code-block:: java

  // set event filter for specific address in block 1 ~ 10
  ContractAddress contractAddress = ContractAddress
      .of("AmNrsAqkXhQfE6sGxTutQkf9ekaYowaJFLekEm8qvDr1RB1AnsiM");
  EventFilter eventFilter = EventFilter.newBuilder(contractAddress)
      .fromBlockNumber(1L)
      .toBlockNumber(10L)
      .build();
  System.out.println("Event filter: " + eventFilter);

Of recent block.

.. code-block:: java

  // set event filter for specific address in recent 1000 block
  ContractAddress contractAddress = ContractAddress
      .of("AmNrsAqkXhQfE6sGxTutQkf9ekaYowaJFLekEm8qvDr1RB1AnsiM");
  EventFilter eventFilter = EventFilter.newBuilder(contractAddress)
      .eventName("set")
      .recentBlockCount(1000)
      .build();
  System.out.println("Event filter: " + eventFilter);

By event name and args.

.. code-block:: java

  // set event filter for specific address with name "set" and args "key" in recent 1000 block
  ContractAddress contractAddress = ContractAddress
      .of("AmNrsAqkXhQfE6sGxTutQkf9ekaYowaJFLekEm8qvDr1RB1AnsiM");
  EventFilter eventFilter = EventFilter.newBuilder(contractAddress)
      .eventName("set")
      .args("key")
      .recentBlockCount(1000)
      .build();
  System.out.println("Event filter: " + eventFilter);