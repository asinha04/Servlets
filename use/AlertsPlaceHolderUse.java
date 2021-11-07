package org.kp.foundation.core.use;

import org.kp.foundation.core.constants.AlertsComponent;
import org.kp.foundation.core.constants.GlobalConstants;
import org.kp.foundation.core.constants.LanguagePicker;
import org.kp.foundation.core.constants.RegionPicker;
import org.kp.foundation.core.utils.LinkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;

/**
 * The AlertsUse class provides the logic to return alerts detail for
 * Notification and Bulletin sections.
 *
 * @author Krishan Rathi
 *
 */
public class AlertsPlaceHolderUse extends BaseWCMUse {
	private static final Logger LOGGER = LoggerFactory.getLogger(AlertsPlaceHolderUse.class);
	private String notificationPath = "";
	private String bulletinPath = "";
	private String placeHolderType = "";
	private static final String PLACEHOLDER_TYPE = "placeHolderType";

	/**
	 *
	 * Activate method that Sightly executes
	 */
	@Override
	public void activate() throws Exception {
		Page currRegion = getCurrentPage().getAbsoluteParent(RegionPicker.CUR_REGION_ROOT_LEVEL);
		Page languagePage = getCurrentPage().getAbsoluteParent(LanguagePicker.SITE_LANGUAGE_PAGE_LEVEL);
		
		if (currRegion != null && languagePage != null) {
			this.notificationPath = currRegion.getPath() + AlertsComponent.NOTIFICATION_COMP_PATH ;
			this.bulletinPath = currRegion.getPath() + AlertsComponent.BULLETIN_COMP_PATH  + "." + getCurrentPage().getName();

		} else if (languagePage != null) {
			this.notificationPath = languagePage.getPath() + GlobalConstants.NATIONAL_PATH
					+ AlertsComponent.NOTIFICATION_COMP_PATH;
			this.bulletinPath = languagePage.getPath() + GlobalConstants.NATIONAL_PATH
					+ AlertsComponent.BULLETIN_COMP_PATH + "." +  getCurrentPage().getName();
		} else {
			this.notificationPath = GlobalConstants.ROOT_PAGE_PATH + GlobalConstants.ENGLISH + GlobalConstants.NATIONAL_PATH
					+ AlertsComponent.NOTIFICATION_COMP_PATH;
			this.bulletinPath = GlobalConstants.ROOT_PAGE_PATH + GlobalConstants.ENGLISH + GlobalConstants.NATIONAL_PATH
					+ AlertsComponent.BULLETIN_COMP_PATH + "." + getCurrentPage().getName();
		}
		
		this.notificationPath = LinkUtil.getRelativeURL(getRequest(), this.notificationPath);
		this.bulletinPath = LinkUtil.getRelativeURL(getRequest(), this.bulletinPath);
		placeHolderType = getProperties().get(PLACEHOLDER_TYPE, "bulletin");
		LOGGER.debug("notificationPath::", notificationPath);
		LOGGER.debug("bulletinPath::",  bulletinPath);
	}

	public String getNotificationPath() {
		return notificationPath;
	}

	public String getBulletinPath() {
		return bulletinPath;
	}

	public String getPlaceHolderType() {
		return placeHolderType;
	}

}