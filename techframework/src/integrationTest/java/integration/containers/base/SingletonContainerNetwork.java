package integration.containers.base;

import org.testcontainers.containers.Network;

public class SingletonContainerNetwork {

  private static Network network;

  static {
    network = Network.newNetwork();
  }

  private SingletonContainerNetwork() {
    // Prevent instantiation
  }

  public static Network getInstance() {
    return network;
  }
}
