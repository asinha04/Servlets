package org.kp.foundation.core.use;

import org.apache.commons.lang.StringUtils;
import org.kp.foundation.core.utils.PropertyInheritedUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FooterTrusteeUse extends BaseWCMUse {

	private static final Logger LOGGER = LoggerFactory.getLogger(FooterTrusteeUse.class);
	private static final String HIDE_TRUSTEE = "hide";
	private String trusteeAltText = "";
	private String trusteeImagePath = "";
	private String trusteeLinkPath;
    private String hide;
    
	/**
	 * Initializes the footer
	 */
	@Override
	public void activate() throws Exception {
		LOGGER.debug("activate method Started for::{}.",  this.getClass().getName());
		trusteeAltText = PropertyInheritedUtil.getProperty(getResource(), "trusteeAltText");
		trusteeAltText = StringUtils.isNotEmpty(trusteeAltText) ? trusteeAltText : "";

		trusteeImagePath = PropertyInheritedUtil.getProperty(getResource(), "trusteeImagePath");
		trusteeImagePath = StringUtils.isNotEmpty(trusteeImagePath) ? trusteeImagePath : "";
		
		
		trusteeLinkPath = PropertyInheritedUtil.getProperty(getResource(), "trusteeLinkPath");
		trusteeLinkPath = StringUtils.isNotEmpty(trusteeLinkPath) ? trusteeLinkPath : "";
		
		 hide = getProperties().get(HIDE_TRUSTEE, "");
		
		LOGGER.debug("activate method ended:: {}.", this.getClass().getName());
	}
	
	public String getTrusteeAltText() {
		return trusteeAltText;
	}

	
	public String getTrusteeImagePath() {
		return trusteeImagePath;
	}
	
	
	public String getTrusteeLinkPath() {
		return trusteeLinkPath;
	}

	/**
	 * Getter for hide.
	 * 
	 * @return the hide
	 */
	public String getHide() {
		return hide;
	}

}
