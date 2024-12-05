package com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.configs;

import com.cdg.pmg.ngp.me.vehiclecomm.domain.exceptions.DomainException;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.utils.Helper;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.listener.models.ParentRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.confluent.kafka.schemaregistry.ParsedSchema;
import io.confluent.kafka.schemaregistry.client.rest.entities.SchemaReference;
import io.confluent.kafka.schemaregistry.json.JsonSchema;
import io.confluent.kafka.schemaregistry.json.JsonSchemaProvider;
import io.confluent.kafka.schemaregistry.json.SpecificationVersion;
import io.confluent.kafka.schemaregistry.json.jackson.Jackson;
import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.serializers.json.KafkaJsonSchemaDeserializer;
import io.confluent.kafka.serializers.json.KafkaJsonSchemaDeserializerConfig;
import io.confluent.kafka.serializers.json.KafkaJsonSchemaSerializer;
import io.confluent.kafka.serializers.json.KafkaJsonSchemaSerializerConfig;
import io.confluent.kafka.serializers.subject.TopicRecordNameStrategy;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.everit.json.schema.FormatValidator;
import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.converter.MessagingMessageConverter;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DomainEventByteArrayJsonSchemaMessageConverter extends MessagingMessageConverter {
  private final OverridableMapperKafkaJsonSchemaDeserializer
      overridableMapperKafkaJsonSchemaDeserializer;

  private final OverridableMapperKafkaJsonSchemaSerializer
      overridableMapperKafkaJsonSchemaSerializer;

  private SchemaRegistryConfig schemaRegistryConfig;

  /** Injects the custom {@link ObjectMapper} bean from the application context. */
  public DomainEventByteArrayJsonSchemaMessageConverter(
      KafkaObjectMapper objectMapper, SchemaRegistryConfig schemaRegistryConfig) {
    this.schemaRegistryConfig = schemaRegistryConfig;

    overridableMapperKafkaJsonSchemaDeserializer =
        new OverridableMapperKafkaJsonSchemaDeserializer();

    overridableMapperKafkaJsonSchemaSerializer = new OverridableMapperKafkaJsonSchemaSerializer();

    overridableMapperKafkaJsonSchemaDeserializer.configure(
        Map.of(
            KafkaJsonSchemaDeserializerConfig.FAIL_INVALID_SCHEMA,
            true,
            AbstractKafkaSchemaSerDeConfig.VALUE_SUBJECT_NAME_STRATEGY,
            TopicRecordNameStrategy.class,
            KafkaJsonSchemaDeserializerConfig.JSON_VALUE_TYPE,
            ParentRequest.class,
            AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG,
            this.schemaRegistryConfig.getUrl(),
            AbstractKafkaSchemaSerDeConfig.BASIC_AUTH_CREDENTIALS_SOURCE,
            this.schemaRegistryConfig.getCredentialsSource(),
            AbstractKafkaSchemaSerDeConfig.USER_INFO_CONFIG,
            this.schemaRegistryConfig.getKeySecret()),
        false);

    overridableMapperKafkaJsonSchemaDeserializer.setObjectMapper(objectMapper);

    overridableMapperKafkaJsonSchemaSerializer.configure(
        Map.of(
            AbstractKafkaSchemaSerDeConfig.AUTO_REGISTER_SCHEMAS,
            false,
            AbstractKafkaSchemaSerDeConfig.USE_LATEST_VERSION,
            true,
            AbstractKafkaSchemaSerDeConfig.LATEST_COMPATIBILITY_STRICT,
            false,
            KafkaJsonSchemaSerializerConfig.ONEOF_FOR_NULLABLES,
            false,
            KafkaJsonSchemaSerializerConfig.FAIL_INVALID_SCHEMA,
            true,
            AbstractKafkaSchemaSerDeConfig.VALUE_SUBJECT_NAME_STRATEGY,
            TopicRecordNameStrategy.class,
            AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG,
            this.schemaRegistryConfig.getUrl(),
            AbstractKafkaSchemaSerDeConfig.BASIC_AUTH_CREDENTIALS_SOURCE,
            this.schemaRegistryConfig.getCredentialsSource(),
            AbstractKafkaSchemaSerDeConfig.USER_INFO_CONFIG,
            this.schemaRegistryConfig.getKeySecret()),
        false);

    overridableMapperKafkaJsonSchemaSerializer.setObjectMapper(objectMapper);
  }

  /**
   * Delegates the conversion to the custom {@link OverridableMapperKafkaJsonSchemaDeserializer}
   * instance for schema validation purpose.
   */
  @Override
  public Object extractAndConvertValue(ConsumerRecord<?, ?> consumerRecord, Type type) {
    try {
      return overridableMapperKafkaJsonSchemaDeserializer.deserialize(
          consumerRecord.topic(), (byte[]) consumerRecord.value());
    } catch (Exception exception) {
      log.error(
          "Error occurred while deserializing record from topic: {} with offset: {} message : {}",
          consumerRecord.topic(),
          consumerRecord.offset(),
          exception.getMessage(),
          exception);
      log.error("Failed to deserialize message from topic:{} ", consumerRecord.topic(), exception);
      // Log the raw value for debugging
      log.error("Raw value: " + Arrays.toString((byte[]) consumerRecord.value()));
    }
    return null;
  }

  @Override
  protected Object convertPayload(Message<?> message) {
    return overridableMapperKafkaJsonSchemaSerializer.serialize(
        (String) message.getHeaders().get(KafkaHeaders.TOPIC),
        (ParentRequest) message.getPayload());
  }

  /**
   * Extends the {@link KafkaJsonSchemaDeserializer} so that it can understand how to deserialize a
   * {@link ParentRequest} message.
   */
  public static class OverridableMapperKafkaJsonSchemaDeserializer
      extends KafkaJsonSchemaDeserializer<ParentRequest> {

    /** Exposes method allowing to override the built-in {@link ObjectMapper}. */
    private void setObjectMapper(ObjectMapper objectMapper) {
      this.objectMapper = objectMapper;
    }

    /**
     * Overrides the configure method to inject the custom {@link LocalDateTimeJsonSchemaProvider}.
     */
    @Override
    protected void configure(KafkaJsonSchemaDeserializerConfig config, Class<ParentRequest> type) {
      // Register the custom the LocalDateTime JSON schema provider
      configureClientProperties(config, new LocalDateTimeJsonSchemaProvider());

      // Other configuration below should be the same as the super class
      this.type = type;
      boolean failUnknownProperties =
          config.getBoolean(KafkaJsonSchemaDeserializerConfig.FAIL_UNKNOWN_PROPERTIES);
      this.objectMapper.configure(
          DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, failUnknownProperties);
      this.validate = config.getBoolean(KafkaJsonSchemaDeserializerConfig.FAIL_INVALID_SCHEMA);
      this.typeProperty = config.getString(KafkaJsonSchemaDeserializerConfig.TYPE_PROPERTY);
    }
  }

  public static class OverridableMapperKafkaJsonSchemaSerializer
      extends KafkaJsonSchemaSerializer<ParentRequest> {

    /** Exposes method allowing to override the built-in {@link ObjectMapper}. */
    private void setObjectMapper(ObjectMapper objectMapper) {
      this.objectMapper = objectMapper;
    }

    /**
     * Overrides the configure method to inject the custom {@link LocalDateTimeJsonSchemaProvider}.
     */
    @Override
    protected void configure(KafkaJsonSchemaSerializerConfig config) {
      // Register the custom the LocalDateTime JSON schema provider
      configureClientProperties(config, new LocalDateTimeJsonSchemaProvider());

      // Other configuration below should be the same as the super class
      this.normalizeSchema = config.normalizeSchema();
      this.autoRegisterSchema = config.autoRegisterSchema();
      this.useSchemaId = config.useSchemaId();
      this.idCompatStrict = config.getIdCompatibilityStrict();
      this.useLatestVersion = config.useLatestVersion();
      this.latestCompatStrict = config.getLatestCompatibilityStrict();
      boolean prettyPrint = config.getBoolean(KafkaJsonSchemaSerializerConfig.JSON_INDENT_OUTPUT);
      this.objectMapper.configure(SerializationFeature.INDENT_OUTPUT, prettyPrint);
      boolean writeDatesAsIso8601 =
          config.getBoolean(KafkaJsonSchemaSerializerConfig.WRITE_DATES_AS_ISO8601);
      this.objectMapper.configure(
          SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, !writeDatesAsIso8601);
      this.specVersion =
          SpecificationVersion.get(
              config.getString(KafkaJsonSchemaSerializerConfig.SCHEMA_SPEC_VERSION));
      this.oneofForNullables =
          config.getBoolean(KafkaJsonSchemaSerializerConfig.ONEOF_FOR_NULLABLES);
      this.failUnknownProperties =
          config.getBoolean(KafkaJsonSchemaDeserializerConfig.FAIL_UNKNOWN_PROPERTIES);
      this.validate = config.getBoolean(KafkaJsonSchemaSerializerConfig.FAIL_INVALID_SCHEMA);
    }
  }

  /** The custom JSON schema provider supporting {@link java.time.LocalDateTime} values. */
  private static class LocalDateTimeJsonSchemaProvider extends JsonSchemaProvider {

    @Override
    public ParsedSchema parseSchemaOrElseThrow(
        io.confluent.kafka.schemaregistry.client.rest.entities.Schema schema, boolean isNew) {
      return new LocalDateTimeJsonSchema(
          schema.getSchema(),
          schema.getReferences(),
          resolveReferences(schema.getReferences()),
          null);
    }
  }

  /** The custom JSON schema supporting {@link java.time.LocalDateTime} string validation. */
  private static class LocalDateTimeJsonSchema extends JsonSchema {
    private static final ObjectMapper objectMapper = Jackson.newObjectMapper();

    public LocalDateTimeJsonSchema(
        String schemaString,
        List<SchemaReference> references,
        Map<String, String> resolvedReferences,
        Integer version) {
      super(schemaString, references, resolvedReferences, version);
    }

    @Override
    public Schema rawSchema() {
      try {
        return SchemaLoader.builder()
            .useDefaults(true)
            .enableOverrideOfBuiltInFormatValidators() // Enable the override to make the
            // customization work
            .draftV7Support()
            .addFormatValidator(
                new LocalDateTimeFormatValidator()) // Register the LocalDateTime validation
            .schemaJson(objectMapper.treeToValue(toJsonNode(), JSONObject.class))
            .build()
            .load()
            .build();
      } catch (JsonProcessingException e) {
        throw new DomainException(e);
      }
    }
  }

  /**
   * The custom validator for date-time format accepting the {@link java.time.LocalDateTime} string
   * representation. It is intended to replace the original {@link
   * org.everit.json.schema.internal.DateTimeFormatValidator} which supports date time with time
   * zone information only.
   */
  private static class LocalDateTimeFormatValidator implements FormatValidator {

    @Override
    public Optional<String> validate(String subject) {
      try {
        LocalDateTime localeDateTime = Helper.parseLocaleDate(subject);
        log.trace("Validate the local date time {}", localeDateTime);
        return Optional.empty();
      } catch (DateTimeParseException e) {
        return Optional.of(String.format("[%s] is not a valid %s.", subject, formatName()));
      }
    }

    @Override
    public String formatName() {
      return "date-time";
    }
  }
}
