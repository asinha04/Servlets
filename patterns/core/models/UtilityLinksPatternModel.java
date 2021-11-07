package org.kp.patterns.core.models;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.kp.foundation.core.constants.GlobalConstants;
import org.kp.foundation.core.enums.PrintBehaviorEnum;
import org.kp.foundation.core.models.BaseModel;
import org.kp.foundation.core.models.MultiFieldLinkModel;
import org.kp.foundation.core.utils.LinkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Sling Model class for Utility Component.
 * 
 * * @author Tirumala Malladi
 */
@Model(adaptables = { SlingHttpServletRequest.class }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)

public class UtilityLinksPatternModel extends BaseModel {
	private static final Logger LOGGER = LoggerFactory.getLogger(UtilityLinksPatternModel.class);

	@Inject
	@Via("resource")
	@Default(booleanValues = false)
	private boolean hideEntireComponent;

	@Inject
	@Via("resource")
	@Default(booleanValues = false)
	private boolean hidePrintLink;

	@Inject
	@Via("resource")
	@Default(booleanValues = false)
	private boolean hidePrintIcon;

	@Inject
	@Via("resource")
	private String printLabel;

	@Inject
	@Via("resource")
	private String printBehavior;

	@Inject
	@Via("resource")
	private String customPrintPath;

	@Inject
	@Via("resource")
	@Default(booleanValues = false)
	private boolean hideHelpLink;

	@Inject
	@Via("resource")
	private String helpLabel;

	@Inject
	@Via("resource")
	private String helpLinkPath;

	@Inject
	@Via("resource")
	private String helpLinkTarget;
	
	private List<MultiFieldLinkModel> utiltyLinksList = new ArrayList<>();
	private boolean printBehaviorSelector;
	private boolean printBehaviorCustomPath;
	private boolean printClickEvent;
	private boolean printBehaviorDefault;

	@Inject
	private SlingHttpServletRequest request;

	/**
	 * Init method populates the Utility Component for items that are created while
	 * authoring in dialog..
	 * 
	 */
	@PostConstruct
	public void init() {
		if (null != printBehavior && printBehavior.length() > 0) {
			printBehaviorSelector = printBehavior.equalsIgnoreCase(PrintBehaviorEnum.PRINT_BEHAVIOR_SELECTOR.getValue())
					? true
					: false;
			printBehaviorCustomPath = printBehavior
					.equalsIgnoreCase(PrintBehaviorEnum.PRINT_BEHAVIOR_CUSTOM_PATH.getValue()) ? true : false;
			printClickEvent = printBehavior.equalsIgnoreCase(PrintBehaviorEnum.PRINT_BEHAVIOR_CUSTOM_EVENT.getValue())
					? true
					: false;
			printBehaviorDefault = printBehavior.equalsIgnoreCase(PrintBehaviorEnum.PRINT_BEHAVIOR_DEFAULT.getValue())
					? true
					: false;
		}
		utiltyLinksList = new ArrayList<MultiFieldLinkModel>();

		Resource linkRootRes = request.getResource().getChild(GlobalConstants.UTILITY_LINKS);
		if (linkRootRes == null) {
			LOGGER.info("utility Links for header are not set!");
			return;
		}
		Iterator<Resource> itr = linkRootRes.listChildren();
		while (itr.hasNext()) {
			Resource linkRes = itr.next();
			MultiFieldLinkModel utilityLinkModel = linkRes.adaptTo(MultiFieldLinkModel.class);
			utilityLinkModel.setLinkPath(LinkUtil.getRelativeURL(request, utilityLinkModel.getLinkPath()));
			utiltyLinksList.add(utilityLinkModel);
		}
	}

	public List<MultiFieldLinkModel> getUtiltyLinksList() {
		return new ArrayList<>(utiltyLinksList);
	}

	public boolean getPrintBehaviorSelector() {
		return printBehaviorSelector;
	}

	public boolean getPrintBehaviorCustomPath() {
		return printBehaviorCustomPath;
	}

	public boolean getPrintBehaviorDefault() {
		return printBehaviorDefault;
	}

	public boolean getHidePrintLink() {
		return hidePrintLink;
	}

	public boolean getHidePrintIcon() {
		return hidePrintIcon;
	}

	public String getPrintLabel() {
		return printLabel;
	}

	public String getCustomPrintPath() {
		return customPrintPath;
	}

	public boolean getHideHelpLink() {
		return hideHelpLink;
	}

	public String getHelpLabel() {
		return helpLabel;
	}

	public String getHelpLinkPath() {
		return LinkUtil.getRelativeURL(request, helpLinkPath);
	}

	public String getHelpLinkTarget() {
		return helpLinkTarget;
	}

	public boolean getPrintClickEvent() {
		return printClickEvent;
	}

	public boolean getHideEntireComponent() {
		return hideEntireComponent;
	}
}