package edu.iu.uits.lms.redirect.controller;

import edu.iu.uits.lms.lti.controller.ConfigurationController;
import edu.iu.uits.lms.lti.model.Canvas13Extension;
import edu.iu.uits.lms.lti.model.Canvas13Placement;
import edu.iu.uits.lms.lti.model.Canvas13Settings;
import edu.iu.uits.lms.lti.model.Lti13Config;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static edu.iu.uits.lms.lti.LTIConstants.CUSTOM_CANVAS_COURSE_ID_KEY;
import static edu.iu.uits.lms.lti.LTIConstants.CUSTOM_CANVAS_COURSE_ID_VAL;
import static edu.iu.uits.lms.lti.LTIConstants.CUSTOM_CANVAS_USER_LOGIN_ID_KEY;
import static edu.iu.uits.lms.lti.LTIConstants.CUSTOM_CANVAS_USER_LOGIN_ID_VAL;
import static edu.iu.uits.lms.lti.LTIConstants.CUSTOM_CANVAS_USER_SIS_ID_KEY;
import static edu.iu.uits.lms.lti.LTIConstants.CUSTOM_CANVAS_USER_SIS_ID_VAL;
import static edu.iu.uits.lms.lti.controller.RedirectableLtiController.CUSTOM_REDIRECT_URL_PROP;
import static edu.iu.uits.lms.lti.model.Canvas13Placement.WINDOW_TARGET_BLANK;

@RestController
public class Config13Controller extends ConfigurationController {

    @GetMapping("/config.json")
    public Lti13Config getConfig(HttpServletRequest request) {
        String urlPrefix = ServletUriComponentsBuilder.fromContextPath(request).toUriString();
        Canvas13Placement coursePlacement = Canvas13Placement.builder()
              .placement(Canvas13Placement.Placement.COURSE_NAVIGATION)
              .enabled(true)
              .messageType(Canvas13Placement.MessageType.LtiResourceLinkRequest)
//              .targetLinkUri(urlPrefix + "/launch")
              .windowTarget(WINDOW_TARGET_BLANK)
              .build();
//        Canvas13Placement accountPlacement = Canvas13Placement.builder()
//              .placement(Canvas13Placement.Placement.ACCOUNT_NAVIGATION)
//              .enabled(true)
//              .messageType(Canvas13Placement.MessageType.LtiResourceLinkRequest)
//              .build();
        List<Canvas13Placement> placements = Arrays.asList(coursePlacement);
        Canvas13Settings canvas13Settings = Canvas13Settings.builder()
              .placements(placements)
              .build();
        Collection<Canvas13Extension> extensions  = Collections.singleton(Canvas13Extension.builder()
              .platform(Canvas13Extension.INSTRUCTURE)
              .domain(request.getServerName())
              .privacyLevel(Lti13Config.PrivacyLevel.PUBLIC)
              .settings(canvas13Settings)
              .build());
        Map<String, String> customFields = new HashMap<>();
        customFields.put(CUSTOM_CANVAS_COURSE_ID_KEY, CUSTOM_CANVAS_COURSE_ID_VAL);
        customFields.put(CUSTOM_CANVAS_USER_LOGIN_ID_KEY, CUSTOM_CANVAS_USER_LOGIN_ID_VAL);
        customFields.put(CUSTOM_CANVAS_USER_SIS_ID_KEY, CUSTOM_CANVAS_USER_SIS_ID_VAL);
        customFields.put(CUSTOM_REDIRECT_URL_PROP, "http://www.google.com/search?q=${CANVAS_COURSE_ID}+${SIS_CAMPUS}");

        String title = "LMS Redirect";
        String description = "Redirect tool";
        String login = "lms_lti_redirect";
        String launch = "/redirect";

        return Lti13Config.builder()
              .title(title)
              .description(description)
              .oidcInitiationUrl(urlPrefix + "/lti/login_initiation/" + login)
              .targetLinkUri(urlPrefix + launch)
              .extensions(extensions)
              .publicJwk(getJKS().toPublicJWK().toJSONObject())
              .customFields(customFields)
//              .privacyLevel(Lti13Config.PrivacyLevel.PUBLIC)
//              .scopes(Collections.singleton(Lti13Config.Scopes.SCOPE_MEMBERSHIP_READONLY.getValue()))
              .build();
    }

}
