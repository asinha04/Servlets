
package org.kp.patterns.core.models;

import javax.inject.Inject;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;

/**
 * Bean class used in social icon model class
 * 
 * @author Tirumala Malladi
 *
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class SocialShareModel {

	@Inject
	private String iconType;
	@Inject
	private String socialNetwork;
	
	private String resourceUrl;
	private String ariaLabel;
		private String analyticsClick;
	
	public String toString() {
		return "SocialIcons [socialNetwork=" + socialNetwork + ", iconType=" + iconType + ", resourceUrl=" + resourceUrl
				+ ", ariaLabel=" + ariaLabel +  ", analyticsClick=" + analyticsClick +  "]";
	}

	public String getIconType() {
		return iconType;
	}

	public void setIconType(String iconType) {
		this.iconType = iconType;
	}

	public String getSocialNetwork() {
		return socialNetwork;
	}

	public void setSocialNetwork(String socialNetwork) {
		this.socialNetwork = socialNetwork;
	}

	public String getResourceUrl() {
		return resourceUrl;
	}

	public void setResourceUrl(String resourceUrl) {
		this.resourceUrl = resourceUrl;
	}

	public String getAriaLabel() {
		return ariaLabel;
	}

	public void setAriaLabel(String ariaLabel) {
		this.ariaLabel = ariaLabel;
	}

	public String getAnalyticsClick() {
		return analyticsClick;
	}

	public void setAnalyticsClick(String analyticsClick) {
		this.analyticsClick = analyticsClick;
	}

	
}
