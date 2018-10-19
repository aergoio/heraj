/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import hera.api.encode.Base58WithCheckSum;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@EqualsAndHashCode
@NoArgsConstructor
public class ContractDefinition {

  public static ContractDefinition of(final Base58WithCheckSum encodedContract) {
    return new ContractDefinition(encodedContract);
  }

  public static ContractDefinition of(final Base58WithCheckSum encodedContract,
      final Object... args) {
    return new ContractDefinition(encodedContract, args);
  }

  @Setter
  @Getter
  protected Base58WithCheckSum encodedContract;

  @Setter
  @Getter
  protected Object[] constructorArgs = new Object[0];

  public ContractDefinition(final Base58WithCheckSum encodedContract) {
    this(encodedContract, new Object[0]);
  }

  public ContractDefinition(final Base58WithCheckSum encodedContract, final Object... args) {
    this.encodedContract = encodedContract;
    this.constructorArgs = args;
  }

}
