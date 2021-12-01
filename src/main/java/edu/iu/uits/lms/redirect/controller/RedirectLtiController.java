package edu.iu.uits.lms.redirect.controller;

import edu.iu.uits.lms.common.variablereplacement.VariableReplacementService;
import edu.iu.uits.lms.lti.controller.RedirectableLtiController;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.tsugi.basiclti.BasicLTIConstants;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chmaurer on 11/20/14.
 */
@Controller
@RequestMapping("/lti")
public class RedirectLtiController extends RedirectableLtiController {

    private boolean openLaunchUrlInNewWindow = false;

    @Autowired
    private VariableReplacementService variableReplacementService = null;

    @Override
    protected VariableReplacementService getVariableReplacementService() {
        return variableReplacementService;
    }

    @Override
    protected String getLaunchUrl(Map<String, String> launchParams) {
        String redirectUrl = launchParams.get(CUSTOM_REDIRECT_URL_PROP);
        return performMacroVariableReplacement(redirectUrl, launchParams);
    }

    @Override
    protected Map<String, String> getParametersForLaunch(Map<String, String> payload, Claims claims) {
        Map<String, String> paramMap = new HashMap<String, String>(1);

        for (String prop : getLaunchParamList()) {
            paramMap.put(prop, payload.get(prop));
        }

        openLaunchUrlInNewWindow = Boolean.valueOf(payload.get(CUSTOM_OPEN_IN_NEW_WINDOW));

        return paramMap;
    }

    @Override
    protected LAUNCH_MODE launchMode() {
        if (openLaunchUrlInNewWindow) {
            return LAUNCH_MODE.WINDOW;
        }
        return LAUNCH_MODE.NORMAL;
    }

    protected List<String> getLaunchParamList() {
        return Arrays.asList(CUSTOM_REDIRECT_URL_PROP, CUSTOM_CANVAS_COURSE_ID,
                CUSTOM_CANVAS_USER_ID, CUSTOM_CANVAS_USER_LOGIN_ID, BasicLTIConstants.LIS_PERSON_NAME_FAMILY,
                BasicLTIConstants.LIS_PERSON_NAME_GIVEN, BasicLTIConstants.LIS_PERSON_SOURCEDID, BasicLTIConstants.ROLES);
    }

    @Override
    protected String getToolContext() {
        return "lms_redirect";
    }

}
