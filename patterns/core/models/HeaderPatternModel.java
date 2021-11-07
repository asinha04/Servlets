package org.kp.patterns.core.models;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.kp.foundation.core.models.BaseModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.day.cq.wcm.api.WCMMode;
import java.util.HashMap;

/**
 * Sling Model class for Header Pattern Component.
 * 
 * * @author Mohan Joshi
 */
@Model(adaptables = { Resource.class,
		SlingHttpServletRequest.class }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class HeaderPatternModel extends BaseModel {

	private static final Logger LOGGER = LoggerFactory.getLogger(HeaderPatternModel.class);

	private static final String LOGGED_OUT = "logged-out";
	private static final String LOGGED_IN = "logged-in";
	private static final String TASKFLOW = "taskflow";
	private static final String HOME_PAGE_SIGN_ON = "homePage-signOn";
	private static final String NON_HOME_PAGE_CLASS = "non-homepage";
	private static final String LOGGED_IN_PAGE_CLASS = "authenticated theme-header-color-moss";
	private static final String TASK_FLOW_CLASS = "task-flow";

	@Inject
	@Via("resource")
	@Default(values = "logged-out")
	private String headerType;

	@Inject
	@Via("resource")
	@Default(values = "Sign In")
	private String signInLabel;

	@Inject
	@Via("resource")
	private String signInLink;

	@Inject
	@Via("resource")
	@Default(values = "Menu")
	private String menuLabel;

	@Inject
	SlingHttpServletRequest request;

	@Inject
	@Via("resource")
	@Default(booleanValues = false)
	private boolean disableSignOn;

	@Inject
	@Via("resource")
	@Default(booleanValues = false)
	private boolean enableAreaOfCare;

	private boolean authorModeAndLoggedOutOrHomePageSignOn = false;

	private boolean authorModeAndLoggedIn = false;

	private boolean publishModeNonTaskFlow = false;

	private boolean homePageSignOn = false;

	private boolean nonTaskFlow = false;

	private boolean authorMode = false;

	private boolean publishMode = false;

	private boolean authoreAndLoggedOutOrHomePageSignOn = false;

	private String headerClasses = NON_HOME_PAGE_CLASS;

	@PostConstruct
	protected void init() {
		LOGGER.debug("Inside header pattern sling model class");
		if (WCMMode.fromRequest(request) == WCMMode.EDIT || WCMMode.fromRequest(request) == WCMMode.PREVIEW) {
			authorMode = true;
		} else if (WCMMode.fromRequest(request) == WCMMode.DISABLED) {
			publishMode = true;
		}
	}

	/**
	 * @return header Type.
	 */
	public String getHeaderType() {
		return headerType;
	}

	/**
	 * @return the authorModeAndLoggedOut
	 */
	public boolean isAuthorModeAndLoggedOutOrHomePageSignOn() {
		if (authorMode && (headerType.equals(LOGGED_OUT) || headerType.equals(HOME_PAGE_SIGN_ON))) {
			authorModeAndLoggedOutOrHomePageSignOn = Boolean.TRUE;
		}
		return authorModeAndLoggedOutOrHomePageSignOn;
	}

	/**
	 * @return the authorModeAndLoggedIn
	 */
	public boolean isAuthorModeAndLoggedIn() {
		if (authorMode && headerType.equals(LOGGED_IN)) {
			authorModeAndLoggedIn = Boolean.TRUE;
		}
		return authorModeAndLoggedIn;
	}

	/**
	 * @return the publishModeNonTaskFlow
	 */
	public boolean isPublishModeNonTaskFlow() {
		if (publishMode && !headerType.equals(TASKFLOW)) {
			publishModeNonTaskFlow = Boolean.TRUE;
		}
		return publishModeNonTaskFlow;
	}

	/**
	 * @return the homePageSignOn
	 */
	public boolean isHomePageSignOn() {
		if (headerType.equals(HOME_PAGE_SIGN_ON)) {
			homePageSignOn = Boolean.TRUE;
		}
		return homePageSignOn;
	}

	/**
	 * @return the nonTaskFlow
	 */
	public boolean isNonTaskFlow() {
		if (!headerType.equals(TASKFLOW)) {
			nonTaskFlow = Boolean.TRUE;
		}
		return nonTaskFlow;
	}

	/**
	 * @return the authoreAndLoggedOutOrHomePageSignOn
	 */
	public boolean isAuthoreAndLoggedOutOrHomePageSignOn() {
		if (authorMode && (headerType.equals(LOGGED_OUT) || headerType.equals(HOME_PAGE_SIGN_ON))) {
			authoreAndLoggedOutOrHomePageSignOn = Boolean.TRUE;
		}
		return authoreAndLoggedOutOrHomePageSignOn;
	}

	/**
	 * @return header classes.
	 */
	public String getHeaderClasses() {
		HashMap<String, String> classesMap = new HashMap<>();
		classesMap.put(LOGGED_OUT, NON_HOME_PAGE_CLASS);
		classesMap.put(LOGGED_IN, LOGGED_IN_PAGE_CLASS);
		classesMap.put(TASKFLOW, TASK_FLOW_CLASS);
		classesMap.put(HOME_PAGE_SIGN_ON, "");
		if (authorMode){
			headerClasses = classesMap.get(headerType);
		}
		else if (publishMode && headerType.equals(TASKFLOW)) {
			headerClasses = TASK_FLOW_CLASS;	
		}
		else if (publishMode && isHomePageSignOn()) {
			headerClasses = "";	
		}
		return headerClasses;
	}

	/**
	 * @return Sign In Button Label.
	 */
	public String getSignInLabel() {
		return signInLabel;
	}

	/**
	 * @return Sign In Link Path.
	 */
	public String getSignInLink() {
		return signInLink;
	}

	/**
	 * @return Menu Button Label.
	 */
	public String getMenuLabel() {
		return menuLabel;
	}

	/**
	 * @return DisableSignOn Check box State.
	 */
	public boolean isDisableSignOn() {
		return disableSignOn;
	}

	/**
	 * @param disableSignOn
	 */
	public void setDisableSignOn(boolean disableSignOn) {
		this.disableSignOn = disableSignOn;
	}

	/**
	 * @return Area of Care Check box State.
	 */
	public boolean isEnableAreaOfCare() {
		return enableAreaOfCare;
	}

}