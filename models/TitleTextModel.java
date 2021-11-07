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
public class TitleTextModel {
	/** The resource link Language for ADA. */
	@Inject
	private String link;

	/** The resource link. */
	@Inject
	private String linkText;
	
	@Inject
	private boolean openWindow;

	/** Context attribute for sightly href. Default value is uri. */
	private String context = GlobalConstants.HTL_CONTEXT_URI;


	public String getLink() {
		return link;
	}

	public String getLinkText() {
		return linkText;
	}

	public String getContext() { return context; }

	public void setLink(String link) {
		this.link = link;
	}

	public void setLinkText(String linkText) {
		this.linkText = linkText;
	}

	public void setContext(String context) { this.context = context; }


	@Override
	public String toString() {
		return "LinkModel2 [link=" + link + ", linkText="
				+ linkText + ", isOpenWindow=" + openWindow + ", context=" + context + "]";
	}

	public boolean isOpenWindow() {
		return openWindow;
	}

	public void setOpenWindow(boolean openWindow) {
		this.openWindow = openWindow;
	}

}