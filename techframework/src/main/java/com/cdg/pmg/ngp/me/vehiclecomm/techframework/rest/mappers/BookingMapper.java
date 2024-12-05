package com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.mappers;

import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.BookingProductData;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.bookingservice.client.models.BookingProducts;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BookingMapper {

  List<BookingProductData> bookingResponseProductToBookingProduct(
      List<BookingProducts> bookingProducts);

  BookingProductData bookingProductsToBookingProductData(BookingProducts bookingProducts);
}
