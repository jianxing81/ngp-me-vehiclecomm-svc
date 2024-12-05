package integration.rest;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface RetryControllerApi {

  @POST("/v1.0/vehcomm/retry/{eventName}")
  Call<Void> processRetry(@Path("eventName") String eventName);
}
