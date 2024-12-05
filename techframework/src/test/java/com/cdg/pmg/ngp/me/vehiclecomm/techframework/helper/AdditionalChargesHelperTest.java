package com.cdg.pmg.ngp.me.vehiclecomm.techframework.helper;

import static org.junit.jupiter.api.Assertions.*;

import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.AdditionalChargeItem;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.helper.AdditionalChargesHelper;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.utils.DataConversion;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.utils.IVDFieldTag;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.utils.IVDListItem;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AdditionalChargesHelperTest {

  @Test
  void testGetAdditionalChargesFromIVDListItemArray() {

    int chargeId = 1;
    String chargeType = "DRIVER_FEE";
    double chargeAmt = 0.50;
    IVDListItem additionalChargeIvdData = new IVDListItem();
    additionalChargeIvdData.putInt32(IVDFieldTag.CHARGE_ID, chargeId);
    additionalChargeIvdData.putText(IVDFieldTag.CHARGE_TYPE, chargeType);
    additionalChargeIvdData.putMoney(
        IVDFieldTag.CHARGE_AMT, DataConversion.toIVDMoneyType(chargeAmt));

    IVDListItem[] additionalChargeIvdList = new IVDListItem[] {additionalChargeIvdData};

    Optional<List<AdditionalChargeItem>> additionalChargesFromIVDListItemArray =
        AdditionalChargesHelper.getAdditionalChargesFromIVDListItemArray(additionalChargeIvdList);

    assertTrue(additionalChargesFromIVDListItemArray.isPresent());

    List<AdditionalChargeItem> actualDataList = additionalChargesFromIVDListItemArray.get();
    AdditionalChargeItem actualData = actualDataList.get(0);

    assertEquals(chargeId, actualData.getChargeId());
    assertEquals(chargeType, actualData.getChargeType());
    assertEquals(chargeAmt, actualData.getChargeAmt(), 0.01);
  }

  @Test
  void testGetAdditionalChargesFromIVDListItemArray_whenIVDListItemListEmpty() {

    Optional<List<AdditionalChargeItem>> additionalChargesFromIVDListItemArray =
        AdditionalChargesHelper.getAdditionalChargesFromIVDListItemArray(new IVDListItem[0]);

    assertTrue(additionalChargesFromIVDListItemArray.isEmpty());
  }

  @Test
  void testGetAdditionalChargesFromIVDListItemArray_whenIVDListItemListIsNull() {

    Optional<List<AdditionalChargeItem>> additionalChargesFromIVDListItemArray =
        AdditionalChargesHelper.getAdditionalChargesFromIVDListItemArray(null);

    assertTrue(additionalChargesFromIVDListItemArray.isEmpty());
  }
}
