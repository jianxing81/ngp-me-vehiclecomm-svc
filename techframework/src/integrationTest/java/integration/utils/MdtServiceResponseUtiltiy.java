package integration.utils;

import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.VehicleDetailsResponse;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.CabchargePolicy;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.ExtraStopsInfo;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.MultiStop;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.fare.client.models.IvdFareResponse;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.fare.client.models.IvdFareResponseData;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.fare.client.models.TariffList;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.jobdispatchservice.client.models.JobOfferResponse;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.jobdispatchservice.client.models.JobOffersResponse;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.mdt.client.models.IVDPingResponse;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.mdt.client.models.IvdZoneInfoResponse;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.mdt.client.models.JobNumberBlockResponse;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.mdt.client.models.MdtChangePinResponse;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.mdt.client.models.MdtChangePinResponseIvdInfo;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.mdt.client.models.MdtForgotPasswordResponse;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.mdt.client.models.VehicleByIvdResponse;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.mdt.client.models.VerifyOtpForgotPasswordResponse;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.mdt.client.models.VerifyOtpForgotPasswordResponseIvdInfo;
import integration.constants.IntegrationTestConstants;
import integration.helpers.general.DataHelper;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class MdtServiceResponseUtiltiy {

  private MdtServiceResponseUtiltiy() {}

  public static VehicleByIvdResponse vehicleDetailsResponse() {
    VehicleByIvdResponse vehicleByIvdResponse = new VehicleByIvdResponse();
    vehicleByIvdResponse.setVehicleId(IntegrationTestConstants.JOB_VEHICLE_NO);
    vehicleByIvdResponse.setIpAddress(IntegrationTestConstants.TEST_IP_ADDRESS);
    vehicleByIvdResponse.setDriverId(IntegrationTestConstants.JOB_DRIVER_ID);
    return vehicleByIvdResponse;
  }

  public static IvdZoneInfoResponse zoneInfoDetailsResponse() {
    IvdZoneInfoResponse zoneInfoResponse = new IvdZoneInfoResponse();
    zoneInfoResponse.setZoneId(IntegrationTestConstants.ZONE_ID);
    zoneInfoResponse.setZoneIvdDesc(IntegrationTestConstants.ZONE_ID_DESC);
    zoneInfoResponse.setRoofTopIndex(99);
    return zoneInfoResponse;
  }

  public static IVDPingResponse ivdPingResponse() {
    IVDPingResponse ivdPingResponseData = new IVDPingResponse();
    ivdPingResponseData.setIpAddress(IntegrationTestConstants.TEST_IP_ADDRESS);
    return ivdPingResponseData;
  }

  public static VehicleByIvdResponse vehicleDetails() {
    VehicleByIvdResponse vehicleByIvdResponse = new VehicleByIvdResponse();
    vehicleByIvdResponse.setVehicleId("TST8020A");
    vehicleByIvdResponse.setDriverId(IntegrationTestConstants.JOB_DRIVER_ID);
    return vehicleByIvdResponse;
  }

  public static VehicleDetailsResponse vehicleDetailsInvalidResponse() {
    return VehicleDetailsResponse.builder().vehicleId(null).build();
  }

  public static VerifyOtpForgotPasswordResponse verifyOtpResponse() {
    VerifyOtpForgotPasswordResponse verifyOtpForgotPasswordResponse =
        new VerifyOtpForgotPasswordResponse();
    VerifyOtpForgotPasswordResponseIvdInfo ivdInfo = new VerifyOtpForgotPasswordResponseIvdInfo();
    ivdInfo.setImsi("1234");
    ivdInfo.setIpAddress("ipv4");
    ivdInfo.setTlvFlag(true);
    verifyOtpForgotPasswordResponse.setPassApproval("1234");
    verifyOtpForgotPasswordResponse.ivdInfo(ivdInfo);
    return verifyOtpForgotPasswordResponse;
  }

  public static MdtChangePinResponse changePinResponse() {
    MdtChangePinResponseIvdInfo ivdInfo = new MdtChangePinResponseIvdInfo();
    ivdInfo.setIpAddress("ipv4");
    ivdInfo.setTlvFlag(true);
    ivdInfo.setImsi("1234");
    MdtChangePinResponse mdtChangePinResponse = new MdtChangePinResponse();
    mdtChangePinResponse.setPassApproval("1234");
    mdtChangePinResponse.setIvdInfo(ivdInfo);
    return mdtChangePinResponse;
  }

  public static JobOffersResponse jobOffersResponse() {
    JobOfferResponse jobOffersResponse = new JobOfferResponse();
    JobOffersResponse returnData = new JobOffersResponse();
    jobOffersResponse.setEventDate(LocalDateTime.now());
    jobOffersResponse.jobNo("9999999");
    jobOffersResponse.ivdNo("12375");
    jobOffersResponse.ipAddress(IntegrationTestConstants.TEST_IP_ADDRESS);
    jobOffersResponse.driverId("5008021");
    jobOffersResponse.vehicleId("TST8021A");
    jobOffersResponse.paymentMethod("7");
    jobOffersResponse.jobStatus("CONFIRMED");
    returnData.setData(List.of(jobOffersResponse));
    return returnData;
  }

  public static List<ExtraStopsInfo> constructExtraStop() {
    ExtraStopsInfo extraStopsInfo = new ExtraStopsInfo();
    extraStopsInfo.setExtraStopName(IntegrationTestConstants.STRING);
    extraStopsInfo.setExtraStopQty(0);
    extraStopsInfo.setExtraStopDetail(IntegrationTestConstants.STRING);
    return List.of(extraStopsInfo);
  }

  public static MultiStop constructMultistop() {
    MultiStop multiStop = new MultiStop();
    multiStop.setIntermediateAddr(IntegrationTestConstants.STRING);
    multiStop.setIntermediateLat(1.456);
    multiStop.setIntermediateLng(103.456);
    return multiStop;
  }

  public static CabchargePolicy constructCabchargePolicy() {
    CabchargePolicy cabchargePolicy = new CabchargePolicy();
    cabchargePolicy.setAmountCap(0.0);
    com.cdg.pmg.ngp.me.vehiclecomm.domain.models.PolicyDetails policyDetail =
        new com.cdg.pmg.ngp.me.vehiclecomm.domain.models.PolicyDetails();
    policyDetail.setPolicyPickupLat(1.67);
    policyDetail.setPolicyPickupLng(103.44);
    policyDetail.setPolicyDestLat(1.45);
    policyDetail.setPolicyDestLng(103.12);
    policyDetail.setPolicyRadius(0.0);
    cabchargePolicy.setPolicyDetails(List.of(policyDetail));
    return cabchargePolicy;
  }

  public static IvdFareResponse fareTariffResponse() {
    IvdFareResponse ivdFareResponse = new IvdFareResponse();
    IvdFareResponseData dataDTO = new IvdFareResponseData();
    dataDTO.setCollectFare(true);
    dataDTO.setBookingFee(BigDecimal.valueOf(11.5));
    dataDTO.setLevy(BigDecimal.valueOf(1));
    dataDTO.setGstInclusive(true);
    dataDTO.setGstAmount(BigDecimal.valueOf(1.2));
    TariffList tariffList = new TariffList();
    tariffList.setTariffTypeCode("PDT");
    tariffList.setFare(BigDecimal.valueOf(65));
    tariffList.setLevy(BigDecimal.ZERO);
    tariffList.setTariffUnit(1);
    tariffList.setDiscountedCurrency("SGD");
    dataDTO.addTariffListItem(tariffList);
    ivdFareResponse.setData(dataDTO);
    return ivdFareResponse;
  }

  public static MdtForgotPasswordResponse forgotPasswordEventResponse() {
    MdtForgotPasswordResponse mdtForgotPasswordResponse = new MdtForgotPasswordResponse();
    MdtChangePinResponseIvdInfo ivdInfo = new MdtChangePinResponseIvdInfo();
    ivdInfo.setImsi("1234");
    ivdInfo.setTlvFlag(true);
    ivdInfo.setIpAddress("ipv4");
    mdtForgotPasswordResponse.setIvdInfo(ivdInfo);
    mdtForgotPasswordResponse.setPassApproval("1");
    return mdtForgotPasswordResponse;
  }

  public static JobNumberBlockResponse jobNoBlockAPIResponse() {
    JobNumberBlockResponse jobNoBlockResponse = new JobNumberBlockResponse();
    jobNoBlockResponse.setJobNoBlockStart(DataHelper.generateRandomIntegers(7777).toString());
    jobNoBlockResponse.setVehicleId(DataHelper.generateRandomString(7));
    jobNoBlockResponse.setJobNoBlockEnd(DataHelper.generateRandomIntegers(7777).toString());
    jobNoBlockResponse.setIsNew(true);
    return jobNoBlockResponse;
  }
}
