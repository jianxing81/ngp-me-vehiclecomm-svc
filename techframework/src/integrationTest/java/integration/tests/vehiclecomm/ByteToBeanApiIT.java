package integration.tests.vehiclecomm;

import com.cdg.pmg.ngp.me.vehiclecomm.techframework.web.providers.models.ByteToBeanRequest;
import integration.IntegrationTestBase;
import integration.utils.HexStrings;
import java.util.Map;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import retrofit2.Response;

@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Execution(ExecutionMode.CONCURRENT)
public class ByteToBeanApiIT extends IntegrationTestBase {

  @ParameterizedTest
  @MethodSource("requestData")
  void shouldSuccessfullyConvertJobAcceptEvent(
      String eventId, ByteToBeanRequest byteToBeanRequest) {
    try {
      Response<Map<String, Object>> response =
          vehicleCommControllerApi.convertByteToBean(eventId, byteToBeanRequest).execute();
      Assertions.assertNotNull(response);
      Map<String, Object> responseBody = response.body();
      Assertions.assertNotNull(responseBody);
      Assertions.assertFalse(responseBody.isEmpty());
    } catch (Exception e) {
      Assertions.fail(e.getMessage());
    }
  }

  private Stream<Arguments> requestData() {
    return Stream.of(
        Arguments.of(
            "151", new ByteToBeanRequest().byteString(HexStrings.ACKNOWLEDGE_CONVERT_STREET_HAIL)),
        Arguments.of(
            "153", new ByteToBeanRequest().byteString(HexStrings.ACKNOWLEDGE_JOB_CANCELLATION)),
        Arguments.of(
            "190", new ByteToBeanRequest().byteString(HexStrings.AUTO_ACCEPT_JOB_CONFIRM_ACK)),
        Arguments.of("134", new ByteToBeanRequest().byteString(HexStrings.BREAK)),
        Arguments.of("133", new ByteToBeanRequest().byteString(HexStrings.CROSSING_ZONE)),
        Arguments.of("171", new ByteToBeanRequest().byteString(HexStrings.DESTINATION_UPDATE)),
        Arguments.of(
            "208", new ByteToBeanRequest().byteString(HexStrings.DRIVER_PERFORMANCE_REQUEST)),
        Arguments.of(
            "194", new ByteToBeanRequest().byteString(HexStrings.FARE_CALCULATION_REQUEST)),
        Arguments.of("226", new ByteToBeanRequest().byteString(HexStrings.FORGOT_PASSWORD)),
        Arguments.of("143", new ByteToBeanRequest().byteString(HexStrings.IVD_HARDWARE_INFO)),
        Arguments.of("219", new ByteToBeanRequest().byteString(HexStrings.IVD_EVENT)),
        Arguments.of("154", new ByteToBeanRequest().byteString(HexStrings.JOB_ACCEPT)),
        Arguments.of("156", new ByteToBeanRequest().byteString(HexStrings.ARRIVAL)),
        Arguments.of("188", new ByteToBeanRequest().byteString(HexStrings.JOB_CONFIRM_ACK)),
        Arguments.of("152", new ByteToBeanRequest().byteString(HexStrings.JOB_MODIFICATION)),
        Arguments.of("141", new ByteToBeanRequest().byteString(HexStrings.JOB_NO_BLOCK_REQ)),
        Arguments.of("155", new ByteToBeanRequest().byteString(HexStrings.JOB_REJECT)),
        Arguments.of("130", new ByteToBeanRequest().byteString(HexStrings.LOG_ON)),
        Arguments.of("131", new ByteToBeanRequest().byteString(HexStrings.LOG_OUT)),
        Arguments.of("176", new ByteToBeanRequest().byteString(HexStrings.MDT_SYNC_REQ)),
        Arguments.of("251", new ByteToBeanRequest().byteString(HexStrings.MESSAGE_ACK)),
        Arguments.of("161", new ByteToBeanRequest().byteString(HexStrings.METER_OFF_DISPATCH)),
        Arguments.of(
            "140", new ByteToBeanRequest().byteString(HexStrings.METER_OFF_STREETHAIL_JOB)),
        Arguments.of("160", new ByteToBeanRequest().byteString(HexStrings.METER_ON_DISPATCH)),
        Arguments.of("139", new ByteToBeanRequest().byteString(HexStrings.METER_ON_STREETHAIL_JOB)),
        Arguments.of("184", new ByteToBeanRequest().byteString(HexStrings.NOTIFY_ON_CALL)),
        Arguments.of("157", new ByteToBeanRequest().byteString(HexStrings.NOTIFY_STATIC_GPS)),
        Arguments.of("129", new ByteToBeanRequest().byteString(HexStrings.POWER_UP)),
        Arguments.of("136", new ByteToBeanRequest().byteString(HexStrings.REGULAR_REPORT)),
        Arguments.of("212", new ByteToBeanRequest().byteString(HexStrings.REPORT_TOTAL_MILEAGE)),
        Arguments.of("221", new ByteToBeanRequest().byteString(HexStrings.REPORT_TRIP_INFO_NORMAL)),
        Arguments.of(
            "166", new ByteToBeanRequest().byteString(HexStrings.RESPOND_TO_SIMPLE_MESSAGE)),
        Arguments.of(
            "167", new ByteToBeanRequest().byteString(HexStrings.RESPOND_TO_STRUCTURE_MESSAGE)),
        Arguments.of("165", new ByteToBeanRequest().byteString(HexStrings.SEND_MESSAGE)),
        Arguments.of("135", new ByteToBeanRequest().byteString(HexStrings.UPDATE_BUSY)),
        Arguments.of("137", new ByteToBeanRequest().byteString(HexStrings.UPDATE_STC)),
        Arguments.of("213", new ByteToBeanRequest().byteString(HexStrings.UPDATE_STOP)),
        Arguments.of("187", new ByteToBeanRequest().byteString(HexStrings.VOICE_STREAM_MESSAGE)));
  }
}
