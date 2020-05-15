BlockOperation
==============

Provides block related operations.

Get Block
---------

Get block. It returns null if no corresponding one.

By Hash.

.. code-block:: java

  BlockHash blockHash = BlockHash.of("DN9TvryaThbJneSpzaXp5ZsS4gE3UMzKfaXC4x8L5qR1");
  Block block = client.getBlockOperation().getBlock(blockHash);
  System.out.println("Block by hash: " + block);

By Height.

.. code-block:: java

  long height = 27_066_653L;
  Block block = client.getBlockOperation().getBlock(height);
  System.out.println("Block by height: " + block);

Get Block Metadata
------------------

Get block metadata. It returns null if no corresponding one.

By Hash.

.. code-block:: java

  BlockHash blockHash = BlockHash.of("DN9TvryaThbJneSpzaXp5ZsS4gE3UMzKfaXC4x8L5qR1");
  BlockMetadata blockMetadata = client.getBlockOperation().getBlockMetadata(blockHash);
  System.out.println("Block metadata by hash: " + blockMetadata);

By Height.

.. code-block:: java

  long height = 27_066_653L;
  BlockMetadata blockMetadata = client.getBlockOperation().getBlockMetadata(height);
  System.out.println("Block metadata by height: " + blockMetadata);

List Block Metadata
-------------------

Get block metadatas. Size maximum is 1000.

By Hash.

.. code-block:: java

  // block metadatas by from hash to previous 100 block
  BlockHash blockHash = BlockHash.of("DN9TvryaThbJneSpzaXp5ZsS4gE3UMzKfaXC4x8L5qR1");
  List<BlockMetadata> blockMetadatas = client.getBlockOperation()
      .listBlockMetadatas(blockHash, 100);
  System.out.println("Block metadatas by hash: " + blockMetadatas);

By Height.

.. code-block:: java

  // block metadatas by from height to previous 100 block
  long height = 27_066_653L;
  List<BlockMetadata> blockMetadatas = client.getBlockOperation()
      .listBlockMetadatas(height, 100);
  System.out.println("Block metadatas by height: " + blockMetadatas);

Block Subscription
------------------

Subscribe new generated block.

.. code-block:: java

  // make a subscription
  Subscription<Block> subscription = client.getBlockOperation()
      .subscribeBlock(new StreamObserver<Block>() {
        @Override
        public void onNext(Block value) {
          System.out.println("Next block: " + value);
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

  // unsubscribe it
  subscription.unsubscribe();

Block Metadata Subscription
---------------------------

Subscribe new generated block metadata.

.. code-block:: java

  // make a subscription
  Subscription<BlockMetadata> subscription = client
      .getBlockOperation().subscribeBlockMetadata(new StreamObserver<BlockMetadata>() {
        @Override
        public void onNext(BlockMetadata value) {
          System.out.println("Next block metadata: " + value);
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

  // unsubscribe it
  subscription.unsubscribe();
