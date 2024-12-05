package com.cdg.pmg.ngp.me.vehiclecomm.application.outbound.ports.web;

import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.CmsConfiguration;

/** Interface Configuration service. */
public interface ConfigurationService {
  /**
   * Get configuration from cms config server
   *
   * @return cms configuration
   */
  CmsConfiguration getCmsConfiguration();
}
