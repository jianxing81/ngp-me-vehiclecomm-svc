package com.cdg.pmg.ngp.me.vehiclecomm.techframework.web.mappers;

import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.CmsConfiguration;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.configs.properties.RefreshableProperties;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

/** Mapper used to map refreshableProperties */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ConfigurationMapper {
  /**
   * Mapper properties to vehiclecomm config
   *
   * @param properties properties can be changed by CMS and AWS parameter store
   * @return VehicleCommConfiguration
   */
  CmsConfiguration refreshablePropertiesToCmsConfig(RefreshableProperties properties);
}
