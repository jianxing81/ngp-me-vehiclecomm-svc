package com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.utils;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class JsonHelperTest {

  @InjectMocks JsonHelper jsonHelper;

  @Mock ObjectMapper jsonEncoder;

  @Test
  void shouldThrowExceptionInExceptionToJsonStringMethod() throws JsonProcessingException {
    when(jsonEncoder.writeValueAsString(any(Exception.class)))
        .thenThrow(JsonProcessingException.class);
    assertDoesNotThrow(() -> jsonHelper.exceptionToJsonString(new Exception()));
  }
}
