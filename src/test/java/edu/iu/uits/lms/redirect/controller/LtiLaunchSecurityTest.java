package edu.iu.uits.lms.redirect.controller;

import edu.iu.uits.lms.common.variablereplacement.VariableReplacementService;
import edu.iu.uits.lms.lti.config.LtiClientTestConfig;
import edu.iu.uits.lms.lti.model.LmsLtiAuthz;
import edu.iu.uits.lms.lti.service.LtiAuthorizationServiceImpl;
import edu.iu.uits.lms.redirect.config.ToolConfig;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthException;
import net.oauth.OAuthMessage;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.tsugi.basiclti.BasicLTIConstants;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static edu.iu.uits.lms.lti.controller.RedirectableLtiController.CUSTOM_REDIRECT_URL_PROP;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@RunWith(SpringRunner.class)
@WebMvcTest(RedirectLtiController.class)
@Import({ToolConfig.class, LtiClientTestConfig.class})
//@ActiveProfiles("none")
public class LtiLaunchSecurityTest {

   @Autowired
   private MockMvc mvc;

   @MockBean
   private LtiAuthorizationServiceImpl ltiAuthorizationService;

//   @MockBean
//   private LtiPropsApi ltiPropsApi;

   @MockBean
   private VariableReplacementService variableReplacementService;

   @Test
   public void ltiLaunch() throws Exception {

      String key = "asdf";
      String secret = "secret";

      LmsLtiAuthz ltiAuthz = new LmsLtiAuthz();
      ltiAuthz.setActive(true);
      ltiAuthz.setConsumerKey(key);
      ltiAuthz.setSecret(secret);

      doReturn(ltiAuthz).when(ltiAuthorizationService).findByKeyContextActive(any(), any());
      doReturn("ltiAuthz").when(variableReplacementService).performMacroVariableReplacement(any(), any());

      Map<String, String> params = new HashMap<>();
      params.put(BasicLTIConstants.LTI_MESSAGE_TYPE, BasicLTIConstants.LTI_MESSAGE_TYPE_BASICLTILAUNCHREQUEST);
      params.put(BasicLTIConstants.LTI_VERSION, BasicLTIConstants.LTI_VERSION_1);
      params.put("oauth_consumer_key", key);
      params.put(BasicLTIConstants.USER_ID, "user");
      params.put(BasicLTIConstants.ROLES, "Instructor");
      params.put(CUSTOM_REDIRECT_URL_PROP, "http://www.google.com");

      Map<String, String> signedParams = signParameters(params, key, secret, "http://localhost/lti", "POST");


      List<NameValuePair> nvpList = signedParams.entrySet().stream()
            .map(entry -> new BasicNameValuePair(entry.getKey(), entry.getValue()))
            .collect(Collectors.toCollection(() -> new ArrayList<>(signedParams.size())));


      //This is an open endpoint and should not be blocked by security
      mvc.perform(post("/lti")
            .header(HttpHeaders.USER_AGENT, "foobar")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .content(EntityUtils.toString(new UrlEncodedFormEntity(nvpList))))

            .andExpect(status().is3xxRedirection());
   }

   private Map<String, String> signParameters(Map<String, String> parameters, String key, String secret, String url, String method) throws Exception {
      OAuthMessage oam = new OAuthMessage(method, url, parameters.entrySet());
      OAuthConsumer cons = new OAuthConsumer(null, key, secret, null);
      OAuthAccessor acc = new OAuthAccessor(cons);
      try {
         oam.addRequiredParameters(acc);

         Map<String, String> signedParameters = new HashMap<>();
         for(Map.Entry<String, String> param : oam.getParameters()){
            signedParameters.put(param.getKey(), param.getValue());
         }
         return signedParameters;
      } catch (OAuthException | IOException | URISyntaxException e) {
         throw new Exception("Error signing LTI request.", e);
      }
   }

}
