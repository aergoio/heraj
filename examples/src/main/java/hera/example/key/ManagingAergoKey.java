/*
 * @copyright defined in LICENSE.txt
 */

package hera.example.key;

import hera.api.model.EncryptedPrivateKey;
import hera.example.AbstractExample;
import hera.key.AergoKey;
import hera.key.AergoKeyGenerator;

public class ManagingAergoKey extends AbstractExample {

  @Override
  public void run() throws Exception {
    // create new key
    AergoKey newKey = new AergoKeyGenerator().create();
    System.out.println("Create arbitary key: " + newKey);

    // export key
    AergoKey exportTarget = new AergoKeyGenerator().create();
    String encryptPassword = "password";
    EncryptedPrivateKey exported = exportTarget.export(encryptPassword);
    System.out.println("Exported one " + exported);

    // create key with a seed
    // (should be 'AmP4B5dPxamKp1kwFzAVGSzK1NHzX3LSnpcfE77gAyVet8gCPQRR')
    String seed = "randomseed";
    AergoKey withSeed = new AergoKeyGenerator().create(seed);
    System.out.println("Create key with seed: " + withSeed);

    // recover key with encrypted one
    // (should be 'AmP4B5dPxamKp1kwFzAVGSzK1NHzX3LSnpcfE77gAyVet8gCPQRR')
    String encrypted = "47R1csG1CiCQ8nXLXPGduSQTwQkG8sbr7k31m5yCuKDaFyBRf84tU7cMiqjXJmVAPbcBxEXSZ";
    String decryptPassword = "password";
    AergoKey recoverted = AergoKey.of(encrypted, decryptPassword);
    System.out.println("Recover key: " + recoverted);
  }

  public static void main(String[] args) throws Exception {
    new ManagingAergoKey().run();
  }

}
