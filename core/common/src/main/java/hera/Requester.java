package hera;

import java.util.List;

public interface Requester {

  <T> T request(Invocation<T> invocation) throws Exception;

  <T> Invocation<T> getInvocation();

}
