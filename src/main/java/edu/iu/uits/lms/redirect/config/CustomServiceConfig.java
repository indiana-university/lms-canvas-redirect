package edu.iu.uits.lms.redirect.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.support.GenericWebApplicationContext;


@ConditionalOnProperty("app.customServicePackage")
@ComponentScan("${app.customServicePackage}")
@Configuration
@Slf4j
public class CustomServiceConfig {

   @Autowired
   private GenericWebApplicationContext context = null;

   public CustomServiceConfig() {
      log.debug("CustomServiceConfig()");
   }

}
