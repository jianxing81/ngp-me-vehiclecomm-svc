package com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.helper;

import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.AdditionalChargeItem;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.utils.DataConversion;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.utils.IVDFieldTag;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.utils.IVDListItem;
import java.util.*;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

@Slf4j
public class AdditionalChargesHelper {

  private AdditionalChargesHelper() {}

  /**
   * Convert list of AdditionalChargeFeeItem to list of IVDListItem, before send data to
   * "ngp.me.esbcomm.ivd_response"。 The IVD field type refer to IVDFieldTag.ADDITIONAL_CHARGES and
   * IVDFieldTag.CHARGE_XXX (from 0x0E00 to 0x0E06)
   *
   * @param additionalChargeItemList list of AdditionalChargeFeeItem
   * @return List of IVDListItem with Optional
   */
  public static Optional<List<IVDListItem>> convertToIvdAdditionalCharges(
      List<AdditionalChargeItem> additionalChargeItemList) {

    try {
      log.info(
          "Start convertToIvdAdditionalCharges, additionalChargeItemList={}.",
          additionalChargeItemList);

      if (CollectionUtils.isEmpty(additionalChargeItemList)) {
        return Optional.empty();
      } else {

        List<IVDListItem> additionalCharges = new ArrayList<>();

        for (AdditionalChargeItem additionalChargeItem : additionalChargeItemList) {
          IVDListItem additionalChargeFeeIvdItem = new IVDListItem();

          // convert chargeId(Integer) to IVDFieldTag.CHARGE_ID(0x0E01,INT32)
          Optional.ofNullable(additionalChargeItem.getChargeId())
              .ifPresent(
                  chargeId -> additionalChargeFeeIvdItem.putInt32(IVDFieldTag.CHARGE_ID, chargeId));

          // convert chargeId(String) to IVDFieldTag.CHARGE_TYPE(0x0E02,TEXT)
          Optional.ofNullable(additionalChargeItem.getChargeType())
              .ifPresent(
                  chargeType ->
                      additionalChargeFeeIvdItem.putText(IVDFieldTag.CHARGE_TYPE, chargeType));

          // convert chargeAmt(Double) to IVDFieldTag.CHARGE_AMT(0x0E03,MONEY)
          Optional.ofNullable(additionalChargeItem.getChargeAmt())
              .ifPresent(
                  chargeAmt ->
                      additionalChargeFeeIvdItem.putMoney(
                          IVDFieldTag.CHARGE_AMT, DataConversion.toIVDMoneyType(chargeAmt)));

          // convert chargeThreshold(Double) to IVDFieldTag.CHARGE_THRESHOLD(0x0E04,MONEY)
          Optional.ofNullable(additionalChargeItem.getChargeThreshold())
              .ifPresent(
                  chargeThreshold ->
                      additionalChargeFeeIvdItem.putMoney(
                          IVDFieldTag.CHARGE_THRESHOLD,
                          DataConversion.toIVDMoneyType(chargeThreshold)));

          // convert chargeUpperLimit(Double) to IVDFieldTag.CHARGE_UPPER_LIMIT(0x0E05,MONEY)
          Optional.ofNullable(additionalChargeItem.getChargeUpperLimit())
              .ifPresent(
                  chargeUpperLimit ->
                      additionalChargeFeeIvdItem.putMoney(
                          IVDFieldTag.CHARGE_UPPER_LIMIT,
                          DataConversion.toIVDMoneyType(chargeUpperLimit)));

          // convert chargeUpperLimit(Double) to IVDFieldTag.CHARGE_UPPER_LIMIT(0x0E06,MONEY)
          Optional.ofNullable(additionalChargeItem.getChargeLowerLimit())
              .ifPresent(
                  chargeLowerLimit ->
                      additionalChargeFeeIvdItem.putMoney(
                          IVDFieldTag.CHARGE_LOWER_LIMIT,
                          DataConversion.toIVDMoneyType(chargeLowerLimit)));

          additionalCharges.add(additionalChargeFeeIvdItem);
        }

        log.info("End convertToIvdAdditionalCharges, additionalCharges={}.", additionalCharges);
        return Optional.of(additionalCharges);
      }
    } catch (Exception e) {

      log.error("Exception in handling convertToIvdAdditionalCharges.", e);
      return Optional.empty();
    }
  }

  /**
   * Convert list of IVDListItem to list of AdditionalChargeItem, this method will process when
   * receive message from "ngp.me.vehiclecomm.ivd_job_event" and before send message to
   * "ngp.me.trip.upload_trip.r2"
   *
   * @param additionalChargeIvdList list of IVDListItem that contains additional charges data from
   *     ESB(message from topic "ngp.me.vehiclecomm.ivd_job_event")
   * @return List of AdditionalChargeItem(only contain chargeId,chargeType,chargeAmt)
   */
  public static Optional<List<AdditionalChargeItem>> getAdditionalChargesFromIVDListItemArray(
      IVDListItem[] additionalChargeIvdList) {

    try {
      if (null == additionalChargeIvdList) {

        log.info("Process getAdditionalChargesFromIVDListItemArray stop, ivdMessage is null.");
        return Optional.empty();

      } else {
        if (0 == additionalChargeIvdList.length) {

          return Optional.empty();

        } else {

          List<AdditionalChargeItem> additionalChargeItemList =
              Arrays.stream(additionalChargeIvdList)
                  .map(
                      additionalChargeIvdListItem -> {
                        AdditionalChargeItem additionalChargeItem = new AdditionalChargeItem();
                        additionalChargeItem.setChargeId(
                            additionalChargeIvdListItem.getInt32(IVDFieldTag.CHARGE_ID));
                        additionalChargeItem.setChargeType(
                            additionalChargeIvdListItem.getText(IVDFieldTag.CHARGE_TYPE));
                        additionalChargeItem.setChargeAmt(
                            DataConversion.toBackendMoneyType(
                                additionalChargeIvdListItem.getMoney(IVDFieldTag.CHARGE_AMT)));
                        return additionalChargeItem;
                      })
                  .toList();

          /*
          Distinct by chargeId and chargeType ,because from current requirement,
          one trip will only have one additional charge fee for each chargeType.
           */
          additionalChargeItemList =
              additionalChargeItemList.stream()
                  .collect(
                      Collectors.toMap(
                          additionalChargeItem ->
                              Map.entry(
                                  additionalChargeItem.getChargeId(),
                                  additionalChargeItem.getChargeType()),
                          additionalChargeItem -> additionalChargeItem,
                          (oldOne, newOne) -> oldOne))
                  .values()
                  .stream()
                  .toList();

          log.info(
              "Process getAdditionalChargesFromIVDListItemArray result, additionalChargeItemList={}.",
              additionalChargeItemList);

          return Optional.of(additionalChargeItemList);
        }
      }
    } catch (Exception e) {

      log.error("Exception in handling getAdditionalChargesFromIVDListItemArray.", e);
      return Optional.empty();
    }
  }
}
