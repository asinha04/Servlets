package org.kp.foundation.core.use;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.kp.foundation.core.constants.GlobalConstants;
import org.kp.foundation.core.constants.RegionPicker;
import org.kp.foundation.core.constants.TransformerConstants;
import org.kp.foundation.core.models.TopNavModel;
import org.kp.foundation.core.utils.LinkUtil;
import org.kp.foundation.core.utils.PropertyInheritedUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.sightly.WCMUsePojo;

//Waiver details on GSC-3619
@SuppressWarnings({ "squid:S3776" })
public class TopNavUse extends WCMUsePojo {

	private static final Logger LOGGER = LoggerFactory.getLogger(TopNavUse.class);
	private static final String HEALTH_MYCARE_TYPE = "/health/mycare/consumer";
	private static final String NAV_TYPE = "navType";
	private List<TopNavModel> navLinks = new ArrayList<TopNavModel>();
	private int numberOfLinks;
	private String navBackgroundColor;
	private String disableRegionPicker;

	/**
	 * Initializes the navigation
	 */
	public void activate() throws Exception {
		String regionName = "";
		// When on language page or site page - scenario only possible in author ==> set
		// region name to "unknownRegion"
		LOGGER.debug("getCurrentPage().getDepth() -->{}", getCurrentPage().getDepth());
		if (RegionPicker.CUR_REGION_ROOT_LEVEL < getCurrentPage().getDepth()) {
			regionName = getCurrentPage().getAbsoluteParent(RegionPicker.CUR_REGION_ROOT_LEVEL).getName();
		} else {
			regionName = RegionPicker.UNKNOWN_REGION;
		}
		Locale curLocale = getCurrentPage().getLanguage(false);
		String navType = get(NAV_TYPE, String.class);
		Resource linkRootRes = PropertyInheritedUtil.getChildNodeResource(getResource(), navType);
		if (linkRootRes == null) {
			LOGGER.error("TopNav Links are not set!");
			return;
		}

		LOGGER.debug("regionName -->{}", regionName);
		LOGGER.debug("curLocale.toString() -->{}", curLocale.toString());
		String disableRegionPickerProperty = PropertyInheritedUtil.getProperty(getResource(),
				GlobalConstants.DISABLE_REGION_PICKER_PROP);
		if (StringUtils.isNotEmpty(disableRegionPickerProperty)) {
			setDisableRegionPicker(disableRegionPickerProperty);
		}

		Iterator<Resource> itr = linkRootRes.listChildren();
		while (itr.hasNext()) {
			Resource linkRes = itr.next();
			TopNavModel topNavModel = linkRes.adaptTo(TopNavModel.class);
			String curPagePathTopNav = topNavModel.getPagePath();

			if (StringUtils.isNotEmpty(curPagePathTopNav)) {
				// We would allowing absolute urL for Health care related
				// patterns, rest would be relative URL
				if (curPagePathTopNav.startsWith(HEALTH_MYCARE_TYPE)) {
					topNavModel.setPagePath(
							LinkUtil.getUnsecureAbsoluteURL(getRequest(), curPagePathTopNav, getCurrentPage()));
				} else {

					if ((curPagePathTopNav.startsWith(TransformerConstants.CONTENT)
							|| curPagePathTopNav.startsWith(TransformerConstants.CONTENT_STRING))
							&& !(curPagePathTopNav.contains("/" + regionName))) {
						String replaceString = TransformerConstants.CONTENT + "/" + curLocale.toString();
						if (curPagePathTopNav.contains(GlobalConstants.NATIONAL_PATH)) {
							replaceString = GlobalConstants.NATIONAL_PATH;
							curPagePathTopNav = StringUtils.replace(curPagePathTopNav, replaceString, "/" + regionName);
						} else {
							curPagePathTopNav = StringUtils.replace(curPagePathTopNav, replaceString,
									replaceString + "/" + regionName);
						}
					}
					topNavModel.setPagePath(LinkUtil.getPathfieldURL(curPagePathTopNav));

				}
				LOGGER.debug("curPagePathTopNav after modifying -->{}", curPagePathTopNav);
				// check if the topNav link is currently the page we are on
				String curPagePath = getCurrentPage().getPath();
				String curPageName = getCurrentPage().getName();

				LOGGER.debug("curPagePath -->{}", curPagePath);
				LOGGER.debug("curPageName -->{}", curPageName);

				if (StringUtils.isNotEmpty(curPagePath)) {
					curPagePathTopNav = curPagePathTopNav.replace(GlobalConstants.HTML_SUFFIX, "");
					LOGGER.debug("curPagePathTopNav after removing suffix -->{}", curPagePathTopNav);
					String curPageNameTopNav = curPagePathTopNav.substring(curPagePathTopNav.lastIndexOf("/") + 1);
					LOGGER.debug("curPageNameTopNav-->{}", curPageNameTopNav);
					if (StringUtils.equalsIgnoreCase(curPageName, curPageNameTopNav)
							|| curPagePath.contains("/" + curPageNameTopNav + "/")) {
						topNavModel.setActive(true);
						setNavBackgroundColor(topNavModel.getColor());
					}
				}
			}
			navLinks.add(topNavModel);
			LOGGER.debug("Background colour set -->{}", getNavBackgroundColor());
		}

		LOGGER.debug("TopNavLinks {}.", navLinks.toString());
		numberOfLinks = navLinks.size();

	}

	/**
	 * Get navigation links.
	 * 
	 * @return List
	 */
	public List<TopNavModel> getNavLinks() {
		return new ArrayList<>(navLinks);
	}

	public int getIncrementOfLinksCount() {
		return numberOfLinks + 1;
	}

	public int getNumberOfLinks() {
		return numberOfLinks;
	}

	public String getNavBackgroundColor() {
		return navBackgroundColor;
	}

	public void setNavBackgroundColor(String navBackgroundColor) {
		this.navBackgroundColor = navBackgroundColor;
	}

	public String getDisableRegionPicker() {
		return disableRegionPicker;
	}

	public void setDisableRegionPicker(String disableRegionPicker) {
		this.disableRegionPicker = disableRegionPicker;
	}

}
