package hera.wallet;

interface LockableCabinet<T> {

  T getUnlocked();

  void setUnlocked(T t);

  boolean lock();

  boolean isUnlocked();

}
