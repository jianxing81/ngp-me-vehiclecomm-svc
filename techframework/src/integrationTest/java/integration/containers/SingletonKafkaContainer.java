package integration.containers;

import integration.containers.base.SingletonContainerNetwork;
import lombok.extern.slf4j.Slf4j;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

@Slf4j
public class SingletonKafkaContainer {

  private static KafkaContainer container;

  private static final DockerImageName imageName =
      DockerImageName.parse("public.ecr.aws/sprig/cp-kafka:7.6.0")
          .asCompatibleSubstituteFor("confluentinc/cp-kafka");

  static {
    container =
        new KafkaContainer(imageName)
            .withNetwork(SingletonContainerNetwork.getInstance())
            .withNetworkAliases("kafka")
            .withLogConsumer(outputFrame -> log.info(outputFrame.getUtf8String()));

    container.start();
  }

  private SingletonKafkaContainer() {
    // Prevent instantiation
  }

  public static KafkaContainer getInstance() {
    return container;
  }
}
