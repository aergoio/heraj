BlockOperation
==============

Provides block related operations.

Get Block
---------

You can fetch block information.

By Hash

.. code-block:: java

  BlockHash blockHash = BlockHash.of("DN9TvryaThbJneSpzaXp5ZsS4gE3UMzKfaXC4x8L5qR1");
  Block blockByHash = client.getBlockOperation().getBlock(blockHash);

By Height

.. code-block:: java

  long height = 27_066_653L;
  Block blockByHeight = client.getBlockOperation().getBlock(height);

Get Block Metadata
------------------

You can fetch block metadata information only.

By Hash

.. code-block:: java

  BlockHash blockHash = BlockHash.of("DN9TvryaThbJneSpzaXp5ZsS4gE3UMzKfaXC4x8L5qR1");
  BlockMetadata metadataByHash = client.getBlockOperation().getBlockMetadata(blockHash);

By Height

.. code-block:: java

  long height = 27_066_653L;
  BlockMetadata metadataByHeight = client.getBlockOperation().getBlockMetadata(height);

List Block Metadata
-------------------

You can fetch multiple blocks with a list size. Size maximum is 1000.

By Hash

It fetch blocks backwardly from provided hash with a given size.

.. code-block:: java

  BlockHash blockHash = BlockHash.of("DN9TvryaThbJneSpzaXp5ZsS4gE3UMzKfaXC4x8L5qR1");
  List<BlockMetadata> metadatasByHash = client.getBlockOperation()
      .listBlockMetadatas(blockHash, 100);

By Height

It fetch blocks backwardly from provided height with a given size.

.. code-block:: java

  long height = 27_066_653L;
  List<BlockMetadata> metadatasByHeight = client.getBlockOperation()
      .listBlockMetadatas(height, 100);

Subscription
------------

You can subscribe new block everytime it's created.

Subscribe block

.. code-block:: java

  // make a subscription
  Subscription<Block> blockSubscription = client.getBlockOperation()
      .subscribeNewBlock(new StreamObserver<Block>() {
        @Override
        public void onNext(Block value) {
          System.out.println("Next: " + value);
        }

        @Override
        public void onError(Throwable t) {
        }

        @Override
        public void onCompleted() {
        }
      });

  // wait for a while
  Thread.sleep(2000L);

  // unsubscribe block stream
  blockSubscription.unsubscribe();

Subscribe block metadata

.. code-block:: java

  // make a subscription
  Subscription<BlockMetadata> metadataSubscription = client
      .getBlockOperation().subscribeNewBlockMetadata(new StreamObserver<BlockMetadata>() {
        @Override
        public void onNext(BlockMetadata value) {
          System.out.println("Next: " + value);
        }

        @Override
        public void onError(Throwable t) {

        }

        @Override
        public void onCompleted() {
        }
      });

  // wait for a while
  Thread.sleep(2000L);

  // unsubscribe block metadata stream
  metadataSubscription.unsubscribe();