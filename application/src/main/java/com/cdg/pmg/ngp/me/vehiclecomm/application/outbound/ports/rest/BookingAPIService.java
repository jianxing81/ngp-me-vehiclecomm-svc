package com.cdg.pmg.ngp.me.vehiclecomm.application.outbound.ports.rest;

import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.BookingProductData;
import java.util.List;
import java.util.Optional;

/* Internal class for wrapper API calls to Booking Service*/
public interface BookingAPIService {
  Optional<List<BookingProductData>> getBookingProductList();

  BookingProductData getBookingProductData(String productId);

  /**
   * Get booking productId using inVehicleDeviceCode
   *
   * @param inVehicleDeviceCode inVehicleDeviceCode
   * @return BookingProductData
   */
  BookingProductData getBookingProductDataForInVehicleDeviceCode(String inVehicleDeviceCode);
}
