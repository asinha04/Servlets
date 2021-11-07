package org.kp.foundation.core.use;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.kp.foundation.core.models.MultiFieldLinkModel;
import org.kp.foundation.core.utils.LinkUtil;
import org.kp.foundation.core.utils.PropertyInheritedUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.sightly.WCMUsePojo;

/**
 * 
 */
public class DynamicLinkListUse extends WCMUsePojo {
	protected final Logger LOGGER = LoggerFactory.getLogger(DynamicLinkListUse.class);
	private String title;
	private String role;
	private static final String PAGE_LINKS = "pageLinks";
	private static final String DLL_TITLE = "title";
	private static final String LINK_TYPE = "selector";
	private static final String LINK_ID = "linkId";
	private static final String TITLE_STYLE = "titleStyle";
	private static final String TITLE_STYLE_SMALL = "small";
	private static final String TITLE_STYLE_LARGE = "large";
	private static final String ROLE_PRESENTATION = "presentation";
	private static final String ROLE_REGION = "region";
	private List<MultiFieldLinkModel> pageLinks = new ArrayList<MultiFieldLinkModel>();
	private String linkType;
	private String dllLinkId;
	private String titleStyle;

	@Override
	public void activate() throws Exception {
		title = PropertyInheritedUtil.getProperty(getResource(), DLL_TITLE);
		linkType = PropertyInheritedUtil.getProperty(getResource(), LINK_TYPE);
		dllLinkId = PropertyInheritedUtil.getProperty(getResource(), LINK_ID);
		titleStyle = PropertyInheritedUtil.getProperty(getResource(), TITLE_STYLE);
		Resource linkRootRes = PropertyInheritedUtil.getChildNodeResource(getResource(), PAGE_LINKS);
		
		if (linkRootRes == null) {
			return;
		} else {
			Iterator<Resource> itr = linkRootRes.listChildren();
			while (itr.hasNext()) {
				Resource linkRes = itr.next();
				MultiFieldLinkModel linkListModel = linkRes.adaptTo(MultiFieldLinkModel.class);
				linkListModel.setLinkPath(LinkUtil.getPathfieldURL(linkListModel.getLinkPath()));
				pageLinks.add(linkListModel);
				LOGGER.debug("DynamicLinkListUse :: pageLinks :: ", pageLinks.toString());
			}
		}

		if (pageLinks.size() == 1)
			role = ROLE_PRESENTATION;
		else
			role = ROLE_REGION;
	}

	public String getLinkType() {
		return linkType;
	}

	public void setLinkType(String linkType) {
		this.linkType = linkType;
	}

	public String getDllLinkId() {
		return dllLinkId;
	}

	public void setDllLinkId(String dllLinkId) {
		this.dllLinkId = dllLinkId;
	}

	public String getTitle() {
		return title;
	}

	public List<MultiFieldLinkModel> getPageLinks() {
		return new ArrayList<>(pageLinks);
	}

	/**
	 * @return the titleStyle - setting the default value as small which will make
	 *         this component backward compatible.
	 */
	public String getTitleStyle() {
		String style = TITLE_STYLE_SMALL;
		if (StringUtils.isNotBlank(titleStyle)
				&& (titleStyle.equals(TITLE_STYLE_SMALL) || titleStyle.equals(TITLE_STYLE_LARGE))) {
			style = titleStyle;
		}
		return style;
	}

	/**
	 * @return the role based on the size of the links
	 */
	public String getRole() {
		return role;
	}

}