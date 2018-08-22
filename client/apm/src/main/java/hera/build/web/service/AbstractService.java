/*
 * @copyright defined in LICENSE.txt
 */

package hera.build.web.service;

import static org.slf4j.LoggerFactory.getLogger;

import org.eclipse.jetty.server.handler.AbstractHandler;
import org.slf4j.Logger;

public abstract class AbstractService extends AbstractHandler {
  protected final transient Logger logger = getLogger(getClass());

}
