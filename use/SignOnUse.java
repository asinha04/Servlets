package org.kp.foundation.core.use;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.kp.foundation.core.utils.LinkUtil;
import org.kp.foundation.core.utils.PropertyInheritedUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SignOnUse extends BaseWCMUse {

	 private static final Logger LOGGER = LoggerFactory.getLogger(SignOnUse.class);
	 private static final String DEFAULT_LOGIN_LINK_NAME = "Sign in to access care";
	 private static final String SIGN_IN_LINK_URL = "signInLinkUrl";
	 private static final String LOGIN_LINK_NAME = "signInLinkName";
	 private String loginLinkUrl;
	 private String loginLinkName;


    @Override
    public void activate() throws Exception {
      	
    	Resource signOnNodeResource = getResource();
    	LOGGER.debug("Node being looked at---> {}.", signOnNodeResource.getPath());
    	
	    loginLinkUrl = PropertyInheritedUtil.getProperty(signOnNodeResource, SIGN_IN_LINK_URL);	
		loginLinkName = PropertyInheritedUtil.getProperty(signOnNodeResource, LOGIN_LINK_NAME);
		
		LOGGER.debug("loginLinkUrl-->{}.", loginLinkUrl);
		LOGGER.debug("loginLinkName--> {}.", loginLinkName);


    }

    public String getLoginLinkUrl() {
	return StringUtils.isEmpty(loginLinkUrl) ? "#":LinkUtil.getPathfieldURL(loginLinkUrl);
    }

    public String getLoginLinkName() {
	return StringUtils.isEmpty(loginLinkName) ? DEFAULT_LOGIN_LINK_NAME : loginLinkName;
    }

}
