package org.kp.foundation.core.use;

import org.kp.foundation.core.constants.GlobalConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.sightly.WCMUsePojo;

public class HeaderStateUse extends WCMUsePojo {

    private static final Logger LOGGER = LoggerFactory.getLogger(HeaderStateUse.class);
    private String templatePath;
    private static final String LOGGED_OUT = "LOGGED_OUT";
   	private static final String HOME_PAGE = "home-page";
	private static final String HOME = "HOME";

    @Override
    public void activate() throws Exception {
        templatePath = getPageProperties().get(GlobalConstants.CQ_TEMPLATE, "");
    }

    public String getHeaderState() {

        String[] tokens = templatePath.split("/");
        String templateName = tokens[tokens.length - 1];
        LOGGER.debug("Template Name = {}.", templateName);
        if(templateName!= null &&templateName.equalsIgnoreCase(HOME_PAGE)) {
        	return HOME;
        } else {
        	return LOGGED_OUT;
        }
       
    }
 
}
