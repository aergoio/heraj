/*
 * @copyright defined in LICENSE.txt
 */

package hera.wallet;

import hera.client.AergoClient;

interface ClientProvider {

  AergoClient getClient();

}
