package integration.utils;

import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.JobDispatchDetailsRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.DeviceType;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.DriverAction;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.bookingservice.client.models.BookingProducts;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.bookingservice.client.models.BookingProductsResponse;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.paxpaymentservice.client.models.PaymentMethodResponse;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.paxpaymentservice.client.models.PaymentsMethodData;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class JobDispatchServiceUtility {
  private JobDispatchServiceUtility() {}

  public static JobDispatchDetailsRequest jobDispatchDriverResponse() {
    return JobDispatchDetailsRequest.builder()
        .driverId("1234543")
        .vehicleId("SH123464")
        .latitude(123D)
        .longitude(3233D)
        .eta("13")
        .driverAction(DriverAction.ACCEPT)
        .eventTime(LocalDateTime.now())
        .deviceType(DeviceType.MDT)
        .build();
  }

  public static PaymentMethodResponse paymentMethodResponse() {
    List<PaymentsMethodData> paymentMethodDatalist = new ArrayList<>();
    PaymentsMethodData paymentsMethodData = new PaymentsMethodData();
    PaymentMethodResponse paymentMethodResponse = new PaymentMethodResponse();
    paymentsMethodData.paymentMode(8);
    paymentsMethodData.code("NETS");
    paymentsMethodData.codeDesc("NETS");
    paymentsMethodData.entryMode(1);
    paymentMethodDatalist.add(paymentsMethodData);
    paymentsMethodData = new PaymentsMethodData();
    paymentsMethodData.paymentMode(7);
    paymentsMethodData.code("ONBOARD");
    paymentsMethodData.codeDesc("ONBOARD");
    paymentsMethodData.entryMode(2);
    paymentMethodDatalist.add(paymentsMethodData);
    return paymentMethodResponse.data(paymentMethodDatalist);
  }

  public static BookingProductsResponse bookingProductResponse() {
    BookingProductsResponse bookingProductsResponse = new BookingProductsResponse();
    List<BookingProducts> bookingProductsList;
    BookingProducts bookingProducts = new BookingProducts();
    bookingProducts.setProductId("FLAT-001");
    bookingProducts.setInVehicleDeviceCode(new BigDecimal(227));
    bookingProductsList = List.of(bookingProducts);
    bookingProductsResponse.setData(bookingProductsList);
    return bookingProductsResponse;
  }
}
