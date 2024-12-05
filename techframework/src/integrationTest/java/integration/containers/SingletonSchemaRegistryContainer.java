package integration.containers;

import integration.containers.base.SchemaRegistryContainer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SingletonSchemaRegistryContainer {

  private static SchemaRegistryContainer container;

  static {
    container = new SchemaRegistryContainer();
    container.start();
  }

  private SingletonSchemaRegistryContainer() {
    // Prevent instantiation
  }

  public static SchemaRegistryContainer getInstance() {
    return container;
  }
}
