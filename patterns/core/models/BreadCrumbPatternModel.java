package org.kp.patterns.core.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.kp.foundation.core.constants.GlobalConstants;
import org.kp.foundation.core.utils.LinkUtil;
import org.kp.foundation.core.utils.WCMUseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;

/**
 * SlingModel class for Breadcrumb component.
 * 
 * @author Tirumala Malladi
 *
 */
@Model(adaptables = { SlingHttpServletRequest.class }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class BreadCrumbPatternModel {

	private static final Logger LOGGER = LoggerFactory.getLogger(BreadCrumbPatternModel.class);
	private static final String DESKTOP_ONLY_CSS_CLASS = "desktop-only";
	private static final String ENGLISH = "en";
	private static final String VISITOR_EN = "Visit";
	private static final String VISITOR_ES = "Visitar";
	
	@Inject
	SlingHttpServletRequest request;

	@Inject
	Page currentPage;

	@Inject
	@Via("resource")
	@Default(booleanValues = false)
	private boolean hideCurrentPage;

	@Inject
	@Via("resource")
	@Default(booleanValues = false)
	private boolean hideInMobileView;

	@Inject
	@Via("resource")
	@Default(intValues = 3)
	private int relParent;

	private String hideInMobileViewClass;
	
	
	private List<BreadCrumbModel> breadCrumbList = new ArrayList<>();
	
	/**
	 * Init method.
	 */
	@PostConstruct
	public void init() {
		LOGGER.debug("BreadCrumbPatternModel : init ");
		initBreadcrumbItems();
	}

	/**
	 * 
	 * 
	 * The title and path for the breadcrumb is populated from current page absolute
	 * parent's, iterated based on the depth of the current page starting from the relative
	 * parent level.
	 * 
	 * Relative parent level is retrieved from the dialog authoring value of
	 * property relParent.
	 * 
	 * @return the List values of breadcrumb.
	 */
	public void initBreadcrumbItems() {
		hideInMobileViewClass = hideInMobileView ? DESKTOP_ONLY_CSS_CLASS : GlobalConstants.EMPTY_STRING;
		breadCrumbList = new ArrayList<BreadCrumbModel>();
		if (relParent >= 3) {
			for (int i = relParent, j = 1; i < currentPage.getDepth() - 1; i++, j++) {
				if (!(j == 1 && hideCurrentPage)) {
					Page trail = currentPage.getParent(j - 1);
					BreadCrumbModel breadCrumbModel = getBreadCrumbModel(trail);
					if(breadCrumbModel!= null) {
						breadCrumbList.add(breadCrumbModel);
					}
				}
			}
		}
		if (breadCrumbList.isEmpty() || relParent < 3 && !hideCurrentPage) {
			breadCrumbList.add(getBreadCrumbModel(currentPage));
		}
		Collections.reverse(breadCrumbList);
		
	}
	/**
	 * 
	 * Breadcrumb tile is set with page navigationTile if exists, else with name or title.
	 * @param currentPage
	 * @return breadCrumModel
	 */

	private BreadCrumbModel getBreadCrumbModel(Page currentPage) {
		String title;
		BreadCrumbModel breadCrumbModel = null;
		if (currentPage != null && !currentPage.isHideInNav()) {
			title = currentPage.getNavigationTitle();
			if (StringUtils.isEmpty(title)) {
				title = StringUtils.isBlank(currentPage.getTitle()) ? currentPage.getName() : currentPage.getTitle();
			}
			breadCrumbModel= new BreadCrumbModel();
			breadCrumbModel.setPageTitle(title);
			breadCrumbModel.setPagePath(LinkUtil.getPathfieldURL(currentPage.getPath()));
	
		}
		return breadCrumbModel;
	}

	public List<BreadCrumbModel> getBreadCrumbList() {
		return new ArrayList<>(breadCrumbList);
	}

	public void setBreadCrumbList(List<BreadCrumbModel> breadCrumbList) {
	  this.breadCrumbList.addAll(breadCrumbList);
	}

	public String getHideInMobileViewClass() {
		return hideInMobileViewClass;
	}

	public boolean isHideCurrentPage() {
		return hideCurrentPage;
	}

	public void setHideCurrentPage(boolean hideCurrentPage) {
		this.hideCurrentPage = hideCurrentPage;
	}

	public String getVisitorLabel() {
		String language = WCMUseUtil.getCurrentLanguage(currentPage);
		return (language!= "" && language.equalsIgnoreCase(ENGLISH))?  VISITOR_EN: VISITOR_ES;
	}

	
	

}
