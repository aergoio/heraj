package hera.api.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum TransactionType {
  UNRECOGNIZED(-1),
  NORMAL(0),
  GOVERNANCE(1);

  @Getter
  private final int intValue;
}
