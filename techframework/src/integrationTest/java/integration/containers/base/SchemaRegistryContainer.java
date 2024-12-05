package integration.containers.base;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

public class SchemaRegistryContainer extends GenericContainer<SchemaRegistryContainer> {

  private static final DockerImageName imageName =
      DockerImageName.parse("confluentinc/cp-schema-registry:7.6.0");

  public SchemaRegistryContainer() {
    super(imageName);
    withNetwork(SingletonContainerNetwork.getInstance());
    withExposedPorts(8081); // Default port for Schema Registry
    withEnv("SCHEMA_REGISTRY_HOST_NAME", "localhost");
    withEnv("SCHEMA_REGISTRY_LISTENERS", "http://0.0.0.0:8081");
    withEnv("SCHEMA_REGISTRY_KAFKASTORE_BOOTSTRAP_SERVERS", "kafka:9092");
  }

  public String getUrl() {
    return String.format("http://%s:%d", getHost(), getFirstMappedPort());
  }
}
