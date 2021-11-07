package org.kp.patterns.core.models;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.kp.foundation.core.constants.GlobalConstants;
import org.kp.foundation.core.models.BaseModel;
import org.kp.foundation.core.models.MultiFieldLinkModel;
import org.kp.foundation.core.utils.WCMUseUtil;
import com.day.cq.wcm.api.WCMMode;

/**
 * SlingModel class for Button Container component.
 * 
 * @author Tirumala Malladi
 *
 */
@Model(adaptables = { Resource.class,
		SlingHttpServletRequest.class }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ButtonContainerPatternModel extends BaseModel {

	private static final String REMOVE_MARGIN = "remove-bttm-margin";
	private static final String CENTER = "center";
	private static final String RIGHT = "right";

	@Inject
	@Via("resource")
	private String linkDisplay;

	@Inject
	@Via("resource")
	private String linkAlignment;

	@Inject
	@Via("resource")
	@Default(booleanValues = true)
	private boolean removeMargin;
	
	@Inject
	@Via("resource")
	@Default(booleanValues = false)
	private boolean viewShowMoreLink;

	private List<MultiFieldLinkModel> buttonLinksList = null;
	private List<MultiFieldLinkModel> buttonLinksListForMobile = null;
	private String removeMarginClass;
	private String showToggleHide;
     
	@PostConstruct
	public void init() {
		buttonLinksList = WCMUseUtil.getLists(request, "links", GlobalConstants.EMPTY_STRING);
		int i = 1;
		for (MultiFieldLinkModel linkModel : buttonLinksList) {
			if (linkModel.getLinkLabel() != null) {
				linkModel.setMobilePosition("." + getAutoGenId() + "-" + i);
				linkModel.setMobilePositionClass(getAutoGenId() + "-" + i);
			}
			if (linkModel.getLinkType()!= null && linkModel.getLinkType().equals("link")) {
				linkModel.setLinkType("button -tertiary");
			}
			if (null == linkModel.getLinkType()) {
				linkModel.setClasses(getClasses());
			}
			i++; 
		}
		if (linkAlignment != null
				&& (linkAlignment.equalsIgnoreCase(CENTER) || linkAlignment.equalsIgnoreCase(RIGHT))) {
			buttonLinksListForMobile = new ArrayList<>();
			ListIterator<MultiFieldLinkModel> listIterator = buttonLinksList.listIterator(buttonLinksList.size());
			while (listIterator.hasPrevious()) {
				buttonLinksListForMobile.add(listIterator.previous());
			}

		} else {
			buttonLinksListForMobile = buttonLinksList;
		}
		removeMarginClass = removeMargin ? "" : REMOVE_MARGIN;
		
		showToggleHide = (viewShowMoreLink && WCMMode.fromRequest(request) == WCMMode.DISABLED) ? "toggle-hide":"";
	}

	public String getLinkDisplay() {
		return linkDisplay;
	}

	public String getLinkAlignment() {
		return linkAlignment;
	}

	public List<MultiFieldLinkModel> getButtonLinksList() {
		return new ArrayList<>(buttonLinksList);
	}

	public List<MultiFieldLinkModel> getButtonLinksListForMobile() {
		return new ArrayList<>(buttonLinksListForMobile);
	
	}

	public String getRemoveMarginClass() {
		return removeMarginClass;
	}
	
	public boolean isViewShowMoreLink() {
		return viewShowMoreLink;
	}

	public String getShowToggleHide() {
		return showToggleHide;
	}

}