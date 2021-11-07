package org.kp.patterns.core.models;

import javax.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.Via;

/**
 * Sling Model class for Feed Container Pattern Component.
 * 
 * * @author Mohan Joshi
 */
@Model(adaptables = { SlingHttpServletRequest.class, Resource.class })
public class FeedContainerPatternModel {

	private static final String ICON_LINK_STYLE = "IconLinkStyle";
	private static final String NONE_STYLE = "none";

	@Inject
	@Via("resource")
	@Optional
	private String id;
	
	@Inject
	@Via("resource")
	@Optional
	private String style;

	@Inject
	@Via("resource")
	@Optional
	private String titleForLinkStyle;

	@Inject
	@Optional
	@Via("resource")
	private String titleForNoneStyle;

	@Inject
	@Via("resource")
	@Optional
	private String display;

	@Inject
	@Via("resource")
	@Optional
	private String pagination;

	@Inject
	@Optional
	@Via("resource")
	@Default(booleanValues = false)
	private boolean showTitleCount;

	@Inject
	@Optional
	@Via("resource")
	@Default(booleanValues = false)
	private boolean hide;

	/**
	 * @return the title
	 */
	public String getTitle() {
		String title = null;
		if (!StringUtils.isBlank(style)) {
			if (style.equals(ICON_LINK_STYLE)) {
				title = titleForLinkStyle;
			} else if (style.equals(NONE_STYLE)) {
				title = titleForNoneStyle;
			}
		}
		return title;
	}
	
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return the style
	 */
	public String getStyle() {
		return style;
	}

	/**
	 * @return the titleForLinkStyle
	 */
	public String getTitleForLinkStyle() {
		return titleForLinkStyle;
	}

	/**
	 * @return the titleForNoneStyle
	 */
	public String getTitleForNoneStyle() {
		return titleForNoneStyle;
	}

	/**
	 * @return the display
	 */
	public String getDisplay() {
		return display;
	}

	/**
	 * @return the showTitleCount
	 */
	public boolean getTitleCount() {
		return showTitleCount;
	}

	/**
	 * @return the hide
	 */
	public boolean isHide() {
		return hide;
	}

	/**
	 * @return the pagination
	 */
	public String getPagination() {
		return pagination;
	}

}