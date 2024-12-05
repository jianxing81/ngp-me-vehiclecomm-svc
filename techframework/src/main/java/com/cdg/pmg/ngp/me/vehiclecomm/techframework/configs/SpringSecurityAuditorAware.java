package com.cdg.pmg.ngp.me.vehiclecomm.techframework.configs;

import java.util.Optional;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class SpringSecurityAuditorAware implements AuditorAware<String> {

  @Override
  public Optional<String> getCurrentAuditor() {
    // For HTTP calls
    ServletRequestAttributes requestAttributes =
        (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    if (requestAttributes == null) {
      return Optional.empty();
    }
    String userId = requestAttributes.getRequest().getHeader("X-User-Id");
    return Optional.of(userId);
  }
}
