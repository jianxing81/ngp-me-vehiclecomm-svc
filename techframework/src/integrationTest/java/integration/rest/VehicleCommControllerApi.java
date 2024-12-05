package integration.rest;

import com.cdg.pmg.ngp.me.vehiclecomm.techframework.web.providers.models.ByteToBeanRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.web.providers.models.EmergencyActionEnum;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.web.providers.models.EmergencyCloseRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.web.providers.models.IvdPingMessage;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.web.providers.models.NotificationRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.web.providers.models.VehicleTrackEnum;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.web.providers.models.VehicleTrackRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.web.providers.models.VoiceEventRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.web.providers.models.VoiceStreamEnum;
import java.util.Map;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface VehicleCommControllerApi {

  @POST("/v1.0/vehcomm/drvapp/notify/{userId}")
  Call<Void> sendNotification(
      @Path("userId") String userId, @Body NotificationRequest notificationRequest);

  @POST("/v1.0/vehcomm/vehicle/track/{event}")
  Call<Void> sendVehicleTrackingByEvent(
      @Path("event") VehicleTrackEnum event, @Body VehicleTrackRequest vehicleTrackRequest);

  @POST("/v1.0/vehcomm/emergency/{action}")
  Call<Void> sendEmergencyClose(
      @Path("action") EmergencyActionEnum action,
      @Body EmergencyCloseRequest emergencyCloseRequest);

  @POST("/v1.0/vehcomm/stream/voice/{event}")
  Call<Void> sendVoiceStreaming(
      @Path("event") VoiceStreamEnum event, @Body VoiceEventRequest voiceEventRequest);

  @POST("/v1.0/vehcomm/ivd/ping")
  Call<Void> sendPingMessage(@Body IvdPingMessage ivdPingMessage);

  @POST("/v1.0/vehcomm/byte-to-bean/{eventId}")
  Call<Map<String, Object>> convertByteToBean(
      @Path("eventId") String eventId, @Body ByteToBeanRequest byteToBeanRequest);
}
