package org.kp.foundation.core.models;

import javax.inject.Inject;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.kp.foundation.core.constants.GlobalConstants;

/**
 * The Class LinkModel.
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class LinkModel {
	/** The resource link Language for ADA. */
	@Inject
	private String linkLanguage;

	/** The resource link. */
	@Inject
	private String resourceLink;

	/** The display text. */
	@Inject
	private String displayText;

	/** Open In New Window. */
	@Inject
	private String target;

	/** Context attribute for sightly href. Default value is uri. */
	private String context = GlobalConstants.HTL_CONTEXT_URI;


	public String getLinkLanguage() {
		return linkLanguage;
	}

	public void setLinkLanguage(String linkLanguage) {
		this.linkLanguage = linkLanguage;
	}

	/**
	 * Gets the resource link.
	 * 
	 * @return the resource link
	 */
	public String getResourceLink() {
		return resourceLink;
	}

	/**
	 * Gets the display text.
	 * 
	 * @return the display text
	 */
	public String getDisplayText() {
		return displayText;
	}

	/**
	 * Gets the target.
	 *
	 * @return the target
	 */
	public String getTarget() {
		return target;
	}

	/**
	 * Gets the context value.
	 *
	 * @return the context value
	 */
	public String getContext() { return context; }

	/**
	 * Sets the resource link.
	 * 
	 * @param resourceLink
	 *            the new resource link
	 */
	public void setResourceLink(String resourceLink) {
		this.resourceLink = resourceLink;
	}

	/**
	 * Sets the display text.
	 * 
	 * @param displayText
	 *            the new display text
	 */
	public void setDisplayText(String displayText) {
		this.displayText = displayText;
	}

	/**
	 * Sets the target.
	 *
	 * @param target
	 *            the new target
	 */
	public void setTarget(String target) {
		this.target = target;
	}

	/**
	 * Sets the context.
	 *
	 * @param context
	 *            the new context
	 */
	public void setContext(String context) { this.context = context; }


	@Override
	public String toString() {
		return "LinkModel [linkLanguage=" + linkLanguage + ", resourceLink=" + resourceLink + ", displayText="
				+ displayText + ", target=" + target + ", context=" + context + "]";
	}

}