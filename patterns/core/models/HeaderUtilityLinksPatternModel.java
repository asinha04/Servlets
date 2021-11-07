package org.kp.patterns.core.models;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.kp.foundation.core.constants.GlobalConstants;
import org.kp.foundation.core.models.BaseModel;
import org.kp.foundation.core.models.MultiFieldLinkModel;
import org.kp.foundation.core.utils.WCMUseUtil;



/**
 * Sling Model class for Utility Component.
 * 
 * * @author Ravish Sehgal
 */
@Model(adaptables = { SlingHttpServletRequest.class }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)

public class HeaderUtilityLinksPatternModel extends BaseModel {

	@Inject
	@Via("resource")
	@Default(booleanValues=false)
	private boolean disableUtility;

	
	private List<MultiFieldLinkModel> utilityLinks = new ArrayList<>();
	private List<MultiFieldLinkModel> utilLinksList = new ArrayList<>();
	private static final String UTILITY_LINKS = "utilityLinks";
	private static final String HYPERLINK = "hyperlink";

	@Inject
	private SlingHttpServletRequest request;

	/**
	 * Init method populates the Utility Component for items that are created while
	 * authoring in dialog..
	 * 
	 */
	@PostConstruct
	public void init() {
		utilLinksList = WCMUseUtil.getLists(request, UTILITY_LINKS, GlobalConstants.EMPTY_STRING);
		for(MultiFieldLinkModel elements : utilLinksList) {
			elements.setLinkType(HYPERLINK);
			utilityLinks.add(elements);
		}
	}

	public List<MultiFieldLinkModel> getUtilityLinks() {
		return new ArrayList<>(utilityLinks);
	}

	public boolean isDisableUtility() {
		return disableUtility;
	}
	
}