package org.kp.foundation.core.use;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.kp.foundation.core.models.LinkModel;
import org.kp.foundation.core.utils.LinkUtil;
import org.kp.foundation.core.utils.PropertyInheritedUtil;

public class SocialLinksUse extends BaseWCMUse {

	private static final String SOCIAL_LINKS_PROPERTY = "socialMediaIcons";
	private static final String HIDE_SOCIAL_LINKS = "hide";
	private static final String HEADER_TITLE = "socialHeader";
	private List<LinkModel> socialLinks = new ArrayList<LinkModel>();
	private String headerTitle = "";
	private String hide;
	private String style = "";

	public void activate() throws Exception {
		headerTitle = PropertyInheritedUtil.getProperty(getResource(), HEADER_TITLE);
		headerTitle = StringUtils.isNotEmpty(headerTitle) ? headerTitle : "";
		hide = getProperties().get(HIDE_SOCIAL_LINKS, "");
		Resource linkRootRes = PropertyInheritedUtil.getChildNodeResource(getResource(), SOCIAL_LINKS_PROPERTY);
		if (linkRootRes == null) {
			return;
		} else {
			Iterator<Resource> itr = linkRootRes.listChildren();
			while (itr.hasNext()) {
				Resource linkRes = itr.next();
				LinkModel socialLinkModel = linkRes.adaptTo(LinkModel.class);
				socialLinkModel.setResourceLink(LinkUtil.getPathfieldURL(socialLinkModel.getResourceLink()));
				socialLinks.add(socialLinkModel);
			}
		}
		if (getWcmMode().isEdit()) {
			style = "float:none";
		}
	}

	public List<LinkModel> getSocialLinks() {
		return new ArrayList<>(socialLinks);
	}

	public String getHeaderTitle() {
		return headerTitle;
	}

	/**
	 * Getter for hide.
	 * 
	 * @return the hide
	 */
	public String getHide() {
		return hide;
	}

	public String getStyle() {
		return style;
	}
}
