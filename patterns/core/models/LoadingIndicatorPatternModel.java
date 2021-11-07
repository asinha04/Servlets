package org.kp.patterns.core.models;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.kp.foundation.core.models.BaseModel;

import com.day.cq.wcm.api.WCMMode;

/**
 * Sling Model class for the Loading Indicator component.
 * 
 * * @author Mohan Joshi
 */

@Model(adaptables = { Resource.class,
		SlingHttpServletRequest.class }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class LoadingIndicatorPatternModel extends BaseModel {

	private static final String X_LARGE = "-xlarge";
	private static final String LARGE = "-large";
	private static final String SMALL = "-small";
	private static final String MEDIUM = "-medium";
	private static final String STYLE_VALUE = "display: none";
	private static final String STYLE_VALUE_DISPLAY = "text-align: center;";
	private static final String LOADING_INDICATOR = "loading-indicator";
	private static final String LOADING_INDICATOR_DISPLAY = "loading-indicator-display";

	@Inject
	@Via("resource")
	private String sizeVariations;

	@Inject
	@Via("resource")
	private String id;

	@Inject
	@Via("resource")
	private String title;

	@Inject
	@Via("resource")
	private String subtitle;

	@Inject
	@Via("resource")
	private String adaLabel;

	private boolean showText;

	private boolean showAdaLabel;

	
	@Inject
	SlingHttpServletRequest request;

	/**
	 * Init method sets the initial values in language indicator modal.
	 * 
	 */
	@PostConstruct
	public void init() {
		if (StringUtils.isNotBlank(sizeVariations)) {
			if (sizeVariations.equals(X_LARGE) || sizeVariations.equals(LARGE)) {
				showText = Boolean.TRUE;
			} else if (sizeVariations.equals(SMALL) || sizeVariations.equals(MEDIUM)) {
				showAdaLabel = Boolean.TRUE;
			}
		}
	}

	/**
	 * @return the sizeVariations
	 */
	public String getSizeVariations() {
		return sizeVariations;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		if (WCMMode.fromRequest(request) == WCMMode.EDIT) {
			id = "";
		}
		return id;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @return the subtitle
	 */
	public String getSubtitle() {
		return subtitle;
	}

	/**
	 * @return the adaLabel
	 */
	public String getAdaLabel() {
		return adaLabel;
	}

	/**
	 * @return the showText
	 */
	public boolean isShowText() {
		return showText;
	}

	/**
	 * @return the style
	 */
	public String getStyle() {
		String style = "";
		if (WCMMode.fromRequest(request) == WCMMode.DISABLED) {
			style = STYLE_VALUE;
		}
		else {
			style = STYLE_VALUE_DISPLAY;
		}
		return style;
	}

	/**
	 * @return the showAdaLabel
	 */
	public boolean isShowAdaLabel() {
		return showAdaLabel;
	}

	/**
	 * @return the styleClass
	 */
	public String getStyleClass() {
		String styleClass = "";
		if (WCMMode.fromRequest(request) != WCMMode.EDIT) {
			styleClass = LOADING_INDICATOR;
		}
		else {
			styleClass = LOADING_INDICATOR_DISPLAY;
		}
		return styleClass;
	}
}