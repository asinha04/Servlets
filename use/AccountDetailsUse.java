package org.kp.foundation.core.use;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.sling.api.resource.Resource;
import org.kp.foundation.core.models.AccountDetailsModel;
import org.kp.foundation.core.utils.LinkUtil;
import org.kp.foundation.core.utils.PropertyInheritedUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AccountDetailsUse extends BaseWCMUse {

	private static final Logger LOGGER = LoggerFactory.getLogger(AccountDetailsUse.class);
	private static final String SIGNOUT_LINK_NAME = "signOutLinkName";
	private static final String ACCOUNT_DETAILS_LINKS = "accountDetailsPageLinks";

	private List<AccountDetailsModel> accountDetailsLinks = new ArrayList<AccountDetailsModel>();
	private String signOutLinkName = null;

	/**
	 * Initializes the Account Details Drop down navigation
	 */
	public void activate() throws Exception {

		Resource resource = getResource();
		LOGGER.debug("Node being looked at---> {}.", resource.getPath());

		signOutLinkName = PropertyInheritedUtil.getProperty(resource, SIGNOUT_LINK_NAME);
		
		Resource linkRootRes = PropertyInheritedUtil.getChildNodeResource(getResource(), ACCOUNT_DETAILS_LINKS);
		if (linkRootRes != null) {
			LOGGER.debug("SignOut Link Name--> {}.", signOutLinkName);
			LOGGER.debug("Account Detail Links Node path --> {}.", linkRootRes.getPath());

			Iterator<Resource> itr = linkRootRes.listChildren();
			while (itr.hasNext()) {
				Resource linkRes = itr.next();
				AccountDetailsModel accountDetailsModel = linkRes.adaptTo(AccountDetailsModel.class);
				accountDetailsModel.setPagePath(LinkUtil.getPathfieldURL(accountDetailsModel.getPagePath()));
				if (Boolean.parseBoolean(accountDetailsModel.getHideLinks())) {
					accountDetailsModel.setHideLinks("PEM");
				} else {
					accountDetailsModel.setHideLinks("");
				}
				accountDetailsLinks.add(accountDetailsModel);
			}
		} else {
			LOGGER.error("Account Details Drop Down Navigation Links are not set!");
			return;
		}

		LOGGER.debug("Account Details Links: ", accountDetailsLinks.toString());
	}

	/**
	 * Get navigation links.
	 * 
	 * @return List
	 */
	public List<AccountDetailsModel> getAccountDetailsLinks() {
		return new ArrayList<>(accountDetailsLinks);
	}

	public String getSignOutLinkName() {
		return this.signOutLinkName;
	}

}
