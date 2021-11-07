package org.kp.foundation.core.use;

import com.adobe.cq.sightly.WCMUsePojo;
import javax.servlet.http.HttpServletResponse;
import org.kp.foundation.core.utils.LinkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Redirect use class is responsible to redirect the page to other urls.
 * 
 * @author Mohan Joshi
 *
 */

public class RedirectUse extends WCMUsePojo {
	private static final Logger LOGGER = LoggerFactory.getLogger(RedirectUse.class);
	private static final String REDIRECT_TYPE = "redirectType";
	private static final String REDIRECT_TARGET = "redirectTarget";
	private static final String ENABLE_REDIRECTION = "enableRedirection";

	private String redirectType;
	private String location;
	private String redirectPath;
	private String enableRedirection;
	private boolean redirectOn;

	@Override
	public void activate() throws Exception {
		LOGGER.info("Activating Redirect Use");
		redirectType = getProperties().get(REDIRECT_TYPE, "");
		location = getProperties().get(REDIRECT_TARGET, "");
		enableRedirection = getProperties().get(ENABLE_REDIRECTION, "");
	}

	/**
	 * This method is responsible to redirect the page to other urls.
	 * 
	 * @throws Exception
	 * 
	 * @returns the childlinks for rootPath selected
	 */
	public void getRedirectUrl() throws Exception {
		redirectOn = Boolean.parseBoolean(enableRedirection);
		if (redirectOn && (location.length() > 0)) {
			if (getCurrentPage() != null && !location.equals(getCurrentPage().getPath())) {
				final int protocolIndex = location.indexOf(":/");
				final int queryIndex = location.indexOf('?');
				if (protocolIndex > -1 && (queryIndex == -1 || queryIndex > protocolIndex)) {
					redirectPath = location;
				} else {
					redirectPath = LinkUtil.getRelativeURL(getRequest(), location);
				}
				getResponse().setStatus(Integer.parseInt(redirectType));
				getResponse().setHeader("Location", redirectPath);
				getResponse().setHeader("Connection", "close");
			} else {
				getResponse().sendError(HttpServletResponse.SC_NOT_FOUND);
			}
		}
	}

	/**
	 * Getter for Redirect path.
	 * 
	 * @return the redirectPath
	 */
	public String getRedirectPath() {
		return location;
	}

	/**
	 * Getter to check if Redirect is enabled.
	 * 
	 * @return the enableRedirection
	 */
	public String getEnableRedirection() {
		return enableRedirection;
	}

	/**
	 * Getter for Redirect type.
	 * 
	 * @return the redirectType
	 */
	public String getRedirectType() {
		return redirectType;
	}
}
