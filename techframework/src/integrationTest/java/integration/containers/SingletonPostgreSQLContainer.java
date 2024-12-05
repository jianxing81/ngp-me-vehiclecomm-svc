package integration.containers;

import integration.containers.base.SingletonContainerNetwork;
import lombok.extern.slf4j.Slf4j;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

@Slf4j
public class SingletonPostgreSQLContainer {

  private static PostgreSQLContainer<?> POSTGRE_SQL_CONTAINER;

  private static final DockerImageName imageName =
      DockerImageName.parse("public.ecr.aws/docker/library/postgres:14.7-alpine")
          .asCompatibleSubstituteFor("postgres");

  static {
    POSTGRE_SQL_CONTAINER =
        new PostgreSQLContainer<>(imageName)
            .withNetwork(SingletonContainerNetwork.getInstance())
            .withNetworkAliases("postgres")
            .withDatabaseName("ngp_me_vehiclecomm")
            .withUsername("postgres")
            .withPassword("postgres")
            .withCopyFileToContainer(
                MountableFile.forClasspathResource("/postgres/01_init.sh"),
                "/docker-entrypoint-initdb.d/")
            .waitingFor(Wait.forListeningPort())
            .withLogConsumer(outputFrame -> log.info(outputFrame.getUtf8String()));

    POSTGRE_SQL_CONTAINER.start();
  }

  private SingletonPostgreSQLContainer() {
    // Prevent instantiation
  }

  public static PostgreSQLContainer<?> getInstance() {
    return POSTGRE_SQL_CONTAINER;
  }
}
