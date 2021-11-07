package org.kp.foundation.core.use;

import org.kp.foundation.core.constants.GlobalConstants;
import org.kp.foundation.core.constants.LanguagePicker;
import org.kp.foundation.core.constants.ProxyComponent;
import org.kp.foundation.core.constants.RegionPicker;
import org.kp.foundation.core.utils.LinkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;

/**
 * The ProxyPlaceHolderUse class provides the logic to return data URI to the servlet that returns the error message(s).
 *
 * @author Krassimir Boyanov
 *
 */
public class ProxyPlaceHolderUse extends BaseWCMUse {
	private static final Logger LOGGER = LoggerFactory.getLogger(ProxyPlaceHolderUse.class);
	private String uri = "";

	/**
	 *
	 * Activate method that Sightly executes
	 */
	@Override
	public void activate() throws Exception {
		Page currRegion = getCurrentPage().getAbsoluteParent(RegionPicker.CUR_REGION_ROOT_LEVEL);
		Page languagePage = getCurrentPage().getAbsoluteParent(LanguagePicker.SITE_LANGUAGE_PAGE_LEVEL);

		if (currRegion != null && languagePage != null) {
			this.uri = currRegion.getPath() + ProxyComponent.SYSTEM_ERROR_MESSAGE_PATH;

		} else if (languagePage != null) {
			this.uri = languagePage.getPath() + GlobalConstants.NATIONAL_PATH
					+ ProxyComponent.SYSTEM_ERROR_MESSAGE_PATH;
		} else {
			this.uri = GlobalConstants.ROOT_PAGE_PATH + GlobalConstants.ENGLISH + GlobalConstants.NATIONAL_PATH
					+ ProxyComponent.SYSTEM_ERROR_MESSAGE_PATH;
		}

		this.uri = LinkUtil.getRelativeURL(getRequest(), this.uri);
		LOGGER.debug("data-uri::", uri);
	}

	public String getUri() {
		return uri;
	}

}