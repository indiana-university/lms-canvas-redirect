package edu.iu.uits.lms.redirect;

import edu.iu.uits.lms.common.variablereplacement.EnableVariableReplacementService;
import edu.iu.uits.lms.lti.config.EnableGlobalErrorHandler;
import edu.iu.uits.lms.lti.config.EnableLtiClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableGlobalErrorHandler
@EnableLtiClient
@EnableVariableReplacementService
public class WebApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebApplication.class, args);
    }

}
