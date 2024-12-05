package com.cdg.pmg.ngp.me.vehiclecomm.techframework.web.configs;

import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.CmsConfiguration;
import com.cdg.pmg.ngp.me.vehiclecomm.application.outbound.ports.web.ConfigurationService;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.annotations.ServiceComponent;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.configs.properties.RefreshableProperties;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.web.mappers.ConfigurationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/** Class is used to fetch the cms configuration */
@RequiredArgsConstructor
@ServiceComponent
@Slf4j
public class ConfigurationServiceImpl implements ConfigurationService {
  private final RefreshableProperties refreshableProperties;
  private final ConfigurationMapper configurationMapper;

  /**
   * Method is used to get cms configurations
   *
   * @return CmsConfiguration dto
   */
  @Override
  public CmsConfiguration getCmsConfiguration() {
    return configurationMapper.refreshablePropertiesToCmsConfig(refreshableProperties);
  }
}
