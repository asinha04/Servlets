package org.kp.patterns.core.models;

import javax.inject.Inject;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.Via;
import org.kp.foundation.core.models.BaseModel;

/**
 * Sling Model class for the Modal Pattern component.
 * 
 * * @author Mohan Joshi
 */

@Model(adaptables = { Resource.class,
		SlingHttpServletRequest.class }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ModalPatternModel extends BaseModel {

	private static final String MODAL_CONTENT = "modal-content";
	private static final String MODAL_CONTAINER = "modal-container";
	private static final String TRUE_VALUE = "true";
	private static final String FALSE_VALUE = "false";
	private static final String FULL_SCREEN = "fullscreen";
	private static final String MODAL_PATTERN_FULLSCREEN = "modal-pattern modal-fullscreen";
	private static final String MODAL_PATTERN = "modal-pattern";

	@Inject
	@Via("resource")
	@Optional
	private String modalId;

	@Inject
	@Via("resource")
	@Optional
	private String modalSize;

	@Inject
	@Via("resource")
	@Optional
	private String modalColor;


	/**
	 * @return the modalId
	 */
	public String getModalId() {
		return modalId;
	}

	/**
	 * @return the modalSize
	 */
	public String getModalSize() {
		return modalSize;
	}


	/**
	 * @return the modalColor
	 */
	public String getModalColor() {
		return modalColor;
	}


	/**
	 * @return if modal would be full screen
	 */
	public String getFullScreen() {
		return FULL_SCREEN.equals(modalSize) ? TRUE_VALUE : FALSE_VALUE;
	}

	/**
	 * @return modal class
	 */
	public String getModalClass() {
		return FULL_SCREEN.equals(modalSize) ? MODAL_PATTERN_FULLSCREEN : MODAL_PATTERN;
	}

	/**
	 * @return modal content class
	 */
	public String getModalContentClass() {
		return FULL_SCREEN.equals(modalSize) ? MODAL_CONTENT : MODAL_CONTAINER;
	}
}