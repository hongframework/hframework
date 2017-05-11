package com.hframework.base.service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@Configuration
public class SmartDispatcherServletConfiguration {}/**extends WebMvcConfigurationSupport {

  @Bean()
  public RequestMappingHandlerMapping requestMappingHandlerMapping() {
    return new SmartRequestMappingHandlerMapping(super.requestMappingHandlerMapping(), getInterceptors());
  }
  public class SmartRequestMappingHandlerMapping extends RequestMappingHandlerMapping {

    public SmartRequestMappingHandlerMapping() {
    }

    public SmartRequestMappingHandlerMapping(RequestMappingHandlerMapping super1, Object[] interceptors) {
      this.setOrder(super1.getOrder());
      this.setInterceptors(interceptors);
      this.setContentNegotiationManager(super1.getContentNegotiationManager());
      this.setCorsConfigurations(super1.getCorsConfigurations());
      this.setUseSuffixPatternMatch(super1.useSuffixPatternMatch());
      this.setUseRegisteredSuffixPatternMatch(super1.useRegisteredSuffixPatternMatch());
      this.setUseTrailingSlashMatch(super1.useTrailingSlashMatch());
      this.setPathMatcher(super1.getPathMatcher());
      this.setUrlPathHelper(super1.getUrlPathHelper());
    }
  }

}
*/