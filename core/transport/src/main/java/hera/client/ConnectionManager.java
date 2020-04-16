package hera.client;

import io.grpc.Channel;
import java.io.Closeable;

// TODO : convert to be generics
public interface ConnectionManager extends Closeable {

  Channel getConnection();

}
