package hera.spec;

import hera.api.model.AccountAddress;
import hera.api.model.ContractDefinition;
import hera.api.model.ContractInvocation;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class AergoSpec {

  /* address, name, private key */

  public static final byte ADDRESS_PREFIX = 0x42;

  // [odd|even] of publickey.y + [optional 0x00] + publickey.x
  // which is equivalent with s compressed public key (see also X9.62 s 4.2.1)
  public static final int ADDRESS_BYTE_LENGTH = 33;

  public static final int NAME_LENGTH = 12;

  public static final byte ENCRYPTED_PRIVATE_KEY_PREFIX = (byte) 0xAA;


  /* amount unit */

  @RequiredArgsConstructor
  public enum Unit {
    AER("Aer", new BigDecimal("1"), new BigDecimal("1")),
    GAER("Gaer", new BigDecimal("1.E-9"), new BigDecimal("1.E9")),
    AERGO("Aergo", new BigDecimal("1.E-18"), new BigDecimal("1.E18"));

    @Getter
    protected final String name;

    @Getter
    protected final BigDecimal minimum;

    @Getter
    protected final BigDecimal ratio;
  }


  /* payload */

  public static final String PAYLOAD_VERSION = "v1";

  public enum PayloadType {
    ContractDefinition("", ContractDefinition.class),
    ContractInvocation("", ContractInvocation.class),
    Vote("", String.class, String[].class),
    Stake("stake"),
    Unstake("unstake"),
    CreateName("createName", String.class),
    UpdateName("updateName", String.class, AccountAddress.class);

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
