package org.kp.patterns.core.models;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.Via;
import org.kp.foundation.core.models.BaseModel;
import com.day.cq.wcm.api.Page;

/**
 * Sling Model class for Action Area Component.
 * 
 * * @author Mohan Joshi
 */
@Model(adaptables = { Resource.class,
		SlingHttpServletRequest.class }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ActionAreaPatternModel extends BaseModel {

	private static final String HEADER_STYLE_2 = "styling-2";
	private static final String HEADER_L1_CLASS = "l1";
	private static final String HEADER_L2_CLASS = "l2";
	private static final String AA_L2_HEADING = "l2-action-area-heading";
	private static final String TASK_FLOW = "task-flow";
	private static final String AA_TASK_FLOW_HEADING = "tf-action-area-heading";
	private static final String AA_ANALYTICS_TAG = "action area";
	private static final String AA_TASK_FLOW_BACKGROUND = "tf-area";
	private static final String AA_L2_BACKGROUND = "mh-area";
	private static final String FLOATING_BUTTON_CLASS = "floating-button";
	
	@Inject
	@Via("resource")
	@Named("title")
	@Optional
	private String headerText;

	@Inject
	@Via("resource")
	@Optional
	private String titlestyle;

	@Inject
	@Optional
	@Via("resource")
	@Default(booleanValues = false)
	private boolean hideTitle;

	@Inject
	@Optional
	@Via("resource")
	@Named("hidesubtitle")
	@Default(booleanValues = false)
	private boolean hideSubtitle;

	@Inject
	@Optional
	@Via("resource")
	@Default(booleanValues = false)
	private boolean hidecta;

	@Inject
	@Optional
	@Via("resource")
	@Default(booleanValues = false)
	private boolean hideActionArea;

	@Inject
	private Page currentPage;

	@Inject
	@Optional
	@Via("resource")
	private String headerTag;
	
	@Inject
	@Optional
	@Via("resource")
	@Default(booleanValues = false)
	private boolean floatingButton;
	
	/**
	 * Init method populates the Action area for items that are created while
	 * authoring in dialog..
	 * 
	 */
	@PostConstruct
	public void init() {
		headerText = StringUtils.isBlank(headerText) ? currentPage.getTitle() : headerText;
	}

	/**
	 * @return the title for Appointment Center Implementation to be removed
	 *         later
	 */
	public String getTitle() {
		return headerText;
	}

	/**
	 * @return the title
	 */
	public String getHeaderText() {
		return headerText;
	}

	/**
	 * @return the titlestyle
	 */
	public String getTitlestyle() {
		return titlestyle;
	}

	/**
	 * @return the hideTitle
	 */
	public boolean isHideTitle() {
		return hideTitle;
	}

	/**
	 * @return the hideSubtitle
	 */
	public boolean isHideSubtitle() {
		return hideSubtitle;
	}

	/**
	 * @return the hidecta
	 */
	public boolean isHidecta() {
		return hidecta;
	}

	/**
	 * @return the hideActionArea
	 */
	public boolean isHideActionArea() {
		return hideActionArea;
	}
	/**
	 * @return component classes
	 */
	public String getComponentClasses() {
		if (StringUtils.isNotBlank(titlestyle) && titlestyle.equals(HEADER_L1_CLASS)) {
			return HEADER_STYLE_2;
		} else if (StringUtils.isNotBlank(titlestyle) && titlestyle.equals(HEADER_L2_CLASS)) {
		  return AA_L2_HEADING;
		}else  {
		  return (StringUtils.isNotBlank(titlestyle) && titlestyle.equals(TASK_FLOW)) ? AA_TASK_FLOW_HEADING : StringUtils.EMPTY;
		}
	}
	
	/**
     * @return background color
     */
    public String getBackgroundColor() {
        if (StringUtils.isNotBlank(titlestyle) && titlestyle.equals(HEADER_L2_CLASS)) {
            return AA_L2_BACKGROUND;
        } else if (StringUtils.isNotBlank(titlestyle) && titlestyle.equals(TASK_FLOW)) {
          return AA_TASK_FLOW_BACKGROUND;
        }else  {
          return StringUtils.EMPTY;
        }
    }

	
	/**
	 * @return the headerTag
	 */
	public String getHeaderTag() {
		return headerTag;
	}
	
	/**
	 * @return the AnalyticsTag based on the title logic
	 */
	public String getAnalyticsTag() {
		if (StringUtils.isNotBlank(headerText)) {
			return !headerText.equals(currentPage.getTitle()) ? AA_ANALYTICS_TAG + ":" + headerText
					: AA_ANALYTICS_TAG;
		}
		return StringUtils.EMPTY;
	}

	/**
	 * @return true if floating button checkbox is enabled and style is selected
	 *         as L1
	 */
	public String getFloatingButtonClass() {
		String floatingButtonClass = null;
		if (StringUtils.isNotBlank(titlestyle) && titlestyle.equals(HEADER_L1_CLASS)) {
			if (floatingButton) {
				floatingButtonClass = FLOATING_BUTTON_CLASS;
			}
		}
		return floatingButtonClass;
	}
}