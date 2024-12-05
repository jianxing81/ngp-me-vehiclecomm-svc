package com.cdg.pmg.ngp.me.vehiclecomm.application.inbound.impl;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.cdg.pmg.ngp.me.vehiclecomm.application.outbound.ports.internal.BeanToByteConverter;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.exceptions.ApplicationException;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.exceptions.DomainException;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.IvdPingMessageRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class VehicleCommApplicationServiceImplTest {

  @InjectMocks private VehicleCommApplicationServiceImpl vehicleCommApplicationService;

  @Mock private BeanToByteConverter beanToByteConverter;

  @Test
  void testSendPingMessageResponseInvalid() {
    IvdPingMessageRequest ivdPingMessageRequest =
        IvdPingMessageRequest.builder()
            .ivdNo(1027)
            .seqNo(10)
            .refNo(12)
            .ipAddress("10.120.140.23")
            .build();
    Mockito.when(
            beanToByteConverter.convertToBytePingMessage(
                ArgumentMatchers.anyInt(), ArgumentMatchers.any()))
        .thenThrow(DomainException.class);
    assertThrows(
        ApplicationException.class,
        () -> vehicleCommApplicationService.sendPingMessage(35, ivdPingMessageRequest));
  }
}
