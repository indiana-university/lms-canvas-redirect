package edu.iu.uits.lms.redirect.controller;

import edu.iu.uits.lms.common.variablereplacement.VariableReplacementService;
import edu.iu.uits.lms.lti.controller.RedirectableLtiController;
import edu.iu.uits.lms.lti.service.OidcTokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.ac.ox.ctl.lti13.security.oauth2.client.lti.authentication.OidcAuthenticationToken;

@Controller
@RequestMapping("/redirect")
public class RedirectController extends RedirectableLtiController {

   @Autowired
   private VariableReplacementService variableReplacementService = null;

   @Override
   protected VariableReplacementService getVariableReplacementService() {
      return variableReplacementService;
   }

   @RequestMapping
   public String redirect() {
      OidcAuthenticationToken token = getTokenWithoutContext();
      String redirectUrl = OidcTokenUtils.getCustomValue(token, CUSTOM_REDIRECT_URL_PROP);
      return "redirect:" + performMacroVariableReplacement(redirectUrl);
   }

}
