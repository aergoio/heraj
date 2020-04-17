package hera.client;

import java.io.Closeable;

interface ClientProvider<ClientT> extends Closeable {

  ClientT get();

}
