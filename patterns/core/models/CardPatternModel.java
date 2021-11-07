package org.kp.patterns.core.models;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.kp.foundation.core.models.BaseModel;

/**
 * SlingModel class for Card Pattern component.
 * 
 * @author Tirumala Malladi
 *
 */
@Model(adaptables = { Resource.class,
		SlingHttpServletRequest.class }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class CardPatternModel extends BaseModel {

	private static final String ROLE_TEXT = "text";
	private static final String ROLE_GROUP = "group";

	@Inject
	@Via("resource")
	private String category;

	@Inject
	@Via("resource")
	private String title;

	@Inject
	@Via("resource")
	private String bodyText;

	@Inject
	@Default(booleanValues = false)
	@Via("resource")
	private boolean cardClickable;

	private String cardRole;

	/**
	 * Init method checks if the card is click-able and provides the appropriate
	 * role for the card
	 * 
	 */
	@PostConstruct
	public void init() {
		if (cardClickable) {
			cardRole = ROLE_TEXT;
		} else if (StringUtils.isNotBlank(category) && StringUtils.isNotBlank(title)
				&& StringUtils.isNotBlank(bodyText)) {
			cardRole = ROLE_GROUP;
		}
	}

	public String getCategory() {
		return category;
	}

	public String getTitle() {
		return title;
	}

	public String getBodyText() {
		return bodyText;
	}

	public boolean isCardClickable() {
		return cardClickable;
	}

	public String getCardRole() {
		return cardRole;
	}

}
