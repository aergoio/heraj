package hera.spec;

import hera.api.model.AccountAddress;
import hera.api.model.ContractDefinition;
import hera.api.model.ContractInvocation;
import lombok.Getter;

public class AergoSpec {

  /* payload */

  public static final String PAYLOAD_VERSION = "v1";

  public enum PayloadType {
    ContractDefinition("", ContractDefinition.class), ContractInvocation("",
        ContractInvocation.class), Vote("", String.class,
            String[].class), Stake("stake"), Unstake("unstake"), CreateName("createName",
                String.class), UpdateName("updateName", String.class, AccountAddress.class);

    @Getter
    protected final String name;
    @Getter
    protected final Class<?>[] targets;

    private PayloadType(final String name, final Class<?>... targets) {
      this.name = name;
      this.targets = targets;
    }
  }

  public static final String BIGNUM_JSON_KEY = "_bignum";

  public static final byte CONTRACT_PAYLOAD_VERSION = (byte) 0xC0;


  /* signature */

  public static final int SIGN_HEADER_MAGIC = 0x30;

  public static final int SIGN_INT_MARKER = 0x02;

  // minimum length of a DER encoded signature which both R and S are 1 byte each.
  // <header-magic> + <1-byte> + <int-marker> + 0x01 + <r.byte> + <int-marker> + 0x01 + <s.byte>
  public static final int SIGN_MINIMUM_LENGTH = 8;

}
