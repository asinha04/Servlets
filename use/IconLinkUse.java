package org.kp.foundation.core.use;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.kp.foundation.core.utils.PropertyInheritedUtil;

/**
 * The Class IconLinkUse.
 */
public class IconLinkUse extends BaseWCMUse {
	public static final String STYLE_LINK = "link";
	public static final String STYLE_TILE = "tile";
	
	private String iconLinkId;
	private String componentStyle;
	private String titleIcon;
	private String title;
	private String subTitle;
	private String description;
	private String linkLabel;
	private String linkPath;
	private String linkTarget;
	private Boolean linkNoFollow;
	private String noFollow;
	private Boolean showLink;	
	private Boolean notTile;
	private Boolean hideIcon;
	
	@Override
	public void activate() throws Exception {
		iconLinkId = PropertyInheritedUtil.getProperty(getResource(),"iconLinkId");
		componentStyle = PropertyInheritedUtil.getProperty(getResource(),"componentStyle");
		titleIcon = PropertyInheritedUtil.getProperty(getResource(),"titleIcon");
		title = StringUtils.trim(PropertyInheritedUtil.getProperty(getResource(),"title"));
		subTitle = StringUtils.trim(PropertyInheritedUtil.getProperty(getResource(),"subTitle"));
		description = PropertyInheritedUtil.getProperty(getResource(),"description");
		linkLabel = StringUtils.trim(PropertyInheritedUtil.getProperty(getResource(),"linkLabel"));
		linkPath = StringUtils.trim(PropertyInheritedUtil.getProperty(getResource(),"linkPath"));
		linkTarget = PropertyInheritedUtil.getProperty(getResource(),"linkTarget");
		linkNoFollow = BooleanUtils.toBoolean(PropertyInheritedUtil.getProperty(getResource(),"linkNoFollow"));
		hideIcon = BooleanUtils.toBoolean(PropertyInheritedUtil.getProperty(getResource(),"hideIcon"));

		// Hide / Show logic for attributes
		noFollow = linkNoFollow ? "nofollow" : "";
		notTile = !StringUtils.equals(STYLE_TILE, this.componentStyle);
		showLink = StringUtils.isNotBlank(linkPath) || StringUtils.isNotBlank(linkLabel) || StringUtils.isNotBlank(linkTarget);
	}

	public String getIconLinkId() {
		return iconLinkId;
	}

	public String getComponentStyle() {
		return componentStyle;
	}

	public String getTitleIcon() {
		return titleIcon;
	}

	public String getTitle() {
		return title;
	}

	public String getSubTitle() {
		return subTitle;
	}

	public String getDescription() {
		return description;
	}

	public String getLinkLabel() {
		return linkLabel;
	}

	public String getLinkPath() {
		return linkPath;
	}
	
	public String getLinkTarget() {
		return linkTarget;
	}

	public Boolean getLinkNoFollow() {
		return linkNoFollow;
	}
	
	public String getNoFollow() {
		return noFollow;
	}
	
	public Boolean getNotTile() {
		return notTile;
	}

	public Boolean getShowLink() {
		return showLink;
	}
	public Boolean getHideIcon() {
		return hideIcon;
	}
}
