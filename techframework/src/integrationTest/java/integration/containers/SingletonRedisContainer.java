package integration.containers;

import lombok.extern.slf4j.Slf4j;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

@Slf4j
public class SingletonRedisContainer {

  private static GenericContainer<?> container;

  static {
    container =
        new GenericContainer<>("redis:7.0.7")
            .withNetworkAliases("redis")
            .withExposedPorts(6379)
            .waitingFor(Wait.forListeningPort())
            .withLogConsumer(outputFrame -> log.info(outputFrame.getUtf8String()));
    container.start();
  }

  private SingletonRedisContainer() {
    // Prevent instantiation
  }

  public static GenericContainer<?> getInstance() {
    return container;
  }
}
