package org.activiti.hframework.bridge.explorer.config;

import org.springframework.context.annotation.*;

@Configuration
@PropertySources({
  @PropertySource(value = "classpath:ui.properties", ignoreResourceNotFound = true),
  @PropertySource(value = "classpath:db.properties", ignoreResourceNotFound = true),
  @PropertySource(value = "classpath:engine.properties", ignoreResourceNotFound = true)
})
@ComponentScan(basePackages = { "org.activiti.explorer.conf" })
@ImportResource({"classpath:activiti-ui-context.xml", "classpath:activiti-login-context.xml", "classpath:activiti-custom-context.xml"})
public class ApplicationConfiguration {
  
}
