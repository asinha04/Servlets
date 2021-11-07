package org.kp.foundation.core.use;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.wcm.api.Page;

public class CheckErrorUse extends WCMUsePojo {

	private static final Logger LOGGER = LoggerFactory.getLogger(CheckErrorUse.class);
	
	private Boolean error404 = false;
	private Boolean error500 = false;
	private Page currentPage;
	
	/**
	 * Initializes the footer
	 */
	public void activate() throws Exception {
		currentPage = getCurrentPage();
		if(checkChildResource(currentPage, "bodypar/globalerror")) {
			LOGGER.debug("Current Page Name --> {}.", currentPage.getName());
			if(StringUtils.equalsIgnoreCase(currentPage.getName(), "404")) { 
				error404 = true;
			}
			if(currentPage.getName().matches("50.?")) { 
				error500 = true;
			}
			LOGGER.debug("error404 --> {}", error404);
			LOGGER.debug("error500 --> {}", error500);
		}
	}

	
	public Boolean getError404() {
		return error404;
	}

	public Boolean getError500() {
		return error500;
	}

	public Boolean checkChildResource(Page page, String childResource)
	{
		if(page.getContentResource(childResource) != null){
			return true;
		}
		
		return false;
	}

}


