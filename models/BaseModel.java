package org.kp.foundation.core.models;

import java.util.Map;
import javax.inject.Inject;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.kp.foundation.core.utils.LinkUtil;
import org.kp.foundation.core.utils.SlingModelUtil;

@Model(adaptables = { SlingHttpServletRequest.class }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class BaseModel {
	@Inject
	private String id;
	@Inject
	private String classes;
	@Inject
	private String attrString;
	@Inject
	protected SlingHttpServletRequest request;
	@Inject
	private Map<String, String> attrMap;
	@Inject
	@Via("resource")
	private String linkLabel;
	@Inject
	@Via("resource")
	private String linkPath;
	@Inject
	@Via("resource")
	private String linkTarget;
	@Inject
	@Via("resource")
	private String linkType;
	@Inject
	@Via("resource")
	private boolean noLinkFollow;
	@Inject
	@Via("resource")
	private String alignment;

	@Inject
	private Resource resource;

	private static final String NO_FOLLOW_TAG = "nofollow";
	private static final String NO_FOLLOW_TAG_EMPTY = "";

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getClasses() {
		return classes;
	}

	public void setClasses(String classes) {
		this.classes = classes;
	}

	public String getAttrString() {
		return attrString;
	}

	public void setAttrString(String attrString) {
		this.attrString = attrString;
	}

	public Map<String, String> getAttrMap() {
		attrMap = SlingModelUtil.parseAttributeString(attrString);
		return attrMap;
	}

	public void setAttrMap(Map<String, String> attrMap) {
		this.attrMap = attrMap;
	}

	/**
	 * @return the linkLabel
	 */
	public String getLinkLabel() {
		return linkLabel;
	}

	/**
	 * @return the linkPath
	 */
	public String getLinkPath() {
		return LinkUtil.getRelativeURL(request, linkPath);
	}

	/**
	 * @return the linkTarget
	 */
	public String getLinkTarget() {
		return linkTarget;
	}

	/**
	 * @return the linkType
	 */
	public String getLinkType() {
		return linkType;
	}

	public String getNoLinkFollow() {
		return noLinkFollow ? NO_FOLLOW_TAG : NO_FOLLOW_TAG_EMPTY;
	}

	/**
	 * @return the Auto Generated ID
	 */
	public String getAutoGenId() {
		String prefix = "";
		String autoGenId = "";
		String[] resourceType = resource.getResourceType().split("/");
		if (resourceType != null) {
			prefix = resourceType[resourceType.length - 1];
		}
		autoGenId = prefix + "-" + String.valueOf(Math.abs(resource.getPath().hashCode() - 1));
		return autoGenId;
	}

	public String getAlignment() {
		return alignment;
	}

}