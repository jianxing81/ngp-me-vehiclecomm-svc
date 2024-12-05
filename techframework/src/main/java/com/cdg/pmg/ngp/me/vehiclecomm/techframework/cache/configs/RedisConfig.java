package com.cdg.pmg.ngp.me.vehiclecomm.techframework.cache.configs;

import com.cdg.pmg.ngp.me.vehiclecomm.application.constants.VehicleCommAppConstant;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.configs.properties.RefreshableProperties;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

@Configuration
@EnableCaching
@RequiredArgsConstructor
public class RedisConfig {

  private final RefreshableProperties refreshableProperties;

  @Bean
  RedisConfiguration redisConfiguration(RedisServerProperties redisServerProperties) {
    RedisConfiguration configuration;
    var clusterProperties = redisServerProperties.getCluster();
    if (Optional.ofNullable(clusterProperties).isPresent()
        && !CollectionUtils.isEmpty(clusterProperties.getNodes())) {
      configuration = new RedisClusterConfiguration(clusterProperties.getNodes());
    } else {
      configuration =
          new RedisStandaloneConfiguration(
              redisServerProperties.getHost(), redisServerProperties.getPort());
    }
    RedisConfiguration.WithAuthentication redisConfigWithAuth =
        (RedisConfiguration.WithAuthentication) configuration;
    String redisUsr = redisServerProperties.getUsername();
    if (StringUtils.hasText(redisUsr)) {
      redisConfigWithAuth.setUsername(redisUsr);
    }
    String redisPwd = redisServerProperties.getPassword();
    if (StringUtils.hasText(redisPwd)) {
      redisConfigWithAuth.setPassword(redisPwd);
    }
    return configuration;
  }

  @Bean
  LettuceClientConfiguration lettuceClientConfiguration(
      RedisServerProperties redisServerProperties) {
    LettuceClientConfiguration.LettuceClientConfigurationBuilder lettuceClientConfigurationBuilder =
        LettuceClientConfiguration.builder();
    if (Boolean.TRUE.equals(redisServerProperties.isSsl())) {
      lettuceClientConfigurationBuilder.useSsl();
    }
    return lettuceClientConfigurationBuilder.build();
  }

  @Bean
  LettuceConnectionFactory lettuceConnectionFactory(
      RedisConfiguration redisConfiguration,
      LettuceClientConfiguration lettuceClientConfiguration) {
    var factory = new LettuceConnectionFactory(redisConfiguration, lettuceClientConfiguration);
    factory.afterPropertiesSet();
    return factory;
  }

  @Bean
  StringRedisTemplate redisTemplate(LettuceConnectionFactory factory) {
    final StringRedisTemplate redisTemplate = new StringRedisTemplate();
    redisTemplate.setKeySerializer(new StringRedisSerializer());
    redisTemplate.setHashKeySerializer(new GenericToStringSerializer<>(String.class));
    redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
    redisTemplate.setConnectionFactory(factory);
    return redisTemplate;
  }

  @Bean
  public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
    Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();

    // Customize expiration time for each cache name
    redisCacheConfigurationWithExpiration(cacheConfigurations);
    // Add more caches with their own expiration times as needed

    return RedisCacheManager.builder(redisConnectionFactory)
        .withInitialCacheConfigurations(cacheConfigurations)
        .build();
  }

  private void redisCacheConfigurationWithExpiration(
      Map<String, RedisCacheConfiguration> cacheConfigurations) {
    cacheConfigurations.put(
        VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME,
        RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(refreshableProperties.getStoreForwardLifeSpan())));
    cacheConfigurations.put(
        VehicleCommAppConstant.REDUNDANT_MSG_CACHE,
        RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(refreshableProperties.getRedundantMessageLifeSpan())));
    cacheConfigurations.put(
        VehicleCommAppConstant.VEHICLE_COMM_PAYMENT_METHOD,
        RedisCacheConfiguration.defaultCacheConfig());
  }
}
